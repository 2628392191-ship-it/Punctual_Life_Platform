package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface shoppingCartService {

    List<ShoppingCart> list();

    void addCart(ShoppingCartDTO shoppingCartDTO);

    void deleteCart();
}
