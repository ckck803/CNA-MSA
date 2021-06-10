package theater;

import lombok.Data;

@Data
public class Book {
    private String bookId;
    private String customerId;
    private String movieId;
    private String payId;
    private String seatId;
    private String bookedYn;
}
