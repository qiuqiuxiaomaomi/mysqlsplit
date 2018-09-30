package com.splittree.rocketmq;

import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

/**
 * Created by yangmingquan on 2018/9/29.
 * 消息事务
 */
@Service
public class RocketMqTransactionProvider implements LocalTransactionExecuter{

    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message message, Object o) {
        System.out.println("执行本地事务 msg = " + new String(message.getBody()));
        String tags = message.getTags();
        if (tags.equals("transaction2")){
            System.out.println("操作失败，需要回滚");
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }
}
