package com.melita.ordermanagement.service.impl;

import com.melita.ordermanagement.base.exceptions.SystemException;
import com.melita.ordermanagement.model.dto.OrderDto;
import com.melita.ordermanagement.model.entity.Product;
import com.melita.ordermanagement.repository.ProductRepository;
import com.melita.ordermanagement.service.OrderPlacementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @author sorokus.dev@gmail.com
 */

@Service
public class OrderPlacementServiceImpl implements OrderPlacementService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderPlacementServiceImpl.class);

    private ProductRepository productRepository;
    private RabbitTemplate rabbitTemplate;

    @Value("${amqp.exchange.name}")
    private String exchangeName;

    @Value("${amqp.routing.key}")
    private String routingKey;

    @Override
    public List<Product> getAvailableProductsWithPackages() {
        LOGGER.debug("Getting available products with packages from DB");
        return productRepository.findAllByOrderByIdAsc();
    }

    @Override
    public void placeOrder(OrderDto orderData) throws SystemException {
        LOGGER.debug("A new Order being placed in the Queue");
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, orderData);
        } catch (AmqpException amqpe) {
            throw new SystemException("AMPQ error while sending to queue", amqpe);
        }
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

}
