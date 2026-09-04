package kr.or.oti.b01.controller;

import java.security.Principal;
import java.util.List;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 추후 Spring Security 연동 시 주석 해제
// import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardservice;

    // 게시판 목록
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        log.info("board list... {}", pageRequestDTO);
        model.addAttribute("pageResponseDTO", boardservice.listWithAll(pageRequestDTO));
    }

    // 게시글 상세 조회
    // @PreAuthorize("isAuthenticated()")
    @GetMapping("/read")
    public void read(long bno, PageRequestDTO pageRequestDTO, Model model) {
        BoardDTO boardDTO = boardservice.get(bno);
        log.info("boardDTO >>> {}", boardDTO);
        log.info("fileNames >>> {}", boardDTO.getFileNames());
        log.info("imageUrls >>> {}", boardDTO.getImageUrls());
        
        model.addAttribute("dto", boardDTO);
    }

    // 게시글 수정 화면 진입
    @GetMapping("/modify")
    public String modify(long bno, Principal principal, PageRequestDTO pageRequestDTO, Model model, RedirectAttributes redirectAttributes) {
        BoardDTO boardDTO = boardservice.get(bno);

        // 시큐리티 연동 전에는 principal이 null이므로 임시 허용 처리
        if (principal == null || principal.getName().equals(boardDTO.getWriter())) {
            model.addAttribute("dto", boardDTO);
            return "board/modify";
        } else {
            redirectAttributes.addFlashAttribute("error", "로그인한 사용자와 게시물 작성자가 달라 수정이 불가합니다.");
            return "redirect:/board/read?bno=" + bno + "&" + pageRequestDTO.getLink();
        }
    }

    // 게시글 수정 처리
    // @PreAuthorize("principal.username == #dto.writer")
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO, @Valid BoardDTO dto, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.info("errors: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/modify?bno=" + dto.getBno() + "&" + pageRequestDTO.getLink();
        }

        boardservice.modify(dto);
        return "redirect:/board/list?" + pageRequestDTO.getLink();
    }

    // 게시글 단건 삭제 처리
    // @PreAuthorize("principal.username == #dto.writer")
    @PostMapping("/remove")
    public String remove(long bno, BoardDTO dto, PageRequestDTO pageRequestDTO) {
        boardservice.remove(bno);
        return "redirect:/board/list?" + pageRequestDTO.getLink();
    }

    // 선택 게시글 일괄 삭제
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/removeSelected")
    public String removeSelected(@RequestParam("bno") List<Long> bnos, PageRequestDTO pageRequestDTO) {
        boardservice.removeSelected(bnos);
        return "redirect:/board/list?" + pageRequestDTO.getLink();
    }

    // 전체 삭제
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/removeAll")
    public String removeAll() {
        boardservice.removeAll();
        return "redirect:/board/list";
    }

    // 게시글 등록 화면 진입
    // @PreAuthorize("hasRole('USER')")
    @GetMapping("/register")
    public void register() {
    }

    // 게시글 등록 처리
    @PostMapping("/register")
    public String register(@Valid BoardDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.info("errors: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }

        log.info("fileNames = {}", dto.getFileNames());

        boardservice.register(dto);
        redirectAttributes.addFlashAttribute("result", dto.getBno());

        return "redirect:/board/list";
    }
}