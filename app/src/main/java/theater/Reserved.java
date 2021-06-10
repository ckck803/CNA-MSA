package theater;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserved extends AbstractEvent {

    private Long id;
    private String bookId;
    private String movieId;
    private String customerId;
    private String seatId;
}
