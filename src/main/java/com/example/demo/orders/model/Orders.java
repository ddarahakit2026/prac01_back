package com.example.demo.orders.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Setter
    @ColumnDefault("false")
    private boolean paid;

    private int paymentPrice;

    @Setter
    private String pgPaymentId;

    @OneToMany(mappedBy = "orders")
    private List<OrdersItem> items;

}
