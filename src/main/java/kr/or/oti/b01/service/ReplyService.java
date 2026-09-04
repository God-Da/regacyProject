package kr.or.oti.b01.service;

import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.dto.ReplyDTO;

public interface ReplyService {
    ReplyDTO registerReply(ReplyDTO replyDTO);
    PageResponseDTO<ReplyDTO> getReplyList(long bno, PageRequestDTO pageRequestDTO);
    ReplyDTO getReply(long rno);
    Long removeReply(long rno);
    ReplyDTO modifyReply(ReplyDTO replyDTO);
}