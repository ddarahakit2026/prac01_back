package com.example.demo.orders;

import com.example.demo.board.BoardRepository;
import com.example.demo.board.model.Board;
import com.example.demo.orders.model.Orders;
import com.example.demo.orders.model.OrdersDto;
import com.example.demo.orders.model.OrdersItem;
import com.example.demo.user.model.AuthUserDetails;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.ToNumberPolicy;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrdersItemRepository ordersItemRepository;
    private final BoardRepository boardRepository;
    private final PaymentClient pg;

    @Transactional
    public void verify(AuthUserDetails user, OrdersDto.VerifyReq dto) {
        CompletableFuture<Payment> completableFuture = pg.getPayment(dto.getPaymentId());
        Payment payment = completableFuture.join();

        if(payment instanceof PaidPayment paidPayment) {
            Map<String, Object> customData = new GsonBuilder()
                    .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                    .create().fromJson(paidPayment.getCustomData(), Map.class);

            Long ordersIdx = Long.parseLong(customData.get("ordersIdx").toString());
            Orders orders = ordersRepository.findById(ordersIdx).orElseThrow();

            int totalPrice = orders.getItems().stream()
                    .map(OrdersItem::getBoard)
                    .mapToInt(Board::getPrice)
                    .sum();

            if(paidPayment.getAmount().getTotal() == totalPrice) {
                orders.setPaid(true);
                orders.setPgPaymentId(dto.getPaymentId());
                ordersRepository.save(orders);
            }

        }

    }


    public OrdersDto.OrdersRes create(AuthUserDetails user, OrdersDto.OrdersReq dto) {
        List<Board> boardList = boardRepository.findAllById(
                dto.getBoardIdxList());

        Orders orders = ordersRepository.save(dto.toEntity(user.toEntity()));

        for (Board board: boardList) {
            OrdersItem ordersItem = OrdersItem.builder()
                    .board(board)
                    .orders(orders)
                    .build();
            ordersItemRepository.save(ordersItem);
        }

        return OrdersDto.OrdersRes.from(orders);
    }

}









