package com.example.demo.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    // WebSocketConfig 클래스에서 설정해둔 /app 뒤에 /test로 메시지를 보내면 해당 메소드를 실행
    // /app/test
    @MessageMapping("/test")

    // WebSocketConfig 클래스에서 설정해둔 /topic 뒤에 /test라는 토픽을 구독한 사용자들에게 return 값을 보내주는 기능
    // test라는 토픽을 구독한 클라이언트들에게 메시지 전송
    @SendTo("/topic/test")
    public String test() {
        System.out.printf("test");

        return "zzzz";
    }
}
