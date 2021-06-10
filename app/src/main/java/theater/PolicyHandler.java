package theater;

import lombok.extern.slf4j.Slf4j;
import theater.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class PolicyHandler{
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    private PointRepository pointRepository;


    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverApproved_Updatestate(@Payload Approved approved) {

        if (!approved.validate())
            return;

        System.out.println("\n\n##### listener Updatestate : " + approved.toJson() + "\n\n");
    }

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverPaymentCanceled_Updatestate(@Payload PaymentCanceled paymentCanceled) {

        if (!paymentCanceled.validate())
            return;

        Reservation ticket = reservationRepository.findByBookId(paymentCanceled.getBookId()).orElse(null);
        ticket.setBookedYn("N");
        System.out.println("\n\n##### listener Updatestate : " + paymentCanceled.toJson() + "\n\n");
    }

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverRegisteredPoint_UpdateState(@Payload RegisteredPoint registeredPoint) {

        if (!registeredPoint.validate())
            return;

        log.info("========= Point 등록 확인 =========");
        Optional<Point> optional = pointRepository.findByCustomerId(registeredPoint.getCustomerId());
        if(!optional.isPresent()){
            Point point = Point.builder()
                    .point(registeredPoint.getPoint())
                    .customerId(registeredPoint.getCustomerId())
                    .build();

            pointRepository.save(point);
        }else{
            Point point = optional.get();
            point.setPoint(point.getPoint() + registeredPoint.getPoint());
        }

    }

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverCanceledPoint_UpdateState(@Payload CanceledPoint canceledPoint) {

        if (!canceledPoint.validate())
            return;

        log.info("========== Point 삭제 확인 =========");
        Optional<Point> optional = pointRepository.findByCustomerId(canceledPoint.getCustomerId());
        if(optional.isPresent()){
            Point point = optional.get();

            if(point.getPoint() - canceledPoint.getPoint() > 0)
                point.setPoint(point.getPoint() - canceledPoint.getPoint());
            else{
                point.setPoint(0L);
            }
        }
    }

}
