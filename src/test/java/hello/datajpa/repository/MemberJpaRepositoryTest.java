package hello.datajpa.repository;

import hello.datajpa.entity.Member;
import hello.datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    TeamJpaRepository teamJpaRepository;

    @PersistenceContext
    EntityManager em;

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

    @Test
    void findByUsernameAndAgeGreaterThen() {
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("AAA", 20, null);
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(members).contains(memberB);
    }

    @Test
    void namedQuery() {
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("BBB", 20, null);
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        List<Member> members = memberJpaRepository.findByUsername2("AAA");

        assertThat(members).contains(memberA);
    }

    @Test
    void bulkUpdate() {
        //given
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("BBB", 20, null);
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);
        //when
        int resultCount = memberJpaRepository.bulkAgePlus(10);

        em.flush();
        em.clear();

        //then
        assertThat(resultCount).isEqualTo(2);
        Member member = memberJpaRepository.findById(memberA.getId());
        assertThat(member.getAge()).isEqualTo(11);
    }

    @Test
    void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamJpaRepository.save(teamA);
        teamJpaRepository.save(teamB);

        Member memberA = new Member("mbA", 10, teamA);
        Member memberB = new Member("mbB", 20, teamB);
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberJpaRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }

        System.out.println("===============================================");

        for (Member member : members) {
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //then
    }
}