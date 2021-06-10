
package theater;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Approved extends AbstractEvent {
    private Long id;
    private String payId;
    private String movieId;
    private String bookId;
    private String seatId;
}

