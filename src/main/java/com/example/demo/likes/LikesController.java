package com.example.demo.likes;

import com.example.demo.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/likes")
@RestController
public class LikesController {
    private final LikesService  likesService;

    @GetMapping("/{boardIdx}")
    public ResponseEntity likes(
            @AuthenticationPrincipal AuthUserDetails user,
            @PathVariable Long boardIdx
    ) {
        likesService.like(user, boardIdx);

        return ResponseEntity.ok("성공");
    }
}
