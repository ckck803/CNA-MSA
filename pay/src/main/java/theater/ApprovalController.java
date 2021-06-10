package theater;

import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ApprovalController {
    @Autowired
    ApprovalRepository approvalRepository;

    @RequestMapping(value = "/approved", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Approval paymentRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String bookId = request.getParameter("bookId");
        String movieId = request.getParameter("movieId");
        String seatId = request.getParameter("seatId");
        String customerId = request.getParameter("customerId");

        /**
         * 예약번호를 받아서 결재를 진행한다. 결재 결과로 payid를 받는다.
         */
        Approval approval = Approval.builder()
                .payId(UUID.randomUUID().toString())
                .bookId(bookId)
                .movieId(movieId)
                .seatId(seatId)
                .customerId(customerId)
                .build();
        approvalRepository.save(approval);

        ObjectMapper objectMapper = new ObjectMapper();
        String sendMessage = objectMapper.writeValueAsString(approval);

        log.info("pay info :: {}", sendMessage);

        return approvalRepository.findByPayId(approval.getPayId()).orElse(null);
    }


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

    @Value("${pay.mid}")
    String mid;

    @GetMapping("/cm")
    public String getCM() {
        return mid;
    }
}
