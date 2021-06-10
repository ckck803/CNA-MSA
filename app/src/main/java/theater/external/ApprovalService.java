
package theater.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import theater.HttpConfiguration;
import theater.fallback.ApprovalServiceFallbackFactory;

import java.util.Date;

@FeignClient(name = "pay-api", url = "${feign.pay-api.url}",
        configuration = HttpConfiguration.class,
        fallbackFactory = ApprovalServiceFallbackFactory.class)
public interface ApprovalService {

    @GetMapping("/approved")
    Approval paymentRequest(@RequestParam("bookId") String bookId
            , @RequestParam("movieId") String movieId
            , @RequestParam("seatId") String seatId
            , @RequestParam("customerId") String customerId
    );

    @GetMapping("/isolation")
    String isolation();
}