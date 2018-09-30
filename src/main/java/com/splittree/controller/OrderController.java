package com.splittree.controller;

import com.splittree.entity.Order;
import com.splittree.entity.Org;
import com.splittree.service.MysqlXaTransactionService;
import com.splittree.service.OrderService;
import com.splittree.service.OrgService;
import com.splittree.service.RocketMqTransactionProviderService;
import io.swagger.annotations.*;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;


/**
 * Created by yangmingquan on 2018/9/4.
 */
@Api(value = "OrderController", description = "党务书库相关api")
@RestController("/order")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private MysqlXaTransactionService mysqlXaTransactionService;
    @Autowired
    private RocketMqTransactionProviderService rocketMqTransactionProviderService;

    //测试
    @RequestMapping(value="saveOrder")
    public String saveOrder() {
        Order order = new Order();
        for (long i = 0;  i< 100; i++) {
            order.setStatus("0");
            order.setUserId(i);
            order.setOrderId(i);
            orderService.updateTransactional(order);
        }
        return null;
    }

    @RequestMapping(value="queryOrder")
    public Object queryOrder() {
        return orderService.getTransactional();
    }

    @RequestMapping(value="saveOrg")
    public String saveOrg() throws SQLException, ClassNotFoundException {
        Org org = new Org();
        for (int i = 0;  i< 100; i++) {
            org.setId(i);
            org.setName(i+"");
            org.setCode(i+"");
            orgService.updateTransactional(org);
        }
        return null;
    }

    @RequestMapping(value="queryOrg")
    public Object queryOrg() {
        return orgService.getTransactional();
    }

    @ApiOperation(value = "Xa事务操作接口",notes = "Xa事务操作接口",httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200,message = "")
    })
    @RequestMapping(value = "xa")
    public Object xa(){
        mysqlXaTransactionService.mysqlXaTransactionServiceExcute();
        return "ok";
    }

    @ApiOperation(value = "rocketmq事务操作接口",notes = "rocketmq事务操作接口",httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200,message = "")
    })
    @RequestMapping(value = "rockemq")
    public Object rocketmq(){
        Message message = new Message();
        rocketMqTransactionProviderService.sendMessage(message);
        return "ok";
    }
}
