package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

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

    public void createItemList2(){
        for(int i=1; i<=5; i++){
            Item item = Item.builder()
                    .itemNm("테스트 상품" + i)
                    .price(10000+i)
                    .itemDetail("테스트 상품 상세 설명"+i)
                    .itemSellStatus(ItemSellStatus.SELL)
                    .stockNumber(100)
                    .regTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            itemRepository.save(item); //실제도 데이터베이스에 저장
        }

        for(int i=6; i<=10; i++){
            Item item = Item.builder()
                    .itemNm("테스트 상품" + i)
                    .price(10000+i)
                    .itemDetail("테스트 상품 상세 설명"+i)
                    .itemSellStatus(ItemSellStatus.SOLD_OUT)
                    .stockNumber(0)
                    .regTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            itemRepository.save(item); //실제도 데이터베이스에 저장
        }
    }

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
    @DisplayName("Querydsl 조회 테스트 2")
    public void queryDslTest2(){
        this.createItemList2();

        // 쿼리에 들어갈 조건을 만들어주는 빌러(Predicate 구현)
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QItem item = QItem.item;

        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL"; //값이 고정되어 있으므로, if문으로 조건 추가 한것과 동일한 결과를 얻는다

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%")); //"테스트 상품 상세 설명" 이 퐇마
        booleanBuilder.and(item.price.gt(price)); //10003보다 큰

        //StringUtils: java가 제공하는 string 기능을 강화한 클래스
        //itemSellStat이 "SELL"인 경우에만 쿼리에 추가
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0,5); //페이지 번호, 페이지당 조회할 데이터 개수
        Page<Item> itemPagingResult =
                itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " +
                            itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();

        for(Item resultItem : resultItemList){
            System.out.println(resultItem.toString());
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