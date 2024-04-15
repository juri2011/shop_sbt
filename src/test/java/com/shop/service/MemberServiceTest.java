package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email("test@test.com")
                .name("홍길동")
                .address("서울시 마포구 합정동")
                .password("1234")
                .build();

        //memberFormDto롤 Member 형으로 변환해서 반환
        return Member.createMember(memberFormDto, passwordEncoder);
    }
    
    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){
        //위에 있는 메소드 실행
        Member member = createMember();

        //생성한 member를 실제로 db에 저장
        Member savedMember = memberService.saveMember(member);
        
        //위에서 생성한 member와 실제로 db에 저장된 member가 같은지 비교
        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());
    }
    
    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){
        //위에 있는 메소드 실행 (두 객체 다 같은 내용)
        Member member1 = createMember();
        Member member2 = createMember();

        memberService.saveMember(member1); //일단 첫번째 Member 객체 저장

        //Junit의 예외 처리 테스트
        Throwable e = assertThrows(IllegalStateException.class, () -> {
            memberService.saveMember(member2);
        });

        //MemberService에서 정의한 에러메시지와 밑의 문자열이 같은지 확인
        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }
    
    
}