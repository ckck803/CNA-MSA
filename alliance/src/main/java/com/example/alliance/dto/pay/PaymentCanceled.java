package com.example.alliance.dto.pay;

import com.example.alliance.domain.AbstractEvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCanceled extends AbstractEvent {
    private Long id;
    private String bookId;
}
