package kr.or.oti.b01.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.dto.BoardListAllDTO;
import kr.or.oti.b01.dto.BoardListReplyCountDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.mapper.BoardMapper;
import kr.or.oti.b01.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final S3Uploader s3Uploader;

    @Override
    public void register(BoardDTO boardDTO) {
        boardMapper.insert(boardDTO);
        Long bno = boardDTO.getBno();

        if (boardDTO.getFileNames() != null && !boardDTO.getFileNames().isEmpty()) {
            int ord = 0;
            for (String fileName : boardDTO.getFileNames()) {
                String[] arr = fileName.split("_", 2);
                if (arr.length == 2) {
                    boardMapper.insertImage(arr[0], arr[1], ord++, bno);
                }
            }
        }
        log.info("Board Registered: {}", boardDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDTO get(long bno) {
        BoardDTO boardDTO = boardMapper.selectOneWithImages(bno);
        if (boardDTO == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다: " + bno);
        }

        if (boardDTO.getFileNames() != null && !boardDTO.getFileNames().isEmpty()) {
            List<String> imgUrls = boardDTO.getFileNames().stream()
                    .map(s3Uploader::getS3URL)
                    .collect(Collectors.toList());
            boardDTO.setImageUrls(imgUrls);
        }

        return boardDTO;
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        BoardDTO oldBoard = boardMapper.selectOneWithImages(boardDTO.getBno());

        if (oldBoard != null && oldBoard.getFileNames() != null) {
            oldBoard.getFileNames().forEach(s3Uploader::removeS3File);
        }

        boardMapper.deleteImages(boardDTO.getBno());
        boardMapper.update(boardDTO);

        if (boardDTO.getFileNames() != null && !boardDTO.getFileNames().isEmpty()) {
            int ord = 0;
            for (String fileName : boardDTO.getFileNames()) {
                String[] arr = fileName.split("_", 2);
                if (arr.length == 2) {
                    boardMapper.insertImage(arr[0], arr[1], ord++, boardDTO.getBno());
                }
            }
        }
    }

    @Override
    public void remove(long bno) {
        BoardDTO boardDTO = boardMapper.selectOneWithImages(bno);
        if (boardDTO != null && boardDTO.getFileNames() != null) {
            boardDTO.getFileNames().forEach(s3Uploader::removeS3File);
        }

        boardMapper.deleteImages(bno);
        boardMapper.delete(bno);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO) {
        List<BoardDTO> dtoList = boardMapper.selectList(pageRequestDTO);
        int total = boardMapper.getCount(pageRequestDTO);

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        List<BoardListReplyCountDTO> dtoList = boardMapper.selectListWithReplyCount(pageRequestDTO);
        int total = boardMapper.getCount(pageRequestDTO);

        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        List<BoardListAllDTO> dtoList = boardMapper.selectListWithAll(pageRequestDTO);
        int total = boardMapper.getCount(pageRequestDTO);

        PageResponseDTO<BoardListAllDTO> result = PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();

        result.getDtoList().forEach(dto -> {
            if (dto.getBoardImages() != null && !dto.getBoardImages().isEmpty()) {
                dto.getBoardImages().forEach(boardImage -> {
                    String imageUrl = s3Uploader.getS3URL(boardImage.getFullName());
                    boardImage.setImageUrl(imageUrl);
                });
            }
        });

        return result;
    }

    @Override
    public void removeSelected(List<Long> bnos) {
        if (bnos == null || bnos.isEmpty()) {
            return;
        }

        for (Long bno : bnos) {
            BoardDTO boardDTO = boardMapper.selectOneWithImages(bno);
            if (boardDTO != null && boardDTO.getFileNames() != null) {
                boardDTO.getFileNames().forEach(s3Uploader::removeS3File);
            }
        }

        boardMapper.deleteSelectedImages(bnos);
        boardMapper.deleteSelected(bnos);
    }

    @Override
    public void removeAll() {
        boardMapper.deleteAllImages();
        boardMapper.deleteAll();
    }
}