package com.example.point.policy;

import com.example.point.config.kafka.KafkaProcessor;
import com.example.point.domain.Point;
import com.example.point.dto.CanceledPoint;
import com.example.point.dto.RegisteredPoint;
import com.example.point.repository.PointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class PolicyHandler {
    @Autowired
    private PointRepository pointRepository;


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
