package hello.datajpa.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/*
    왠만하면 generatedvalue를 사용하는게 좋지만
    사용할 수 없는상황에서 다른 id 값을 사용하게 되면
    spring data jpa 내부에서는 persist가 아니라 merge를 실행하게 된다.
    이러한 문제를 해결하기 위해 나온 인터페이스가 Persistable<> 이다
    isNew를 오버라이드 해서 어떨때 새로운 객체인지 직접 지정해주면 된다.
    권장하는 방법은 @CreatedDate를 사용했을때 해당값이 null인경우 새로운 객체라고 판단하면 된다.
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
    @Id
//    @GeneratedValue
    private String id;
    private String name;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
