package com.melita.ordermanagement.service;

/*
 * @author sorokus.dev@gmail.com
 */

import com.melita.ordermanagement.base.exceptions.SystemException;
import com.melita.ordermanagement.model.dto.OrderDto;
import com.melita.ordermanagement.model.entity.Product;

import java.util.List;

public interface OrderPlacementService {

    List<Product> getAvailableProductsWithPackages();

    void placeOrder(OrderDto orderData) throws SystemException;
}
