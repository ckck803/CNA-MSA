package theater;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="RetrieveMovie_table")
public class RetrieveMovie {

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
