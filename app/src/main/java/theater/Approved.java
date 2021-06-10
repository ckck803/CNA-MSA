package theater;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Approved extends AbstractEvent {
    private Long id;
    private String payId;
    private String movieId;
    private String bookId;
    private String seatId;
}

