# 서비스 동작 확인
- app servie
```sh
http http://localhost:8081
HTTP/1.1 200
Content-Type: application/hal+json;charset=UTF-8
Date: Thu, 20 May 2021 08:49:51 GMT
Transfer-Encoding: chunked

{
    "_links": {
        "profile": {
            "href": "http://localhost:8081/profile"
        },
        "reservations": {
            "href": "http://localhost:8081/reservations{?page,size,sort}",
            "templated": true
        },
        "retrieveMovies": {
            "href": "http://localhost:8081/retrieveMovies"
        },
        "retrieveReservations": {
            "href": "http://localhost:8081/retrieveReservations"
        }
    }
}
```
- pay service
```sh
http http://localhost:8082
HTTP/1.1 200 
Content-Type: application/hal+json;charset=UTF-8
Date: Thu, 20 May 2021 08:56:24 GMT
Transfer-Encoding: chunked

{
    "_links": {
        "approvals": {
            "href": "http://localhost:8082/approvals{?page,size,sort}",
            "templated": true
        },
        "profile": {
            "href": "http://localhost:8082/profile"
        }
    }
}

```
- movie service
```sh
http http://localhost:8083
HTTP/1.1 200
Content-Type: application/hal+json;charset=UTF-8
Date: Thu, 20 May 2021 08:55:41 GMT
Transfer-Encoding: chunked

{
    "_links": {
        "detailinqueries": {
            "href": "http://localhost:8083/detailinqueries"
        },
        "listinqueries": {
            "href": "http://localhost:8083/listinqueries"
        },
        "movieInquiries": {
            "href": "http://localhost:8083/movieInquiries"
        },
        "movieManagements": {
            "href": "http://localhost:8083/movieManagements{?page,size,sort}",
            "templated": true
        },
        "profile": {
            "href": "http://localhost:8083/profile"
        }
    }
}
```
- theater service
```sh
http http://localhost:8084
HTTP/1.1 200
Content-Type: application/hal+json;charset=UTF-8
Date: Thu, 20 May 2021 08:56:00 GMT
Transfer-Encoding: chunked

{
    "_links": {
        "movieSeats": {
            "href": "http://localhost:8084/movieSeats{?page,size,sort}",
            "templated": true
        },
        "movies": {
            "href": "http://localhost:8084/movies{?page,size,sort}",
            "templated": true
        },
        "profile": {
            "href": "http://localhost:8084/profile"
        },
        "reservationavailabilityinquiries": {
            "href": "http://localhost:8084/reservationavailabilityinquiries"
        }
    }
}
```

# 서비스 테스트

- movie등록
```sh
http POST http://localhost:8083/movieManagements movieId=10001 title="분노의 질주" status="opened"
http POST http://localhost:8083/movieManagements movieId=10002 title="미션 파서블" status="opened"
http POST http://localhost:8083/movieManagements movieId=10003 title="자산어보" status="opened"
http POST http://localhost:8083/movieManagements movieId=10004 title="간이역" status="opened"
http GET http://localhost:8083/movieManagements
http GET http://localhost:8084/movies
```
- 예매하기
```sh
http POST http://localhost:8081/reservations bookId=20000001 customerId=user01 movieId=10001
http POST http://localhost:8081/reservations bookId=20000002 customerId=user02 movieId=10003
http POST http://localhost:8081/reservations bookId=20000003 customerId=user03 movieId=10004

http http://localhost:8081/reservations
http http://localhost:8082/approvals
```