package com.splittree.rocketmq;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

/**
 * Created by yangmingquan on 2018/9/30.
 */
@Service
public class RocketMqTransactionCheckListener implements TransactionCheckListener{

    @Override
    public LocalTransactionState checkLocalTransactionState(MessageExt messageExt) {
        System.out.println("未决事务，服务器回调客户端" + new String(messageExt.getBody().toString()));
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
