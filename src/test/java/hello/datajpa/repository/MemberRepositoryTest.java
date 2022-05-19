package hello.datajpa.repository;

import hello.datajpa.entity.Member;
import hello.datajpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void memberTest() {
        //given
        Member member = new Member("memberA");
        //when
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertThat(foundMember).isEqualTo(member);
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void memberBasicCRUD() {
        //given
        Member memberA = new Member("memberA");
        Member memberB = new Member("memberAB");

        //when
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        Member foundMemberA = memberRepository.findById(memberA.getId()).get();
        Member foundMemberB = memberRepository.findById(memberB.getId()).get();
        assertThat(foundMemberA).isEqualTo(memberA);
        assertThat(foundMemberB).isEqualTo(memberB);
        //then

        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(memberA);
        memberRepository.delete(memberB);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }
}