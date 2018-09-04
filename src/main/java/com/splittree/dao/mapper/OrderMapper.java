package com.splittree.dao.mapper;

import com.splittree.entity.Order;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by yangmingquan on 2018/9/4.
 */
public interface OrderMapper  {

    List<Order> getAll();

    void update(Order order);

    void insert(Order order);
}
