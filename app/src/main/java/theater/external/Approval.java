package theater.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Approval {
    private Long id;
    private String payId;
    private String bookId;
    private String movieId;
    private String customerId;
    private String seatId;
}
