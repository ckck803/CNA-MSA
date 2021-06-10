package theater;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="RetrieveReservation_table")
public class RetrieveReservation {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

}
