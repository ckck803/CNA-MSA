package com.example.alliance.policy;

import com.example.alliance.config.kafka.KafkaProcessor;
import com.example.alliance.domain.PointManagement;
import com.example.alliance.dto.pay.Approved;
import com.example.alliance.dto.pay.PaymentCanceled;
import com.example.alliance.repository.PointManagementRepository;
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
    private PointManagementRepository pointManagementRepository;

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverApproved_registeredPoint(@Payload Approved approved) {

        log.info("Event 발생");
        if (!approved.validate())
            return;

        log.info("Register Point Event 발생");

        PointManagement pointManagement = PointManagement.builder()
                .point(1000L)
                .customerId(approved.getCustomerId())
                .bookId(approved.getBookId())
                .build();

        Optional<PointManagement> optional = pointManagementRepository.findByBookId(approved.getBookId());
        if(optional.isPresent()){
            pointManagement.setStatus("U");
        }else {
            pointManagement.setStatus("C");
            pointManagementRepository.save(pointManagement);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverCanceledPoint_CanceledPoint(@Payload PaymentCanceled paymentCanceled) {

        if (!paymentCanceled.validate())
            return;

        log.info("Canceled Point Event 발생");

        Optional<PointManagement> optional = pointManagementRepository.findByBookId(paymentCanceled.getBookId());
        if(optional.isPresent()){
            PointManagement point = optional.get();
            pointManagementRepository.deleteByBookId(point.getBookId());
        }
    }
}
