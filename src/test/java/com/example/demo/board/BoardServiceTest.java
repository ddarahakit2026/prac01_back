package com.example.demo.board;

import com.example.demo.board.model.Board;
import com.example.demo.board.model.BoardDto;
import com.example.demo.reply.ReplyRepository;
import com.example.demo.user.model.AuthUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

/*
    TDD : Test-Driven Development, 테스트 주도 개발
        테스트 코드를 먼저 작성하고 그 테스트를 통과할 수 있는 코드를 나중에 만들자.

        1. 테스트 코드 작성 - 실패
        2. 구현할 코드 작성(테스트를 통과할 수 있는 최소한의 코드만 작성) - 성공
        3. 리팩토링 - 성공

    테스트 종류
        단위 테스트 : 하나의 클래스만 독립적으로 테스트, 외부 의존성을 Mock(가짜) 클래스로 대체해서 테스트
                일반적으로 서비스 클래스를 테스트
        통합 테스트 : 여러 의존하고 있는 클래스들 모두 같이 테스트, 스프링 컨텍스트를 실제로 실행, DB연동도 같이 함.
                일반적으로 컨트롤러 클래스를 테스트
*/

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @Mock // 가짜 객체 생성
    private BoardRepository boardRepository;
    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks // 가짜 객체로 의존성 주입
    private BoardService boardService;

    @Test
    void boardService_게시글등록_성공() {
        // given : 주어져야 하는 데이터를 여기서 지정
        AuthUserDetails user = AuthUserDetails.builder()
                .idx(1L)
                .role("ROLE_USER")
                .username("test01@test.com")
                .name("test01")
                .enable(true)
                .build();
        BoardDto.RegReq dto = new BoardDto.RegReq();
        dto.setTitle("제목01");
        dto.setContents("내용01");

        Board returnEntity = Board.builder()
                .idx(1L)
                .title("제목01")
                .contents("내용01")
                .build();

        given(boardRepository.save(any(Board.class))).willReturn(returnEntity);

        // when : 실제 실행을 테스트할 코드
        BoardDto.RegRes result = boardService.register(user, dto);

        // then : 실행 결과
        assertNotNull(result);
        assertEquals(1L, result.getIdx()); // 성공
//        assertEquals(2L, result.getIdx()); // 실패
        assertEquals("내용01", result.getContents());
    }

    @Test
    void list() {
    }

    @Test
    void read() {
    }
}