package com.example.alliance.dto.app;

import com.example.alliance.domain.AbstractEvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisteredPoint extends AbstractEvent {
    private Long id;
    private Long point;
    private String customerId;
}
