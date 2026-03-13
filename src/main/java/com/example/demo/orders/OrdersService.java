package com.example.demo.orders;

import com.example.demo.board.BoardRepository;
import com.example.demo.board.model.Board;
import com.example.demo.orders.model.Orders;
import com.example.demo.orders.model.OrdersDto;
import com.example.demo.orders.model.OrdersItem;
import com.example.demo.user.model.AuthUserDetails;
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
