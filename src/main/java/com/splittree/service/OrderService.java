package com.splittree.service;

import com.splittree.dao.mapper.OrderMapper;
import com.splittree.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yangmingquan on 2018/9/4.
 */
@Service("orderService")
public class OrderService{

    @Autowired
    OrderMapper orderMapper;

    public void updateTransactional(Order order) {
        try{
            orderMapper.insert(order);
        }catch(Exception e){
            throw e;   // 事物方法中，如果使用trycatch捕获异常后，需要将异常抛出，否则事物不回滚。
        }

    }

    public List<Order> getTransactional() {
        List<Order> objectList = null;
        try{
            objectList = orderMapper.getAll();
        }catch(Exception e){
            throw e;   // 事物方法中，如果使用trycatch捕获异常后，需要将异常抛出，否则事物不回滚。
        }
        return objectList;
    }
}
