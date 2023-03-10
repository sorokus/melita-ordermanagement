package com.melita.ordermanagement.api;

import com.melita.ordermanagement.base.exceptions.BusinessException;
import com.melita.ordermanagement.base.exceptions.SystemException;
import com.melita.ordermanagement.service.OrderApprovalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * @author sorokus.dev@gmail.com
 */

@RestController
@RequestMapping("/api/v1/ordermanagement/approval")
@SecurityRequirement(name = "swagger")
public class OrderApprovalRestController {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderApprovalRestController.class);

    private OrderApprovalService orderApprovalService;

    //    @PostMapping(value = "/approveOrder")
    @GetMapping(value = "/approveOrder/{orderId}")
    // Changed from POST to GET in order be able to call from email message directly. Security is employed, so it's safe.
    public void approveOrder(@PathVariable Long orderId, Principal principal)
            throws BusinessException, SystemException {
        LOGGER.debug("A request to approve Order #" + orderId + " is arrived from '" + principal.getName()+"'");
        orderApprovalService.approveOrder(orderId, principal.getName());
    }

    @Autowired
    public void setOrderApprovalService(OrderApprovalService orderApprovalService) {
        this.orderApprovalService = orderApprovalService;
    }

}