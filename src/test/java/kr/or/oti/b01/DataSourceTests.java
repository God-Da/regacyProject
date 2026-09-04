package kr.or.oti.b01;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Slf4j
public class DataSourceTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired(required = false)
    private BoardMapper boardMapper;

    // 1. HikariCP 및 MariaDB(23306 포트) 물리 연결 테스트
    @Test
    public void testConnection() {
        try (Connection con = dataSource.getConnection()) {
            log.info("=== 1. DB 커넥션 획득 성공 ===");
            log.info("Connection 객체: {}", con);
            assertNotNull(con);
        } catch (Exception e) {
            log.error("DB 연결 실패: 포트 23306 실행 여부 및 계정 정보 확인 필요", e);
            throw new RuntimeException(e);
        }
    }

    // 2. MyBatis SqlSessionFactory 빈 생성 및 세션 오픈 테스트
    @Test
    public void testMyBatis() {
        try (SqlSession session = sqlSessionFactory.openSession();
             Connection con = session.getConnection()) {
            log.info("=== 2. MyBatis 세션 오픈 성공 ===");
            log.info("SqlSession 객체: {}", session);
            assertNotNull(session);
        } catch (Exception e) {
            log.error("MyBatis 설정 실패: root-context.xml 확인 필요", e);
            throw new RuntimeException(e);
        }
    }

    // 3. Mapper 인터페이스 주입 및 INSERT 쿼리 동작 테스트
    @Test
    public void testBoardMapper() {
        log.info("=== 3. BoardMapper 빈 주입 확인 ===");
        log.info("BoardMapper 객체: {}", boardMapper);
        assertNotNull("BoardMapper 빈이 주입되지 않았습니다. mybatis-spring:scan 설정을 확인하세요.", boardMapper);

        BoardDTO boardDTO = BoardDTO.builder()
                .title("레거시 전환 단위 테스트")
                .content("MyBatis 연동 테스트 내용입니다.")
                .writer("user00")
                .build();

        boardMapper.insert(boardDTO);
        log.info("=== 4. INSERT 성공, 생성된 게시글 번호(bno): {} ===", boardDTO.getBno());
        assertNotNull(boardDTO.getBno());
    }
}