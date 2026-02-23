package org.example.spring03.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestController {
    @GetMapping("/ex01")
    public String ex01() {
        return "성공";
    }

    @GetMapping("/ex02")
    public String ex02() {
        return "성공";
    }

    @GetMapping("/ex03")
    public String ex03() {
        return "성공";
    }

    @GetMapping("/ex04")
    public String ex04() {
        return "성공";
    }
}
