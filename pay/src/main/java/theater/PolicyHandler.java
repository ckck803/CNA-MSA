package theater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import theater.config.kafka.KafkaProcessor;

import javax.transaction.Transactional;

@Service
@Slf4j
public class PolicyHandler {
    @Autowired
    private ApprovalRepository approvalRepository;

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverCanceled_Cancel(@Payload Canceled canceled) {

        if (!canceled.validate())
            return;

        System.out.println("\n\n##### listener Cancel : " + canceled.toJson() + "\n\n");
        log.info("Pay Id: " + canceled.getPayId());
        approvalRepository.deleteByPayId(canceled.getPayId());
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

}
