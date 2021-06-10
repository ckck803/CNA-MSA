package com.example.alliance.domain;

import com.example.alliance.dto.app.CanceledPoint;
import com.example.alliance.dto.app.RegisteredPoint;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointManagement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long point;
    private String bookId;
    private String customerId;
    private String status;

    @PostPersist
    public void onPersist(){
        RegisteredPoint registeredPoint = RegisteredPoint.builder()
                .id(this.id)
                .point(this.getPoint())
                .customerId(this.getCustomerId())
                .build();

        registeredPoint.publishAfterCommit();
    }

    @PostRemove
    public void onRemove(){
        CanceledPoint canceledPoint = CanceledPoint.builder()
                .id(this.id)
                .point(this.getPoint())
                .customerId(this.getCustomerId())
                .build();

        canceledPoint.publishAfterCommit();
    }
}
