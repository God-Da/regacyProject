package kr.or.oti.b01.controller;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.dto.ReplyDTO;
import kr.or.oti.b01.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 추후 Swagger 설정 추가 시 활성화
// import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyService replyService;

    // @ApiOperation(value = "Replies POST", notes = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> register(@Valid @RequestBody ReplyDTO replyDTO,
                                                      BindingResult bindingResult) throws BindException {
        log.info("replyDTO : {}", replyDTO);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        replyDTO = replyService.registerReply(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", replyDTO.getRno());

        return ResponseEntity.ok(resultMap);
    }

    // @ApiOperation(value = "Replies of Board", notes = "GET 방식으로 특정 게시물의 댓글리스트 조회")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> list(@PathVariable("bno") long bno, PageRequestDTO pageRequestDTO) {
        return replyService.getReplyList(bno, pageRequestDTO);
    }

    // @ApiOperation(value = "Read reply", notes = "GET 방식으로 특정 댓글 조회")
    @GetMapping(value = "/{rno}")
    public ReplyDTO read(@PathVariable("rno") long rno) {
        return replyService.getReply(rno);
    }

    // @ApiOperation(value = "Modify reply", notes = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify(@PathVariable("rno") long rno, @Valid @RequestBody ReplyDTO replyDTO) {
        replyDTO.setRno(rno);
        replyService.modifyReply(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }

    // @ApiOperation(value = "Delete reply", notes = "Delete 방식으로 특정 댓글 삭제")
    @DeleteMapping(value = "/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") long rno, PageRequestDTO pageRequestDTO) {
        replyService.removeReply(rno);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }
}