package theater;

import javax.persistence.*;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "Reservation_table")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String movieId;

    @Column(nullable = false, unique = true)
    private String payId;

    @Column(nullable = false, unique = true)
    private String seatId;

    private String bookedYn;

    @PostLoad
    public void onPostLoad() {
        Logger logger = LoggerFactory.getLogger("Reservation");
        logger.info("Load");
    }
}