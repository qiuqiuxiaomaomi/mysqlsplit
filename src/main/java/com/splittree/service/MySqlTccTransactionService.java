package com.splittree.service;

import com.splittree.annotation.Tcc;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yangmingquan on 2018/9/29.
 * TCC 分布式事务
 */
@Service
public class MySqlTccTransactionService {

    @Transactional
    @Tcc(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean pay(){
        boolean result = false;
        try{
            // 减库存
            result = decreaseStorage();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Transactional
    public boolean confirm(){
        return true;
    }

    @Transactional
    public boolean cancel(){
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean decreaseStorage(){
        //库存不足 throw Exception
        //库存充足
        return true;
    }
}
