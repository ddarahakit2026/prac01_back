package com.example.demo.orders.model;

import com.example.demo.board.model.Board;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name="orders_idx")
    private Orders orders;

    @ManyToOne
    @JoinColumn(name="board_idx")
    private Board board;

}
