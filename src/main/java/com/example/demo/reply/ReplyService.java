package com.example.demo.reply;

import com.example.demo.board.BoardRepository;
import com.example.demo.board.model.Board;
import com.example.demo.common.exception.BaseException;
import com.example.demo.reply.model.Reply;
import com.example.demo.reply.model.ReplyDto;
import com.example.demo.user.UserRepository;
import com.example.demo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.common.model.BaseResponseStatus.FAIL;

@RequiredArgsConstructor
@Service
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    private User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw BaseException.from(FAIL);
        }

        return userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> BaseException.from(FAIL)
        );
    }

    public ReplyDto.RegRes register(Long boardIdx, ReplyDto.RegReq dto) {
        User user = getLoginUser();
        Board board = boardRepository.findById(boardIdx).orElseThrow();

        Reply entity = dto.toEntity();
        entity.setBoard(board);
        entity.setUser(user);

        replyRepository.save(entity);

        return ReplyDto.RegRes.from(entity);
    }

    public List<ReplyDto.ListRes> list(Long boardIdx) {
        List<Reply> replyList = replyRepository.findAllByBoard_IdxOrderByIdxAsc(boardIdx);
        return replyList.stream().map(ReplyDto.ListRes::from).toList();
    }

    public ReplyDto.RegRes update(Long replyIdx, ReplyDto.RegReq dto) {
        getLoginUser(); // 로그인만 체크(구성 유지)
        Reply reply = replyRepository.findById(replyIdx).orElseThrow();
        reply.update(dto);
        replyRepository.save(reply);
        return ReplyDto.RegRes.from(reply);
    }

    public void delete(Long replyIdx) {
        getLoginUser(); // 로그인만 체크(구성 유지)
        replyRepository.deleteById(replyIdx);
    }
}