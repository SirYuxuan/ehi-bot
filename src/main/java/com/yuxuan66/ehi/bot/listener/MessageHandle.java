package com.yuxuan66.ehi.bot.listener;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.yuxuan66.ehi.bot.consts.Const;
import com.yuxuan66.ehi.bot.exception.CommandException;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.MessageEvent;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class MessageHandle {

    /**
     * 全局消息处理
     */
    public static void handle(Event event) throws CommandException {

        for (Listener listener : Const.MESSAGE_HANDLERS) {

            ExecutorService executor = Executors.newSingleThreadExecutor();

            FutureTask<Object> future = new FutureTask<>(() -> {
                listener.gotAMessage(event, ((MessageEvent) event).getMessage().contentToString());
                return null;
            });

            executor.execute(future);

            try {
                future.get(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                listener.sendMessage(event,"查询过慢,请耐心等待");
            }

        }

    }
}
