package com.example.alliance.dto.pay;

import com.example.alliance.domain.AbstractEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Approved extends AbstractEvent {
    private Long id;
    private String payId;
    private String movieId;
    private String bookId;
    private String seatId;
    private String customerId;
}
