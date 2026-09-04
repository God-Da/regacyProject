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
public class MemberControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    // 매 테스트마다 고유한 ID를 생성하여 중복 에러를 방지합니다.
    private static final String TEST_MID = "testuser_" + (System.currentTimeMillis() % 10000);

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    // 1. 로그인 폼 진입 GET
    @Test
    public void test01_LoginGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/member/login")
                        .param("errorCode", "")
                        .param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/login"));
        log.info("=== 1. /member/login GET 검증 완료 ===");
    }

    // 2. 회원가입 폼 진입 GET
    @Test
    public void test02_JoinGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/member/join"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/join"));
        log.info("=== 2. /member/join GET 검증 완료 ===");
    }

    // 3. 신규 회원가입 처리 POST (성공 케이스)
    @Test
    public void test03_JoinPost_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/member/join")
                        .param("mid", TEST_MID)
                        .param("mpw", "1111")
                        .param("email", TEST_MID + "@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login?result=success"));
        log.info("=== 3. /member/join POST 성공 검증 완료 (생성 MID: {}) ===", TEST_MID);
    }

    // 4. 동일 ID로 재가입 시도 (중복 예외 MidExistException 발생 및 리다이렉트 검증)
    @Test
    public void test04_JoinPost_DuplicateFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/member/join")
                        .param("mid", TEST_MID)
                        .param("mpw", "1111")
                        .param("email", TEST_MID + "@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/join"))
                .andExpect(flash().attribute("error", "mid"));
        log.info("=== 4. /member/join POST 중복 ID 예외 분기 검증 완료 ===");
    }

    // 5. 회원 목록 조회 GET (페이징 데이터 및 키워드 Model 전달 검증)
    @Test
    public void test05_MemberList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/member/list")
                        .param("page", "1")
                        .param("size", "10")
                        .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("member/memberList"))
                .andExpect(model().attributeExists("pageResponse"))
                .andExpect(model().attributeExists("keyword"));
        log.info("=== 5. /member/list GET 검증 완료 ===");
    }

    // 6. 회원 수정 화면 진입 GET
    @Test
    public void test06_ModifyGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/member/modify")
                        .param("mid", TEST_MID))
                .andExpect(status().isOk())
                .andExpect(view().name("member/memberModify"))
                .andExpect(model().attributeExists("member"));
        log.info("=== 6. /member/modify GET 검증 완료 ===");
    }

    // 7. 회원 정보 수정 처리 POST
    @Test
    public void test07_ModifyPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/member/modify")
                        .param("mid", TEST_MID)
                        .param("email", "updated_" + TEST_MID + "@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/list"))
                .andExpect(flash().attribute("result", "회원 정보가 수정되었습니다."));
        log.info("=== 7. /member/modify POST 검증 완료 ===");
    }

    // 8. 회원 탈퇴 처리 POST
    @Test
    public void test08_Remove() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/member/remove")
                        .param("mid", TEST_MID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/list"))
                .andExpect(flash().attributeExists("result"));
        log.info("=== 8. /member/remove POST 검증 완료 ===");
    }
}