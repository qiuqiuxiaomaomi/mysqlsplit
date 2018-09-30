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

    @Tcc(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean pay(){
        try{
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Transactional
    public boolean confirm(){
        return true;
    }

    @Transactional
    public boolean cancel(){
        return true;
    }
}
