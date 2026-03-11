package com.example.demo.board;

import com.example.demo.board.model.BoardDto;
import com.example.demo.user.UserService;
import com.example.demo.user.model.AuthUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void boardController_게시글_작성_성공() throws Exception {
        BoardDto.RegReq dto = new BoardDto.RegReq("제목01","내용01");
        AuthUserDetails user = AuthUserDetails.builder()
                .idx(1L)
                .username("test01@test.com")
                .role("ROLE_USER")
                .name("test01")
                .build();
        mockMvc.perform(
                MockMvcRequestBuilders.post("/board/reg")
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .content(objectMapper.writeValueAsString(dto))
        );
    }
}