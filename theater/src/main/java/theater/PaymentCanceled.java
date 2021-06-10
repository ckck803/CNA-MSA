
package theater;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCanceled extends AbstractEvent {
    private Long id;
    private String bookId;
}

