package kr.or.oti.b01.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.dto.BoardListAllDTO;
import kr.or.oti.b01.dto.BoardListReplyCountDTO;
import kr.or.oti.b01.dto.PageRequestDTO;

@Mapper
public interface BoardMapper {

    // 게시글 본문 등록
    void insert(BoardDTO boardDTO);

    // 첨부파일 이미지 개별 등록
    void insertImage(@Param("uuid") String uuid, 
                     @Param("fileName") String fileName, 
                     @Param("ord") int ord, 
                     @Param("board_bno") Long board_bno);

    // 기본 단건 조회
    BoardDTO selectOne(Long bno);

    // 이미지 파일명 리스트가 포함된 단건 조회
    BoardDTO selectOneWithImages(Long bno);

    // 게시글 수정
    void update(BoardDTO boardDTO);

    // 게시글 단건 삭제
    void delete(Long bno);

    // 특정 게시글의 첨부 이미지 전체 삭제
    void deleteImages(Long bno);

    // 단순 목록 페이징 조회
    List<BoardDTO> selectList(PageRequestDTO pageRequestDTO);

    // 댓글 수 포함 페이징 목록 조회
    List<BoardListReplyCountDTO> selectListWithReplyCount(PageRequestDTO pageRequestDTO);

    // 이미지 및 댓글 수 포함 페이징 목록 조회
    List<BoardListAllDTO> selectListWithAll(PageRequestDTO pageRequestDTO);

    // 검색 조건 반영된 총 레코드 수 카운트
    int getCount(PageRequestDTO pageRequestDTO);

    // 선택 게시글 일괄 삭제
    void deleteSelected(@Param("bnos") List<Long> bnos);

    // 선택 게시글의 첨부 이미지 일괄 삭제
    void deleteSelectedImages(@Param("bnos") List<Long> bnos);

    // 전체 게시글 삭제
    void deleteAll();

    // 전체 이미지 삭제
    void deleteAllImages();
}