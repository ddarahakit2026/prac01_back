import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

public class BulkInsert {

    // DB 연결 정보 (본인의 환경에 맞게 수정하세요)
    private static final String URL = "jdbc:mariadb://10.10.10.30:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "qwer1234";

    // 생성할 데이터 건수 설정
    private static final int USER_COUNT = 10000;    // 사용자 1만 명
    private static final int BOARD_COUNT = 50000;   // 게시글 5만 개
    private static final int REPLY_COUNT = 200000;  // 댓글 20만 개
    private static final int LIKE_COUNT = 300000;   // 좋아요 30만 개

    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false); // 수동 커밋으로 성능 최적화

            System.out.println("대량 데이터 삽입 시작...");
            long start = System.currentTimeMillis();

            // 1. 제약 조건 일시 중지 (속도 향상)
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            }

            // 2. 순차적으로 데이터 삽입 (FK 관계 고려)
            insertUsers(conn, USER_COUNT);
            insertBoards(conn, BOARD_COUNT, USER_COUNT);
            insertReplies(conn, REPLY_COUNT, BOARD_COUNT, USER_COUNT);
            insertLikes(conn, LIKE_COUNT, BOARD_COUNT, USER_COUNT);

            // 3. 제약 조건 다시 활성화
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }

            conn.commit();
            long end = System.currentTimeMillis();
            System.out.println("전체 작업 완료! 총 소요 시간: " + (end - start) / 1000.0 + "초");

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private static void insertUsers(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO user (email, enable, name, password, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setString(1, "user" + i + "@example.com");
                pstmt.setBoolean(2, true);
                pstmt.setString(3, "사용자" + i);
                pstmt.setString(4, "pass123");
                pstmt.setString(5, "ROLE_USER");
                pstmt.addBatch();
                if (i % 1000 == 0) pstmt.executeBatch();
            }
            pstmt.executeBatch();
        }
        System.out.println("User 삽입 완료.");
    }

    private static void insertBoards(Connection conn, int count, int userCount) throws SQLException {
        String sql = "INSERT INTO board (create_date, update_date, contents, title, user_idx) VALUES (?, ?, ?, ?, ?)";
        Random random = new Random();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setTimestamp(1, now);
                pstmt.setTimestamp(2, now);
                pstmt.setString(3, "게시글 내용 상세 데이터... " + i);
                pstmt.setString(4, "게시글 제목 " + i);
                pstmt.setLong(5, random.nextInt(userCount) + 1);
                pstmt.addBatch();
                if (i % 1000 == 0) pstmt.executeBatch();
            }
            pstmt.executeBatch();
        }
        System.out.println("Board 삽입 완료.");
    }

    private static void insertReplies(Connection conn, int count, int boardCount, int userCount) throws SQLException {
        String sql = "INSERT INTO reply (contents, board_idx, user_idx) VALUES (?, ?, ?)";
        Random random = new Random();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setString(1, "댓글 내용입니다 " + i);
                pstmt.setLong(2, random.nextInt(boardCount) + 1);
                pstmt.setLong(3, random.nextInt(userCount) + 1);
                pstmt.addBatch();
                if (i % 1000 == 0) pstmt.executeBatch();
            }
            pstmt.executeBatch();
        }
        System.out.println("Reply 삽입 완료.");
    }

    private static void insertLikes(Connection conn, int count, int boardCount, int userCount) throws SQLException {
        String sql = "INSERT INTO likes (board_idx, user_idx) VALUES (?, ?)";
        Random random = new Random();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setLong(1, random.nextInt(boardCount) + 1);
                pstmt.setLong(2, random.nextInt(userCount) + 1);
                pstmt.addBatch();
                if (i % 1000 == 0) pstmt.executeBatch();
            }
            pstmt.executeBatch();
        }
        System.out.println("Likes 삽입 완료.");
    }
}