package theater;

import javax.persistence.*;

import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="MovieSeat_table")
public class MovieSeat {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String seatId;
    private String screenId;
    private String bookId;
    private String status;

    @PostPersist
    public void onPostPersist(){
        SeatReserved seatReserved = new SeatReserved();
        BeanUtils.copyProperties(this, seatReserved);
        seatReserved.setMessage("예매 되었습니다.");
        seatReserved.publishAfterCommit();
    }

    @PostRemove
    public void onPostRemove(){
        SeatCanceled seatCanceled = new SeatCanceled();
        BeanUtils.copyProperties(this, seatCanceled);
        seatCanceled.setMessage("예매가 취소되었습니다.");
        seatCanceled.publishAfterCommit();
    }
}
