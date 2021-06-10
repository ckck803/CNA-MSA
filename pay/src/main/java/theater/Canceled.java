
package theater;

import lombok.Data;

@Data
public class Canceled extends AbstractEvent {

    private Long id;
    private String bookId;
    private String payId;
    private String movieId;
    private String customerId;
}

