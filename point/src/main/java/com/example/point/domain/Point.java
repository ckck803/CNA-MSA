package com.example.point.domain;

import javax.persistence.*;

import lombok.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Point_table")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Point {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long point;
    private String customerId;
}
