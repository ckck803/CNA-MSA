package theater;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import theater.config.kafka.KafkaProcessor;

@Service
public class PolicyHandler{

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSeatReserved_Kakao(@Payload SeatReserved seatReserved){

        if(!seatReserved.validate()) return;

        System.out.println("\n\n##### listener Kakao : " + seatReserved.toJson() + "\n\n");

        // Sample Logic //
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSeatCanceled_Kakao(@Payload SeatCanceled seatCanceled){

        if(!seatCanceled.validate()) return;

        System.out.println("\n\n##### listener Kakao : " + seatCanceled.toJson() + "\n\n");

        // Sample Logic //
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
