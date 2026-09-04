package kr.or.oti.b01;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.oti.b01.dto.ReplyDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
    "file:src/main/webapp/WEB-INF/spring/root-context.xml",
    "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ReplyControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // 실제 존재하는 게시글 번호(bno) 지정
    private static final Long TEST_BNO = 205L;
    private static Long generatedRno = 0L;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.objectMapper = new ObjectMapper();
    }

    // 1. 댓글 등록 POST (/replies/)
    @Test
    public void test01_Register() throws Exception {
        ReplyDTO replyDTO = ReplyDTO.builder()
                .bno(TEST_BNO)
                .replyText("REST 댓글 등록 테스트 본문")
                .replyer("user00")
                .build();

        String jsonStr = objectMapper.writeValueAsString(replyDTO);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/replies/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rno").exists())
                .andReturn();

        // 생성된 rno 추출하여 다음 테스트에서 활용
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> map = objectMapper.readValue(responseContent, Map.class);
        generatedRno = Long.valueOf(map.get("rno").toString());

        log.info("=== 1. 댓글 등록 성공, 생성된 rno: {} ===", generatedRno);
    }

    // 2. 특정 게시물의 댓글 목록 조회 GET (/replies/list/{bno})
    @Test
    public void test02_List() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/replies/list/" + TEST_BNO)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dtoList").isArray())
                .andExpect(jsonPath("$.total").isNumber());

        log.info("=== 2. 특정 게시글({}) 댓글 목록 조회 성공 ===", TEST_BNO);
    }

    // 3. 특정 댓글 단건 조회 GET (/replies/{rno})
    @Test
    public void test03_Read() throws Exception {
        long targetRno = generatedRno > 0 ? generatedRno : 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/replies/" + targetRno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rno").value(targetRno))
                .andExpect(jsonPath("$.replyText").exists());

        log.info("=== 3. 댓글 단건 조회 성공: rno = {} ===", targetRno);
    }

    // 4. 댓글 수정 PUT (/replies/{rno})
    @Test
    public void test04_Modify() throws Exception {
        long targetRno = generatedRno > 0 ? generatedRno : 1L;

        ReplyDTO modifyDTO = ReplyDTO.builder()
                .rno(targetRno)
                .bno(TEST_BNO)
                .replyText("수정된 댓글 내용입니다.")
                .replyer("user00")
                .build();

        String jsonStr = objectMapper.writeValueAsString(modifyDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/replies/" + targetRno)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rno").value(targetRno));

        log.info("=== 4. 댓글 수정 성공: rno = {} ===", targetRno);
    }

    // 5. 댓글 삭제 DELETE (/replies/{rno})
    @Test
    public void test05_Remove() throws Exception {
        long targetRno = generatedRno > 0 ? generatedRno : 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/replies/" + targetRno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rno").value(targetRno));

        log.info("=== 5. 댓글 삭제 성공: rno = {} ===", targetRno);
    }
}