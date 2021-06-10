# 2조 과제 : 영화예매 시스템
![image](https://user-images.githubusercontent.com/80744278/119227825-d0ccf880-bb4a-11eb-9f53-99faf04a0f35.png)

---
# 서비스 시나리오

기능적 요구사항
1. 고객이 영화를 선택하여 예매한다.
1. 고객이 결제한다.
1. 예매가 완료되면 예매 내역이 해당 극장에 전달된다.
1. 영화가 등록되면 상영관이 지정된다.
1. 해당 극장은 해당영화의 관람관 좌석를 예매처리 한다.
1. 고객은 예매를 취소할 수 있다.
1. 예매를 취소하면 좌석예매도 취소처리한다.
1. 예매 현황은 카톡으로 알려 준다.(이벤트 발행)

비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않으면 예매를 할 수 없다.(Sync호출)
1. 장애격리
    1. 영화관리, 좌석관리 기능이 수행되지 않더라도 예매는 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 자신의 예매번호를 이용하여 예매내역을 확인할 수 있어야 한다  CQRS
    1. 예매상태가 바뀔때마다 카톡 등으로 알림을 줄 수 있어야 한다  Event driven

---
# 체크포인트

1. Saga
1. CQRS
1. Correlation
1. Req/Resp
1. Gateway
1. Deploy/ Pipeline
1. Circuit Breaker
1. Autoscale (HPA)
1. Zero-downtime deploy (Readiness Probe)
1. Config Map/ Persistence Volume
1. Polyglot
1. Self-healing (Liveness Probe)

---
# 분석/설계

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: http://www.msaez.io/#/storming/NsV4iwCaeqQOsNMyMgsTQ9Vaxgw2/share/c17526752f14346221c225bf4462d0df

### 이벤트 도출
![image](https://user-images.githubusercontent.com/80744278/119439561-98513880-bd5d-11eb-82b4-e90e886525a2.png)

### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/80744278/119439606-b028bc80-bd5d-11eb-8504-b76ca2b125e6.png)
- movie서비스에서 deleteMovie 기능 제거 (현재 사용 이벤트 없음)

### 액터, 커맨드 부착하여 읽기 좋게
![image](https://user-images.githubusercontent.com/80744278/119443846-3c8aad80-bd65-11eb-820e-4facedbd1f0f.png)

### 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/80744278/119444710-a48dc380-bd66-11eb-85d1-c72493cded18.png)

### 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/80744278/119444790-c38c5580-bd66-11eb-864e-d3e57cb8350d.png)

### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)
![image](https://user-images.githubusercontent.com/80908892/118935502-94509f80-b986-11eb-820d-7ad60bf637a0.png)

### 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증
- app서비스에서 pay서비스로가는 cancel(pub/sub) 중복 표현 제거(3개 -> 1개)
![image](https://user-images.githubusercontent.com/80908892/118985614-8e27e680-b9b9-11eb-9b81-fe8d1196887e.png)

- movie서비스에서 cutomer조회 기능 제거
- MovieSeat 어그리게이션에서 불필요 항목 제거 (movieId, payId)
- MovieSeat, Movie 어그리게이션에 예매번호(bookId) 및 상영관번호(screenId) 컬럼 추가
- Approved 클래스에 필요 항목 추가 (bookId, movieId)
- User의 View 채널은 App으로 통일
- 기타 정비 및 현행화
![image](https://user-images.githubusercontent.com/81547613/119284217-1fc57b80-bc7a-11eb-8ad5-59d8efba8487.png)

## 개인 추가 구현

제휴사에서 포인트 적립을 위한 alliance 서비스와 사용자가 자신의 Point 적립 내용을 볼 수 있도록 Point 서비스를 별도로 구현함

### CQRS적으로 구현

- 쓰기 : Pay 서비스를 이용해 Alliance 서비스에 쓰기를 진행
- 읽기 : Point 서비스에서 자신의 Point 이력을 조회한다.

![](point.png)

<!-- ![](alliance-point.png) -->

---
# 구현:

* 분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다

```maven
cd app
mvn spring-boot:run

cd pay
mvn spring-boot:run 

cd movie
mvn spring-boot:run  

cd theater
mvn spring-boot:run

cd notice
mvn spring-boot:run
```

## 개인 구현
```maven
cd point
mvn spring-boot:run
```

---
## DDD 의 적용
* 총 5개의 Domain 으로 관리되고 있으며, 예약관리(Reservation) , 결제관리(Approval), 상영영화관리(MovieManagement), 영화좌석관리(MovieSeat), 영화관리(Movie)으로 구성하였습니다. 

```java
package theater;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="reservations", path="reservations")
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>{

}
```

---
## 마이크로 서비스 호출 흐름

```
kubectl exec -it pod/siege -c siege -- /bin/bash
```
### 영화 등록 처리 : 관리자가 영화를 등록 합니다.

1. Movie 서비스를 이용해 영화를 등록한다.
2. 새로운 영화 등록 Event가 발생
3. Theater 서비스에서 해당 Event를 수신 후 데이터를 생성

- 쓰기 : Movie 서비스를 통해 Theater 서비스에 데이터를 넣어준다.
- 읽기 : Theater 서비스를 통해 등록된 영화목록을 확인할 수 있다.

* MOVIE 등록
```
http POST http://movie:8080/movieManagements movieId="MOVIE-00001" title="어벤져스" status="RUNNING"
http POST http://movie:8080/movieManagements movieId="MOVIE-00002" title="아이언맨" status="RUNNING"
http POST http://movie:8080/movieManagements movieId="MOVIE-00003" title="토르" status="WAITING"
```

* Movie 서비스 내 MOVIE_MANAGEMENTS (영화관리) 테이블 데이터 생성 완료
![](new-movie-registe.png)

* Theater 서비스 내 MOVIES (좌석배정) 테이블 데이터 생성 완료

![](new-theater.png)

### 영화 예매 처리 : 고객이 영화와 좌석번호를 선택하여 예매를 요청하면 결재요청(pay)은 (req/res)되며, 결재 성공 시 극장(theater) 상영관 예매가 완료 됩니다.

* 예매 요청
```
http POST http://app:8080/reservations/new bookId="B1001" customerId="C1001" movieId="MOVIE-00001" seatId="A-1"
http POST http://app:8080/reservations/new bookId="B1002" customerId="C1002" movieId="MOVIE-00002" seatId="B-1"
http POST http://app:8080/reservations/new bookId="B1003" customerId="C1003" movieId="MOVIE-00003" seatId="C-1"
```

* PAY 서비스 내 APPROVALS (승인내역) 테이블 데이터 생성 완료

![](new-reservations.png)

* App 서비스 내 RESERVATIONS (예매내역) 테이블 데이터 생성 완료

![](new-pay-approvals.png)

## 승인(req/res)완료 후 이벤트

- 결재 승인 이벤트가 발생
- Point 서비스 - 포인트 적립
- Theater 서비스 - 좌석 예약
#### [개인과제] 결재가 완료 됬을시 포인트를 적립해준다.(pub/sub)

![](new-point.png)

* THEATER 서비스 내 MOVIE_SEATS (좌석배정내역) 테이블 데이터 생성 완료

![](new-movieSeats.png)

### 영화 예매 취소 처리 : 고객이 특정 예약을 취소하면 결재(pay) 및 해당 극장(theater) 예약이 취소 됩니다.

- 고객이 예약 취소 요청을 보낸다.
- App 서비스에서 Cancel 이벤트를 발생
- Pay 서비스에서 Cancel 이벤트를 수신 후 취소가 됐다는 이벤트를 발생
- App 서비스에서 결재 취소 이벤트를 수신 후 status를 N으로 비활성화
- Point 서비스에서 결재 취소 이벤트를 수신 후 Point 이력을 삭제
- Theater 서비스에서 결재 취소 이벤트 수신 후 좌석을 cancel상태로 바꿈

* 예약 취소 요청
```
http DELETE http://app:8080/reservations/B1001    
http DELETE http://app:8080/reservations/B1002
http DELETE http://app:8080/reservations/B1003
```

![](new-delete.png)

* PAY 서비스 내 APPROVALS (승인내역) 테이블 데이터 삭제 완료

![](new-delete-pay.png)

* APP 서비스 내에서 status를 N으로 갱신

![](new-cancel-reservation.png)

#### [개인과제] Point 서비스에서는 포인트 적립 내용을 삭제한다.

![](new-delete-point.png)

* THEATER 서비스 내 MOVIE_SEATS (좌석배정내역) 테이블 STATUS 갱신 완료

![](new-canceled-theater.png)

---
## Gateway 적용

* 게이트웨이의 설정은 8080이며, 예매관리/결재관리 및 극장정보등 마이크로서비스에 대한 일원화 된 접점을 제공하기 위한 설정이다.
```
app 서비스 : 8081
pay 서비스 : 8082
movie 서비스 : 8083
theater 서비스 : 8084
```

* gateway > applitcation.yml 설정

![image](https://user-images.githubusercontent.com/81547613/119323918-2d9af100-bcba-11eb-8378-1338b6337b18.png)

### 개인 구현
```
point 서비스 : 8085
```

![](gateway-config.png)



## Gateway 테스트

* Gateway 테스트는 다음과 같이 확인하였다.

> kubectl get all

![image](https://user-images.githubusercontent.com/81547613/119357273-5505b400-bce2-11eb-854d-12930219b3ad.png)

> External IP:8080 을 통한 API호출을 통해 http://app:8080 으로부터 응답을 받음

![image](https://user-images.githubusercontent.com/81547613/119356977-fdffdf00-bce1-11eb-8dc6-b098b5ef0975.png)

### 개인과제 구현

point 서비스까지 모두 떠 있는 것을 확인할 수 있다.

![](new-kubenetes-gateway.png)

> http://point:8080 을 통한 API호출을 통해 응답을 받는 것을 확인할 수 있다.

![](new-point-http.png)
---
## 동기식 호출 과 Fallback 처리

영화 예약(app)후 결재처리(pay) 간의 호출은 동기식 일관성을 유지하기 위하여 FeignClient 를 이용하여 호출하도록 한다. 

### 동기식 호출 영화 예약 후 결재 처리
#### Local 환경에서 확인

* App Service는 실행 상태 / Pay Service는 중단 상태
```http
http POST http://localhost:8081/reservations/new bookId="B1001" customerId="C1001" movieId="MOVIE-00001" seatId="A-1"
```

* Response Message : Pay disapproved가 뜨면서 Pay 서비스를 확인하라는 문구를 보낸다.

```
http POST http://app:8080/reservations/new bookId="B1001" customerId="C1001" movieId="MOVIE-00001" seatId="A-1"
```

```
HTTP/1.1 400
Connection: close
Content-Type: application/json;charset=UTF-8
Date: Mon, 24 May 2021 05:30:15 GMT
Transfer-Encoding: chunked

{
    "details": "uri=/reservations/new",
    "message": "[B1001] Pay disapproved, You must confirm Pay Service",
    "timestamp": "2021-05-24T05:30:15.807+0000"
}
```

```
- Fallback 처리 결과 
- Spring Boot Log Message
2021-05-24 14:02:11.708 DEBUG 25811 --- [strix-pay-api-1] theater.external.ApprovalService         : [ApprovalService#paymentRequest] ---> GET http://localhost:8082/approved?bookId=B1001&movieId=MOVIE-00001&seatId=A-1 HTTP/1.1
2021-05-24 14:02:11.714 DEBUG 25811 --- [strix-pay-api-1] theater.external.ApprovalService         : [ApprovalService#paymentRequest] <--- ERROR ConnectException: Connection refused (Connection refused) (5ms)
2021-05-24 14:02:11.719  INFO 25811 --- [strix-pay-api-1] t.f.ApprovalServiceFallbackFactory       : ========= FallbackFactory called: Confirm Pay Service =========
2021-05-24 14:02:11.719  INFO 25811 --- [strix-pay-api-1] t.f.ApprovalServiceFallbackFactory       : Error Message: Connection refused (Connection refused) executing GET http://localhost:8082/approved?bookId=B1001&movieId=MOVIE-00001&seatId=A-1
2021-05-24 14:02:11.721  INFO 25811 --- [nio-8081-exec-1] theater.ReservationController            : ========= Pay didn't Approve. Confirm Pay Service.=========
```

#### 쿠버네티스에서 확인

Pay 서비스가 정상이 아니므로 Pay 서비스를 확인하라는 문구를 보여준다.

![](new-hystrix.png)

---
## 비동기식 호출 / 장애격리 / 성능

영화 예매 취소(app) 요청과 결재 취소(pay), 해당 극장 영화/좌석 취소(theater)는 비동기식 처리이므로, 다른 시스템의 상태가 영화 예매 취소(app)의 서비스 호출에 영향이 없도록 구성한다.
즉, 결재 취소 시스템이 비정상일 경우 영화/좌석은 취소되지 않고, 결재 시스템이 재기동 되면 이벤트를 구독하여 취소시킨다.

* 영화 예매 취소 전 영화/좌석 예약 상태

```json
{
    "_links": {
        "movieSeat": {
            "href": "http://localhost:8084/movieSeats/9"
        },
        "self": {
            "href": "http://localhost:8084/movieSeats/9"
        }
    },
    "bookId": "B1004",
    "screenId": "어벤져스_상영관",
    "seatId": "D-1",
    "status": "Reserved"
}
```

* PAY 서비스 다운
```
2021-05-24 15:04:04.998  INFO 8732 --- [       Thread-8] o.s.i.monitor.IntegrationMBeanExporter   : Summary on shutdown: _org.springframework.integration.errorLogger.handler
2021-05-24 15:04:05.000  INFO 8732 --- [       Thread-8] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2021-05-24 15:04:05.003  INFO 8732 --- [       Thread-8] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
[INFO] ----일괄 작업을 끝내시겠습니까 (Y/N)? y

C:\Dev-Workspace\reqres_theater\pay>
```

* 영화 예매 취소 요청
```
>http DELETE http://localhost:8081/reservations/B1004
HTTP/1.1 204
Date: Mon, 24 May 2021 06:05:26 GMT
```

* 극장 영화/좌석 예약 상태 확인 : 예약취소되지 않음 (Reserved)

```json
{
    "_links": {
        "movieSeat": {
            "href": "http://localhost:8084/movieSeats/9"
        },
        "self": {
            "href": "http://localhost:8084/movieSeats/9"
        }
    },
    "bookId": "B1004",
    "screenId": "어벤져스_상영관",
    "seatId": "D-1",
    "status": "Reserved"
}
```

---
#운영
---
## 소스 패키징

### 클라우드 배포를 위한 패키징 작업
```
cd app
mvn clean && mvn package
cd ..
cd pay
mvn clean && mvn package
cd ..
cd movie
mvn clean && mvn package
cd ..
cd theater
mvn clean && mvn package
cd ..
cd gateway
mvn clean && mvn package
cd ..
```
	
![image](https://user-images.githubusercontent.com/80744278/119227564-8a2ace80-bb49-11eb-88ce-e2df20a617a4.png)

---
## 클라우드 배포/운영 파이프라인

### aws 클라우드에 배포하기 위한 주요 정보 설정

```
클러스터 명 : user02-eks
```

### aws config - IAM => 엑세스관리 > 사용자
```json
$ aws configure
AWS Access Key ID [None]: AKIAQYU2ROSK4JEJH2VM
AWS Secret Access Key [None]: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Default region name [None]: ap-northeast-2
Default output format [None]: json

#설정확인
$cat ~/.aws/config
[default]
region = ap-northeast-2
output = json

#인증정보 확인
$cat ~/.aws/credentials
[default]
aws_access_key_id = AKIAQYU2ROSK4JEJH2VM
aws_secret_access_key = ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
```

### EKS생성 (클러스터생성 및 설정)
```json
$eksctl create cluster --name team02-eks --version 1.17 --nodegroup-name standard-workers --node-type t3.medium --nodes 4 --nodes-min 1 --nodes-max 4
$kubectl get nodes

$aws eks --region ap-northeast-2 update-kubeconfig --name team02-eks
$kubectl config current-context
$kubectl get all
```

### 네임스페이스 생성

```shell
# Spring Cloud 프로젝트 배포를 위한 namespace 
kubectl create ns user12
# Kafka를 배포하기 위한 namespace
kubectl create ns kafka
# 생성된 namespace 목록을 가져온다.
kubectl get ns
```

![image](https://user-images.githubusercontent.com/80908892/119248843-d2d89b00-bbce-11eb-8359-6d6af7ba5fb1.png)

### 기본 namespace설정 및 서비스확인

```sh
kubectl config set-context $(kubectl config current-context) --namespace=team02
kubectl get all
```

### helm(3.X설치)

> helm 설치 스크립트를 다운 받음

```shll
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh
```

```shell
root@labs-1227910482:/home/project/team# 
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 11248  100 11248    0     0  33981      0 --:--:-- --:--:-- --:--:-- 33981
```

> 다운 받은 스크립트 실행

```shell
chmod 700 get_helm.sh
./get_helm.sh
```

```shell
Downloading https://get.helm.sh/helm-v3.5.4-linux-amd64.tar.gz
Verifying checksum... Done.
Preparing to install helm into /usr/local/bin
helm installed into /usr/local/bin/helm
```

> repository 추가

```shell
helm repo add incubator https://charts.helm.sh/incubator
```

```shell
WARNING: Kubernetes configuration file is group-readable. This is insecure. Location: /root/.kube/config
WARNING: Kubernetes configuration file is world-readable. This is insecure. Location: /root/.kube/config
"incubator" has been added to your repositories
root@labs-1227910482:/home/project/team# helm repo update
WARNING: Kubernetes configuration file is group-readable. This is insecure. Location: /root/.kube/config
WARNING: Kubernetes configuration file is world-readable. This is insecure. Location: /root/.kube/config
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "incubator" chart repository
Update Complete. ⎈Happy Helming!⎈
```

### kafka설치

```shell
# kafka namespace에 kafka를 설치해준다.
helm install my-kafka --namespace kafka incubator/kafka
# kafka namespace에 kafka가 정상적으로 설치돼 있는지 확인
kubectl get all -n kafka
```

![image](https://user-images.githubusercontent.com/81547613/119323673-ee6ca000-bcb9-11eb-8564-863fe6384430.png)

### ECR 생성

```shell
aws ecr create-repository --repository-name user12-app --region ap-northeast-1
aws ecr create-repository --repository-name user12-movie --region ap-northeast-1
aws ecr create-repository --repository-name user12-pay --region ap-northeast-1
aws ecr create-repository --repository-name user12-theater --region ap-northeast-1
aws ecr create-repository --repository-name user12-gateway --region ap-northeast-1
aws ecr create-repository --repository-name user12-alliance --region ap-northeast-1
aws ecr create-repository --repository-name user12-point --region ap-northeast-1
```

### 서비스들 Docker를 이용해 Build 진행하기

```shell
docker build -t 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-app:v1 ./app
docker build -t 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-movie:v1 ./movie
docker build -t 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-pay:v1 ./pay
docker build -t 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-theater:v1 ./theater
docker build -t 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-gateway:v1 ./gateway
docker build -t 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-alliance:v1 ./alliance
docker build -t 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-point:v1 ./point
```

### ECR에 이미지들을 Push

```shell
docker push 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-app:v1
docker push 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-movie:v1
docker push 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-pay:v1
docker push 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-theater:v1
docker push 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-gateway:v1
docker push 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-alliance:v1
docker push 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-point:v1
```



### deployment.yml로 서비스 배포

### 각 마이크로 서비스를 yml 파일을 사용하여 배포 합니다.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app
  labels:
    app: app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: app
  template:
    metadata:
      labels:
        app: app
    spec:
      containers:
        - name: app
          image: 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user02-app:v1
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5

```

## 배포처리

### 서비스 배포

```shell
kubectl apply -f ./app/kubernetes --namespace=user12
kubectl apply -f ./pay/kubernetes --namespace=user12
kubectl apply -f ./movie/kubernetes --namespace=user12
kubectl apply -f ./theater/kubernetes --namespace=user12
kubectl apply -f ./alliance/kubernetes --namespace=user12
kubectl apply -f ./point/kubernetes --namespace=user12
```

### Gateway Service 배포
```shell
cd gateway
kubectl create deploy gateway --image=052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user02-gateway:v1
# gateway는 외부에서 접속이 가능하게 LoadBalancer형태로 deploy한다.
kubectl expose deploy gateway --type="LoadBalancer" --port=8080
```
![](new-kubenetes-gateway.png)
---
## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현하였습니다.

### Hystrix 를 설정:  

요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정


```yaml
# application.yml
feign:
  pay-api:
    url: http://localhost:8082
  httpclient:
    connection-timeout: 1
  hystrix:
    enabled: true
  client:
    config:
      default:
        loggerLevel: BASIC
hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610
```

## 부하테스트


* Siege 리소스 생성

```
kubectl run siege --image=apexacme/siege-nginx 
```

* 실행

```
kubectl exec -it pod/siege-5459b87f86-hlfm9 -c siege -- /bin/bash
```

* 부하 실행

```bash
siege -c200 -t60S -r10 -v --content-type "application/json" http://app:8080/isolations
```

#### 부하를 진행하기 위한 소스 추가

1. App 서비스에 isolations로 요청이 들어온다.
2. App 서비스에서 Pay 서비스의 isolations로 요청을 보낸다.

> ReservationController

```java
public class ReservationController {
    @GetMapping("/isolations")
    String isolation() {
        return approvalService.isolation();
    }
}
```

> ApprovalService

```java
@FeignClient(name = "pay-api", url = "${feign.pay-api.url}",
        configuration = HttpConfiguration.class,
        fallbackFactory = ApprovalServiceFallbackFactory.class)
public interface ApprovalService {

    @GetMapping("/approved")
    Approval paymentRequest(@RequestParam("bookId") String bookId
            , @RequestParam("movieId") String movieId
            , @RequestParam("seatId") String seatId
    );

    @GetMapping("/isolation")
    String isolation();
}
```

> ApprovalServiceFallbackFactory

```java
public class ApprovalServiceFallbackFactory implements FallbackFactory<ApprovalService> {

    @Override
    public ApprovalService create(Throwable cause) {
        return new ApprovalService() {

            @Override
            public String isolation() {
                return "fallback";
            }
        };
    }
}
```

#### Pay 서비스

> ApprovalController

```java
@RequestMapping(value = "/isolation", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
public String isolation(HttpServletRequest request, HttpServletResponse response) throws Exception {    
    Random rng = new Random();
    long loopCnt = 0;

    while (loopCnt < 100) {
        double r = rng.nextFloat();
        double v = Math.sin(Math.cos(Math.sin(Math.cos(r))));
        System.out.println(String.format("r: %f, v %f", r, v));
        loopCnt++;
    }
    
    return "real";
}
```

- 부하 발생하여 CB가 발동하여 요청 실패처리하였고, 밀린 부하가 pay 서비스에서 처리되면서 
다시 pay에서 서비스를 받기 시작 합니다

![](new-stress.png)

- report

![](new-stress-result.png)

---
## 오토스케일 아웃

### deployment.yml 설정

```yaml
spec:
  containers:
    - name: movie
      image: 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user02-movie:v1
      ports:
        - containerPort: 8080
      readinessProbe:
        httpGet:
          path: '/actuator/health'
          port: 8080
        initialDelaySeconds: 10
        timeoutSeconds: 2
        periodSeconds: 5
        failureThreshold: 10
      livenessProbe:
        httpGet:
          path: '/actuator/health'
          port: 8080
        initialDelaySeconds: 120
        timeoutSeconds: 2
        periodSeconds: 5
        failureThreshold: 5
      resources:
        limits:
          cpu: "500m"
        requests:
          cpu: "200m"
```

### Auto Scale 설정

```shell
kubectl autoscale deployment movie --cpu-percent=50 --min=1 --max=10
```

![](new-hpa.png)

### 부하기능 API 추가

```java
@RestController
public class MovieManagementController {


    @GetMapping("/hpa")
    public String getHPA() {
        Random rng = new Random();
        long loopCnt = 0;

        while (loopCnt < 100) {
            double r = rng.nextFloat();
            double v = Math.sin(Math.cos(Math.sin(Math.cos(r))));
            System.out.println(String.format("r: %f, v %f", r, v));
            loopCnt++;
        }

        return "hpa";
    }
}
```

### 부하기능 API 수행

> 명령어

```shell
# stresstool Container에 접속
kubectl exec -it pod/stresstool-85fb8c78db-77fb4 -- /bin/bash
# stresstool Container에서 siege를 사용해 Stress Test를 시작해 Scale Out이 정상적으로 실행되는지 확인
siege -c60 -t60S -v http://movie:8080/hpa
```

![](new-hap-result.png)

### 오토스케일링에 대한 모니터링:

![](new-autoscale.png)
---
## Config Map

### config map 생성 후 조회

> Config Map 생성

```shell
kubectl create configmap mid-cm --from-literal=mid=pg0000123
configmap/mid-cm created
```

> Config Map 확인

```shell
kubectl get cm
NAME     DATA   AGE
mid-cm   1      42s
```

### Deployment.yml 설정

```yaml
env:
- name: MID
  valueFrom:
    configMapKeyRef:
      name: mid-cm
      key: mid
```

#### 전체

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pay
  labels:
    app: pay
spec:
  replicas: 1
  selector:
    matchLabels:
      app: pay
  template:
    metadata:
      labels:
        app: pay
    spec:
      containers:
        - name: pay
          image: 879772956301.dkr.ecr.ap-northeast-1.amazonaws.com/user12-pay:v1
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
          env:
            - name: MID
              valueFrom:
                configMapKeyRef:
                  name: mid-cm
                  key: mid
```

### apllication.yml 설정

```yaml
pay:
  mid: ${MID}
```

### Application 읽기

ApprovalController.java

```java
@Value("${pay.mid}")
String mid;

@GetMapping("/cm")
public String getCM() {
    return mid;
}
```

### 테스트 수행

* pay:8080/cm 호출 시 키 값(pg0000123) 정상 

```http
root@stresstool-85fb8c78db-77fb4:/# http pay:8080/cm
HTTP/1.1 200 
Content-Length: 9
Content-Type: text/plain;charset=UTF-8
Date: Mon, 24 May 2021 15:04:29 GMT

pg0000123
```


---

## Persistance Volume

### PVC 관련 YAML 생성

```shell
ll

total 24
drwxr-xr-x  2 root root 6144 May 25 02:12 ./
drwxr-xr-x 10 root root 6144 May 25 01:59 ../
-rw-r--r--  1 root root  872 May 25 02:12 efs-provisioner-deploy.yaml
-rw-r--r--  1 root root  126 May 25 01:59 efs-provisioner-storageclass.yaml
-rw-r--r--  1 root root 1330 May 25 01:59 rbac.yaml
-rw-r--r--  1 root root   89 May 25 01:59 service_account.yaml
```

```shell
cat efs-provisioner-deploy.yaml 
```

```yaml
kind: Deployment
apiVersion: apps/v1
metadata:
  namespace: team02
  name: efs-provisioner
spec:
  selector:
    matchLabels:
      app: efs-provisioner
  replicas: 1
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: efs-provisioner
    spec:
      serviceAccount: efs-provisioner
      containers:
        - name: efs-provisioner
          image: quay.io/external_storage/efs-provisioner:v2.4.0
          env:
            - name: FILE_SYSTEM_ID
              value: fs-1d2e9d7d
            - name: AWS_REGION
              value: ap-northeast-2
            - name: PROVISIONER_NAME
              value: my-aws.com/aws-efs
          volumeMounts:
            - name: pvcs
              mountPath: /pvcs
      volumes:
        - name: pvcs
          nfs:
            server: fs-1d2e9d7d.efs.ap-northeast-2.amazonaws.com
            path: /
```

### YAML 파일 적용 및 SC, PVC 상태확인

```shell
kubectl apply -f .
```

```shell
serviceaccount/efs-provisioner created
clusterrole.rbac.authorization.k8s.io/efs-provisioner-runner created
clusterrolebinding.rbac.authorization.k8s.io/run-efs-provisioner created
role.rbac.authorization.k8s.io/leader-locking-efs-provisioner created
rolebinding.rbac.authorization.k8s.io/leader-locking-efs-provisioner created
deployment.apps/efs-provisioner configured
storageclass.storage.k8s.io/efs-provisioner configured
```

```shell
kubectl get sc
```

```shell
NAME              PROVISIONER             RECLAIMPOLICY   VOLUMEBINDINGMODE      ALLOWVOLUMEEXPANSION   AGE
aws-efs           my-aws.com/aws-efs      Delete          Immediate              false                  10h
efs-provisioner   user02-efs              Delete          Immediate              false                  11h
gp2 (default)     kubernetes.io/aws-ebs   Delete          WaitForFirstConsumer   false                  21h
```

> 서비스 확인

```shell
kubectl get pvc
```

```
NAME      STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
aws-efs   Bound    pvc-0f90e1d5-8edd-491d-bcfe-4c8f86a606b3   1Mi        RWX            aws-efs        10h
```

### PV 적용한 POD(mypod) 내 텍스트파일 생성
```
root@labs-1227910482:/home/project/reqres_theater/movie/kubernetes# kubectl exec -it pod/mypod -- bin/bash
```
```
root@mypod:/mnt/aws# echo "team02 message.!!!" >> msg.txt
echo "team02 message.vi!" >> msg.txt
root@mypod:/mnt/aws# ls
msg.txt
```
```
root@mypod:/mnt/aws# cat msg.txt 
team02 message.vi!
```

### MOVIE POD 내 텍스트파일 생성여부 확인

```shell
kubectl exec -it pod/movie-67d777cc7d-78j9r -- bin/sh
```

```shell
/ # ls
app.jar  dev      home     media    opt      root     sbin     sys      usr
bin      etc      lib      mnt      proc     run      srv      tmp      var
/ # cd mnt
/mnt # ls
aws
/mnt # cd aws
```

```shell
/mnt/aws # ls
msg.txt
/mnt/aws # cat msg.txt 
team02 message.vi!
```

---
## 헥사고날 아키텍처 다이어그램 도출 (Polyglot)
![image](https://user-images.githubusercontent.com/80744278/119462464-b7f75980-bd7b-11eb-8351-65499d0907bf.png)

## 폴리글랏 퍼시스턴스

* MOVIE 시스템은 HSQLDB 사용
```
위치 : /reqres_theater/movie/pom.xml
```

Dependency 추가

![](new-dependency.png)
```
위치 : /reqres_theater/movie/src/main/resources/application.yml
```

![](new-movie-hsqldb.png)

* 다른 시스템은 모두 H2DB 사용
```
위치 : /reqres_theater/app/pom.xml
```
![image](https://user-images.githubusercontent.com/80744278/119217655-83369880-bb16-11eb-95f9-588fcfcebcbe.png)



---
## Zero-downtime deploy (Readiness Probe)

> 서비스 가능여부 확인

### 현재 POD 개수 확인 (2개)

```shell
NAME                         READY   STATUS    RESTARTS   AGE
pod/movie-5f6c5757cb-5q2j8   1/1     Running   0          7m43s
pod/movie-5f6c5757cb-m7brh   1/1     Running   0          5m13s
pod/multitool                1/1     Running   1          10h
pod/order-666f44f458-rj6tt   1/1     Running   1          10h
pod/stresstool               1/1     Running   0          28m
```

### deployment.yml에 readiness 옵션을 추가 

![](new-readness.png)

### 부하 기능 API 추가
```java
- MovieManagementController.java
#####################################################
@GetMapping("/serviceAddress")
public String getServiceAddress () {
    String serviceAddress = null;
    if (serviceAddress == null) {
        serviceAddress = findMyHostname() + "/" + findMyIpAddress();
    }
    return serviceAddress;
}

private String findMyHostname() {
    try {
        return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
        return "unknown host name";
    }
}

private String findMyIpAddress() {
    try {
        return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
        return "unknown IP address";
    }
}
#####################################################
```

### 부하 기능 api 수행

```shell
kubectl exec -it pod/siege -c siege -- /bin/bash
```


```
while true; do curl movie:8080/serviceAddress; echo echo ""; sleep 1; done
```


## Readiness 테스트 수행결과

* 서비스가 Round Robin 형태로 번갈아 가면서 요청을 처리함

```shell
movie-5f6c5757cb-5q2j8/10.1.1.211echo 
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
```

### Movie 서비스 하나 정지

```http
http http://movie:8080/actuator/health
http put http://movie:8080/actuator/down 
```

서비스를 하나 정지하면 그때부터는 한 서비스에서만 데이터를 처리하기 시작한다.

```shell
movie-5f6c5757cb-m7brh/10.1.1.212echo  
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-5q2j8/10.1.1.211echo
movie-5f6c5757cb-m7brh/10.1.1.212echo  
======================================
# movie-5f6c5757cb-5q2j8/10.1.1.211echo서비스 완전 종료
======================================
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
movie-5f6c5757cb-m7brh/10.1.1.212echo
```


---
## Self-Healing (Liveness Probe)


### deployment.yml 에 Liveness Probe 옵션 추가


```yaml
livenessProbe:
    httpGet:
        path: '/actuator/health'
        port: 8080
    initialDelaySeconds: 120
    timeoutSeconds: 2
    periodSeconds: 5
    failureThreshold: 5
```

* Movie 시스템의 health를 down 시키면 READY가 0/1로 내려갔다가 RESTART가 1로 올라가며 다시 시스템이 살아나는 것을 확인함

![image](https://user-images.githubusercontent.com/80908892/119315005-53bb9380-bcb0-11eb-868f-46da0ae46814.png)

---
※ 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW
