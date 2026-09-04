package kr.or.oti.b01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.oti.b01.dto.MemberJoinDTO;
import kr.or.oti.b01.dto.MemberListDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 추후 Spring Security 연동 시 활성화
// import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public void login(String errorCode, String logout) {
        log.info("login() 호출..");
        if (logout != null) {
            log.info("user Logout..");
        }
    }

    // 회원가입 진입
    @GetMapping("/join")
    public void joinGET() {
        log.info("joinGET() 호출..");
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String joinPOST(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes) {
        log.info("joinPOST() 호출.. memberJoinDTO: {}", memberJoinDTO);

        try {
            memberService.join(memberJoinDTO);
        } catch (MemberService.MidExistException e) {
            redirectAttributes.addFlashAttribute("error", "mid");
            return "redirect:/member/join";
        }
        redirectAttributes.addAttribute("result", "success");
        return "redirect:/member/login";
    }

    // 회원 목록 조회
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public String memberList(
            @RequestParam(defaultValue = "") String keyword,
            PageRequestDTO pageRequestDTO,
            Model model) {

        log.info("memberList() 호출.. keyword: {}, pageRequestDTO: {}", keyword, pageRequestDTO);

        PageResponseDTO<MemberListDTO> result = memberService.memberList(keyword, pageRequestDTO);

        model.addAttribute("pageResponse", result);
        model.addAttribute("keyword", keyword);

        return "member/memberList";
    }

    // 회원 탈퇴 처리
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/remove")
    public String remove(String mid, RedirectAttributes redirectAttributes) {
        memberService.withdraw(mid);

        redirectAttributes.addFlashAttribute("result", mid + " 회원이 탈퇴 처리되었습니다.");
        return "redirect:/member/list";
    }

    // 회원 수정 화면
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/modify")
    public String modifyGET(String mid, Model model) {
        log.info("modifyGET() 호출.. mid: {}", mid);

        MemberListDTO memberDTO = memberService.getMember(mid);
        model.addAttribute("member", memberDTO);

        return "member/memberModify";
    }

    // 회원 수정 처리
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/modify")
    public String modifyPOST(MemberListDTO memberDTO, RedirectAttributes redirectAttributes) {
        log.info("modifyPOST() 호출.. memberDTO: {}", memberDTO);

        memberService.modify(memberDTO);

        redirectAttributes.addFlashAttribute("result", "회원 정보가 수정되었습니다.");
        return "redirect:/member/list";
    }
}