package kr.or.oti.b01.service;

import java.util.List;
import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.dto.BoardListAllDTO;
import kr.or.oti.b01.dto.BoardListReplyCountDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;

public interface BoardService {

    void register(BoardDTO boardDTO);

    BoardDTO get(long bno);

    void modify(BoardDTO boardDTO);

    void remove(long bno);

    PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    void removeSelected(List<Long> bnos);

    void removeAll();
}