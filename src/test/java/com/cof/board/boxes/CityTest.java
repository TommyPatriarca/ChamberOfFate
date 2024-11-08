package com.cof.board.boxes;

import com.cof.board.Groups;
import com.cof.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    private City city;
    private Player player;

    @BeforeEach
    void init() {
        city = new City(Groups.YELLOW, "Traona", 60, 50, 200, 10);
        player = new Player("test_name");
    }

    @Test
    void getHouseNumber() {
        assertEquals(city.getHouseNumber(),0);
        city.buyHouse(player);
        assertEquals(city.getHouseNumber(),1);
    }

    @Test
    void buyHouse() {
        city.buyPropriety(player);
        int money = player.getMoney();
        int houses = city.getHouseNumber();
        city.buyHouse(player);
        assertEquals(money-city.getHousePrice(1), player.getMoney());
        assertEquals(houses+1, city.getHouseNumber());
        player.removeMoney(1480);
        money = player.getMoney();
        city.buyHouse(player);
        assertEquals(money, player.getMoney());
    }

    @Test
    void getHousePrice() {
        assertEquals(city.getHousePrice(1),20);
        assertEquals(city.getHousePrice(2),30);
        assertEquals(city.getHousePrice(3),40);
        assertEquals(city.getHousePrice(4),50);
        assertEquals(city.getHousePrice(5),60);
    }

    @Test
    void buyPropriety() {
        int money = player.getMoney();
        city.buyPropriety(player);
        assertEquals(money-city.getPrice(), player.getMoney());
        player.removeMoney(1420);
        money = player.getMoney();
        city.buyPropriety(player);
        assertEquals(money, player.getMoney());
    }

    @Test
    void sellHouse() {
        city.buyPropriety(player);
        city.buyHouse(player);
        int money = player.getMoney();
        city.sellHouse(player);
        assertEquals(money + (city.getHousePrice(1)/2) , player.getMoney());
    }

    @Test
    void sellPropriety() {
        city.buyPropriety(player);
        int money = player.getMoney();
        city.sellPropriety(player);
        assertEquals(money + (city.getPrice()/2),player.getMoney());
    }

    @Test
    void getNome() {
        assertEquals(player.getName(),"test_name");
    }

    @Test
    void getGroup() {
        assertEquals(city.getGroup(),Groups.YELLOW);
    }

    @Test
    void getOwner() {
        assertNull(city.getOwner());
        city.buyPropriety(player);
        assertEquals(city.getOwner(),player);
        city.sellPropriety(player);
        assertNull(city.getOwner());
    }

    @Test
    void getPrice() {
        assertEquals(city.getPrice(),60);
    }

    @Test
    void isOwned() {
        assertFalse(city.isOwned());
        city.buyPropriety(player);
        assertTrue(city.isOwned());
        city.sellPropriety(player);
        assertFalse(city.isOwned());
    }

    @Test
    void getPaid() {
        int money = player.getMoney();
        city.getPaid(player);
        assertEquals(money-city.getPayment(city.getHouseNumber()), player.getMoney());
        city.buyPropriety(player);
        money = player.getMoney();
        city.getPaid(player);
        assertEquals(money, player.getMoney());
    }
}