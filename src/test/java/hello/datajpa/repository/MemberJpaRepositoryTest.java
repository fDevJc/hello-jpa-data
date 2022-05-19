package hello.datajpa.repository;

import hello.datajpa.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {
        //given
        Member member = new Member("memberA");
        
        //when
        Member savedMember = memberJpaRepository.save(member);
        Member foundMember = memberJpaRepository.findById(savedMember.getId());

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
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        Member foundMemberA = memberJpaRepository.findOptionalById(memberA.getId()).get();
        Member foundMemberB = memberJpaRepository.findOptionalById(memberB.getId()).get();
        assertThat(foundMemberA).isEqualTo(memberA);
        assertThat(foundMemberB).isEqualTo(memberB);
        //then

        List<Member> members = memberJpaRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(memberA);
        memberJpaRepository.delete(memberB);

        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }
}