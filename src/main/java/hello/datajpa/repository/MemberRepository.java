package hello.datajpa.repository;

import hello.datajpa.dto.MemberDto;
import hello.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> , MemberCustomRepository{
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

//    @Query(name = "Member.findByUsername2")
    List<Member> findByUsername2(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findMember(@Param("username") String username, @Param("age") int age);

    @Query("select m from Member m where m.username in :usernames")
    List<Member> findMembersByUsernames(@Param("usernames") Collection<String> usernames);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new hello.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    Page<Member> findByAge(int age, Pageable pageable);

    /*
        카운트 쿼리를 별도로 만들수 있도록 제공한다.
        left outer 조인의 경우 전체 카운트의 변동이 없기때문에 최적화를 위해 따로 만드는게 좋을때가 있다.
     */
    @Query(value = "select m from Member m", countQuery = "select count(m) from Member m")
    Page<Member> findCountQueryByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    List<Member> findListByAge(int age, Pageable pageable);

//    @Modifying(clearAutomatically = true)
    @Modifying
    @Query("update Member m set m.age = m.age+1")
    int bulkAgePlus();

    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberFetchJoin2();


    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockById(Long Id);
}
