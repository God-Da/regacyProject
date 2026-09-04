package kr.or.oti.b01.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.dto.ReplyDTO;
import kr.or.oti.b01.mapper.ReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;

    @Override
    public ReplyDTO registerReply(ReplyDTO replyDTO) {
        replyMapper.insert(replyDTO);
        return replyDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ReplyDTO> getReplyList(long bno, PageRequestDTO pageRequestDTO) {
        List<ReplyDTO> dtoList = replyMapper.selectListOfBoard(bno, pageRequestDTO);
        int total = replyMapper.getCountOfBoard(bno);

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyDTO getReply(long rno) {
        return replyMapper.selectOne(rno);
    }

    @Override
    public Long removeReply(long rno) {
        replyMapper.delete(rno);
        return rno;
    }

    @Override
    public ReplyDTO modifyReply(ReplyDTO replyDTO) {
        replyMapper.update(replyDTO);
        return replyDTO;
    }
}