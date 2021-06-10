
package theater;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CanceledPoint extends AbstractEvent {
    private Long id;
    private Long point;
    private String customerId;
}


