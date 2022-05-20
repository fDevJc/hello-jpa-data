package hello.datajpa.repository;

import hello.datajpa.dto.MemberDto;
import hello.datajpa.entity.Member;
import hello.datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

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

    @Test
    void findByUsernameAndAgeGreaterThen() {
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("AAA", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(members).contains(memberB);
    }

    @Test
    void namedQuery() {
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("BBB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> members = memberRepository.findByUsername2("AAA");

        assertThat(members).contains(memberA);
    }

    @Test
    void namedQuery2() {
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("BBB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> members = memberRepository.findMember("AAA",10);

        assertThat(members).contains(memberA);
    }

    @Test
    void namedQuery3() {
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("BBB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<String> memberUsernameList = memberRepository.findUsernameList();

        assertThat(memberUsernameList).contains("AAA","BBB");
    }

    @Test
    void namedQuery4() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("AAA", 10, teamA);
        Member memberB = new Member("BBB", 20, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        for (MemberDto memberDto : memberDtos) {
            System.out.println("memberDto = " + memberDto);
        }

        assertThat(memberDtos.size()).isEqualTo(2);
        assertThat(memberDtos.get(0).getTeamName()).isEqualTo("teamA");
    }

    @Test
    void namedQuery5() {
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("BBB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<String> usernames = new ArrayList<>();
        usernames.add(memberA.getUsername());
        usernames.add(memberB.getUsername());

        List<Member> membersByUsernames = memberRepository.findMembersByUsernames(usernames);

        assertThat(membersByUsernames).contains(memberA, memberB);
    }

    @Test
    void paging() {
        //given
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));

        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        long totalElements = page.getTotalElements();
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);  //page 번호
        assertThat(page.getTotalPages()).isEqualTo(2);  //전체 page 수
        assertThat(page.isFirst()).isTrue();    //첫 page 인지
        assertThat(page.hasNext()).isTrue();    //다음 page가 있는지
    }

    /*
        page dto로 쉽게 만드는법
     */
    @Test
    void paging1() {
        //given
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));

        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> memberDtos = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        long totalElements = page.getTotalElements();
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);  //page 번호
        assertThat(page.getTotalPages()).isEqualTo(2);  //전체 page 수
        assertThat(page.isFirst()).isTrue();    //첫 page 인지
        assertThat(page.hasNext()).isTrue();    //다음 page가 있는지
    }


    @Test
    void paging2() {
        //given
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int age = 10;

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
//        long totalElements = page.getTotalElements();
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
//        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);  //page 번호
//        assertThat(page.getTotalPages()).isEqualTo(2);  //전체 page 수
        assertThat(page.isFirst()).isTrue();    //첫 page 인지
        assertThat(page.hasNext()).isTrue();    //다음 page가 있는지
    }

    @Test
    void paging3() {
        //given
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int age = 10;

        //when
        List<Member> page = memberRepository.findListByAge(age, pageRequest);

        //then
        assertThat(page.size()).isEqualTo(3);
    }

    @Test
    void bulkUpdate() {
        //given
        Member memberA = new Member("AAA", 10, null);
        Member memberB = new Member("BBB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        int resultCount = memberRepository.bulkAgePlus();

//        em.flush();
        em.clear(); //@Modifying(clearAutomatically = true) 어노테이션 추가하면 굳이 클리어 필요없음

        //then
        assertThat(resultCount).isEqualTo(2);
        Member member = memberRepository.findById(memberA.getId()).get();
        assertThat(member.getAge()).isEqualTo(11);
    }

    @Test
    void fetchJoin() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("mbA", 10, teamA);
        Member memberB = new Member("mbB", 20, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }

        System.out.println("===============================================");

        for (Member member : members) {
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //then
    }

    @Test
    void fetchJoin2() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("mbA", 10, teamA);
        Member memberB = new Member("mbB", 20, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }

        System.out.println("===============================================");

        for (Member member : members) {
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //then
    }

    @Test
    void queryHint() {
        //given
        Member memberA = new Member("mbA", 10, null);
        Member memberB = new Member("mbB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        em.flush();
        em.clear();

        Member member = memberRepository.findById(memberA.getId()).get();
        member.setUsername("memberA");

        /*
            변경감지로 인해 memberA가 업데이트 된다.
            변경감지를 한다는 의미는 원본을 들고있다는 의미
            원본을 들고있다는 건 메모리를 더 잡아 먹고 있다는 의미
            변경감지 체크로직도 비용이 든다

            근데 변경할 생각이 있는게 아니라 조회용으로만 쓸거다
         */
        em.flush();

        //then
    }

    @Test
    void queryHint2() {
        //given
        Member memberA = new Member("mbA", 10, null);
        Member memberB = new Member("mbB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        em.flush();
        em.clear();

        Member member = memberRepository.findReadOnlyById(memberA.getId());
        member.setUsername("memberA");

        /*
            변경감지로 인해 memberA가 업데이트 된다.
            변경감지를 한다는 의미는 원본을 들고있다는 의미
            원본을 들고있다는 건 메모리를 더 잡아 먹고 있다는 의미
            변경감지 체크로직도 비용이 든다

            근데 변경할 생각이 있는게 아니라 조회용으로만 쓸거다
            ==> 힌트를 주면 변경감지를 안한다.
         */
        em.flush();

        //then
    }

    @Test
    void lock() {
        //given
        Member memberA = new Member("mbA", 10, null);
        Member memberB = new Member("mbB", 20, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        em.flush();
        em.clear();

        /*
            select
                member0_.member_id as member_i1_0_,
                member0_.age as age2_0_,
                member0_.team_id as team_id4_0_,
                member0_.username as username3_0_
            from
                member member0_
            where
                member0_.member_id=? for update
         */

        Member member = memberRepository.findLockById(memberA.getId());
        member.setUsername("memberA");

        //then
    }
}