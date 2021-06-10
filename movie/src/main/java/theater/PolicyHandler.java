package theater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import theater.config.kafka.KafkaProcessor;

@Service
public class PolicyHandler{
    @Autowired MovieManagementRepository movieManagementRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload MovieRegistered movieRegistered){
        if(!movieRegistered.validate()){
            return;
        }

    }
}
