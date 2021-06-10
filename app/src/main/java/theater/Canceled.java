
package theater;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Canceled extends AbstractEvent {

    private Long id;
    private String bookId;
    private String payId;
}

