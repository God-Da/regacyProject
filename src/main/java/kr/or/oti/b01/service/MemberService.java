package kr.or.oti.b01.service;

import java.util.List;
import kr.or.oti.b01.dto.MemberJoinDTO;
import kr.or.oti.b01.dto.MemberListDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;

public interface MemberService {

    static class MidExistException extends Exception {
    }

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;

    List<MemberListDTO> memberList();

    // Spring Data Pageable 대신 PageRequestDTO / PageResponseDTO 적용
    PageResponseDTO<MemberListDTO> memberList(String keyword, PageRequestDTO pageRequestDTO);

    void withdraw(String mid);

    MemberListDTO getMember(String mid);

    void modify(MemberListDTO memberDTO);
}