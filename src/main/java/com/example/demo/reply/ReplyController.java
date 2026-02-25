package com.example.demo.reply;

import com.example.demo.common.model.BaseResponse;
import com.example.demo.reply.model.ReplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/reply")
@RestController
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    // 댓글 작성
    @PostMapping("/reg/{boardIdx}")
    public ResponseEntity register(@PathVariable Long boardIdx, @RequestBody ReplyDto.RegReq dto) {
        ReplyDto.RegRes result = replyService.register(boardIdx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 댓글 목록
    @GetMapping("/list/{boardIdx}")
    public ResponseEntity list(@PathVariable Long boardIdx) {
        List<ReplyDto.ListRes> dto = replyService.list(boardIdx);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    // 댓글 수정
    @PutMapping("/update/{replyIdx}")
    public ResponseEntity update(@PathVariable Long replyIdx, @RequestBody ReplyDto.RegReq dto) {
        ReplyDto.RegRes result = replyService.update(replyIdx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{replyIdx}")
    public ResponseEntity delete(@PathVariable Long replyIdx) {
        replyService.delete(replyIdx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }
}