package kr.or.oti.b01;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
    "file:src/main/webapp/WEB-INF/spring/root-context.xml",
    "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class BoardControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    // 1. 등록 화면 폼 진입 GET
    @Test
    public void test01_RegisterGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/board/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/register"));
        log.info("=== 1. /board/register GET 검증 완료 ===");
    }

    // 2. 게시글 등록 POST 및 리다이렉트 확인
    @Test
    public void test02_RegisterPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/board/register")
                        .param("title", "컨트롤러 테스트 제목")
                        .param("content", "컨트롤러 테스트 본문")
                        .param("writer", "user00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"))
                .andExpect(flash().attributeExists("result"));
        log.info("=== 2. /board/register POST 검증 완료 ===");
    }

    // 3. 게시판 목록 GET 및 모델 데이터(pageResponseDTO) 전달 확인
    @Test
    public void test03_List() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/board/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"))
                .andExpect(model().attributeExists("pageResponseDTO"));
        log.info("=== 3. /board/list GET 검증 완료 ===");
    }

    // 4. 게시글 상세 조회 GET 및 Model 전달 확인
    @Test
    public void test04_Read() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/board/read")
                        .param("bno", "205")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/read"))
                .andExpect(model().attributeExists("dto"));
        log.info("=== 4. /board/read GET 검증 완료 ===");
    }

    // 5. 게시글 수정 화면 진입 GET
    @Test
    public void test05_ModifyGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/board/modify")
                        .param("bno", "205")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/modify"))
                .andExpect(model().attributeExists("dto"));
        log.info("=== 5. /board/modify GET 검증 완료 ===");
    }

    // 6. 게시글 수정 처리 POST 및 리다이렉트
    @Test
    public void test06_ModifyPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/board/modify")
                        .param("bno", "205")
                        .param("title", "수정된 컨트롤러 테스트 제목")
                        .param("content", "수정된 컨트롤러 테스트 내용")
                        .param("writer", "user00")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().is3xxRedirection());
        log.info("=== 6. /board/modify POST 검증 완료 ===");
    }

    // 7. 선택 게시글 일괄 삭제 POST
    @Test
    public void test07_RemoveSelected() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/board/removeSelected")
                        .param("bno", "205")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().is3xxRedirection());
        log.info("=== 7. /board/removeSelected POST 검증 완료 ===");
    }
}