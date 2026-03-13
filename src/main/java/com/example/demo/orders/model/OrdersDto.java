package com.example.demo.orders.model;

import com.example.demo.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public class OrdersDto {
    @Builder
    @Getter
    public static class VerifyReq {
        private String paymentId;
    }


    @Builder
    @Getter
    public static class OrdersReq {
        private Integer paymentPrice;
        private List<Long> boardIdxList;

        public Orders toEntity(User user) {
            return Orders.builder()
                    .paid(false)
                    .paymentPrice(paymentPrice)
                    .build();
        }
    }


    @Builder
    @Getter
    public static class OrdersRes {
        private Long ordersIdx;
        private boolean paid;

        public static OrdersRes from(Orders entity) {
            return OrdersRes.builder()
                    .ordersIdx(entity.getIdx())
                    .paid(entity.isPaid())
                    .build();
        }
    }


}
