
package com.example.point.dto;

import com.example.point.domain.AbstractEvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisteredPoint extends AbstractEvent {
    private Long id;
    private Long point;
    private String customerId;
}


