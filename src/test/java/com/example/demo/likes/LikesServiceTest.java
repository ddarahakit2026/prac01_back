package com.example.demo.likes;

import com.example.demo.board.BoardRepository;
import com.example.demo.board.model.Board;
import com.example.demo.user.model.AuthUserDetails;
import com.example.demo.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LikesServiceTest {
    @Autowired
    private LikesService likesService;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    void like() throws InterruptedException {
        // 실행해볼 전체 스레드 수
        int threadCount = 1000;

        // 비동기로 실행할 스레드의 수(스프링 서버의 스레드풀 수)
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= threadCount; i++) {
            AuthUserDetails user = AuthUserDetails.builder().idx(Long.valueOf(i)).build();
            executorService.submit(() -> {
                try {
                    likesService.like(user, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 요청이 끝날 때까지 대기

        long endTime = System.currentTimeMillis();

        System.out.printf("처리 시간 : " + (endTime - startTime) + "ms");
        Board board = boardRepository.findById(1L).orElseThrow();
        assertEquals(threadCount, board.getLikesCount());
    }
}





