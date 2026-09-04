package kr.or.oti.b01.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.ReplyDTO;

@Mapper
public interface ReplyMapper {

    // 댓글 등록 (bno는 ReplyDTO의 bno 필드로 전달, 생성된 rno 자동 주입)
    void insert(ReplyDTO replyDTO);

    // 댓글 단건 조회
    ReplyDTO selectOne(long rno);

    // 댓글 수정
    void update(ReplyDTO replyDTO);

    // 댓글 단건 삭제
    void delete(long rno);

    // 특정 게시글의 댓글 페이징 목록 조회
    List<ReplyDTO> selectListOfBoard(@Param("bno") long bno, @Param("pageRequestDTO") PageRequestDTO pageRequestDTO);

    // 특정 게시글의 전체 댓글 수 카운트
    int getCountOfBoard(long bno);

    // 게시글 삭제 시 하위 댓글 일괄 삭제
    void deleteByBoardBno(long bno);
}