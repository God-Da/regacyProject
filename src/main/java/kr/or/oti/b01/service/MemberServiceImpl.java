package kr.or.oti.b01.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.oti.b01.dto.MemberJoinDTO;
import kr.or.oti.b01.dto.MemberListDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid = memberJoinDTO.getMid();
        int count = memberMapper.checkMid(mid);

        if (count > 0) {
            throw new MidExistException();
        }

        if (passwordEncoder != null) {
            memberJoinDTO.setMpw(passwordEncoder.encode(memberJoinDTO.getMpw()));
        }

        memberMapper.insert(memberJoinDTO);

        // Enum 객체 없이 문자열 "USER" 바로 등록
        memberMapper.insertRole(mid, "USER");
        log.info("Member Joined: {}", memberJoinDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberListDTO> memberList() {
        return memberMapper.selectAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MemberListDTO> memberList(String keyword, PageRequestDTO pageRequestDTO) {
        List<MemberListDTO> dtoList = memberMapper.selectList(keyword, pageRequestDTO.getSkip(), pageRequestDTO.getSize());
        int total = memberMapper.getCount(keyword);

        return PageResponseDTO.<MemberListDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    @Override
    public void withdraw(String mid) {
        memberMapper.updateDel(mid, true);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberListDTO getMember(String mid) {
        return memberMapper.selectOne(mid);
    }

    @Override
    public void modify(MemberListDTO memberDTO) {
        memberMapper.update(memberDTO);

        if (memberDTO.getRoleSet() != null && !memberDTO.getRoleSet().isEmpty()) {
            memberMapper.deleteRoles(memberDTO.getMid());
            // role이 이미 String이므로 그대로 등록
            memberDTO.getRoleSet().forEach(role -> {
                memberMapper.insertRole(memberDTO.getMid(), role);
            });
        }
    }
}