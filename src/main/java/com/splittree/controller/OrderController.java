package com.splittree.controller;

import com.splittree.entity.Order;
import com.splittree.service.OrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by yangmingquan on 2018/9/4.
 */
@Api(value = "OrderController", description = "党务书库相关api")
@RestController("/order")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //测试
    @RequestMapping(value="update1")
    public String updateTransactional() {
        Order order = new Order();
        for (long i = 0;  i< 100; i++) {
            order.setStatus("0");
            order.setUserId(i);
            order.setOrderId(i);
            orderService.updateTransactional(order);
        }
        return "test";
    }

    //测试
    @RequestMapping(value="get1")
    public Object getTransactional() {
        Order order = new Order();
        return orderService.getTransactional();
    }
}
