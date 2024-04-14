package com.shop.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {
    @Autowired //test에서는 생성자 주입이 안됨
    ItemRepository itemRepository;

    @Autowired
    EntityManager em;


    public void createItemList(){
        for(int i=0; i<=10; i++){
            Item item = Item.builder()
                    .itemNm("테스트 상품" + i)
                    .price(10000+i)
                    .itemDetail("테스트 상품 상세 설명"+i)
                    .itemSellStatus(ItemSellStatus.SELL)
                    .stockNumber(100)
                    .regTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            Item savedItem = itemRepository.save(item); //실제도 데이터베이스에 저장
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트 1")
    public void queryDslTest(){
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;

        List<Item> itemList = queryFactory.select(qItem)
                .from(qItem)
                .where(qItem.itemDetail.like("%테스트%"))
                .orderBy(qItem.price.desc())
                .fetch(); //이 시점에 쿼리문이 실행된다. 쿼리 결과를 리스트로 반환

        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemList(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList =
                itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }
    
    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList =
                itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = Item.builder()
                .itemNm("테스트 상품")
                .price(10000)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .regTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        Item savedItem = itemRepository.save(item); //실제도 데이터베이스에 저장
        System.out.println(savedItem.toString());
    }
}