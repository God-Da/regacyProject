package kr.or.oti.b01.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import kr.or.oti.b01.dto.MemberInfoDTO;
import kr.or.oti.b01.dto.MemberJoinDTO;
import kr.or.oti.b01.dto.MemberListDTO;
import kr.or.oti.b01.dto.PageRequestDTO;

@Mapper
public interface MemberMapper {

    // 아이디 중복 확인
    int checkMid(String mid);

    // 회원 정보 등록
    void insert(MemberJoinDTO memberJoinDTO);

    // 회원 권한 등록 (member_role_set 테이블)
    void insertRole(@Param("mid") String mid, @Param("role") String role);

    // 단건 조회 (권한 포함)
    MemberListDTO selectOne(String mid);

    // 시큐리티 로그인/소셜용 단건 조회
    MemberInfoDTO selectWithRoles(String mid);
    MemberInfoDTO selectByEmailWithRoles(String email);

    // 회원 정보 수정 (이메일 수정)
    void update(MemberListDTO memberDTO);

    // 회원 탈퇴 처리 (del = true)
    void updateDel(@Param("mid") String mid, @Param("del") boolean del);

    // 권한 초기화
    void deleteRoles(String mid);

    // 전체 회원 목록 조회
    List<MemberListDTO> selectAll();

    // 페이징/검색 목록 조회
    List<MemberListDTO> selectList(@Param("keyword") String keyword, @Param("skip") int skip, @Param("size") int size);

    // 총 회원 수 카운트
    int getCount(@Param("keyword") String keyword);
}