package hello.datajpa.repository;

import hello.datajpa.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ItemRepositoryTest {
    @Autowired ItemRepository itemRepository;

    @Test
    void save() {
        //given
        Item item = new Item("A","itemA");

        //when
        itemRepository.save(item);

        //then
    }
}