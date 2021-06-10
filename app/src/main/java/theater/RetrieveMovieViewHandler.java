package theater;

import theater.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RetrieveMovieViewHandler {


    @Autowired
    private RetrieveMovieRepository retrieveMovieRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReserved_then_CREATE_1 (@Payload Reserved reserved) {
        try {

            if (!reserved.validate()) return;

            // view 객체 생성
            RetrieveMovie retrieveMovie = new RetrieveMovie();
            // view 객체에 이벤트의 Value 를 set 함
            // view 레파지 토리에 save
            retrieveMovieRepository.save(retrieveMovie);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}