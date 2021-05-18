package com.yuxuan66.ehi.bot.listener;

import com.yuxuan66.ehi.bot.exception.CommandException;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public interface Listener {

    /**
     * 是否只允许群使用
     * @return
     */
    default boolean isOnlyGroup(){return true;}

    /**
     * 是否启动处理器
     * @return 默认启动
     */
    default boolean isStart(){
        return true;
    }

    /**
     * 注册插件信息
     * @return 插件信息
     */
    HandlerInfo register();

    void gotAMessage(Event event,String message) throws CommandException;

    default String getMessage(Event event){
        if(event instanceof MessageEvent){
            return ((MessageEvent) event).getMessage().contentToString();
        }
        return "";
    }
    default MessageChain getMessageChain(Event event){
        if(event instanceof MessageEvent){
            return ((MessageEvent) event).getMessage();
        }
        return MessageUtils.newChain();
    }

    default void sendMessage(Event event, MessageChain message,boolean isAt){
        if(event instanceof MessageEvent){
            if(event instanceof GroupMessageEvent){
                ((GroupMessageEvent)event).getGroup().sendMessage(isAt?new At(((GroupMessageEvent) event).getSender()).plus("\r\n").plus(message):message);
            }else{
                ((MessageEvent) event).getSender().sendMessage(message);
            }
        }
    }
    default void sendMessage(Event event, String message){
        sendMessage(event, MessageUtils.newChain(message),true);
    }

    default Long getQQ(Event event){
        return ((MessageEvent)event).getSender().getId();
    }

    default Group getGroup(Event event){
        return ((GroupMessageEvent)event).getGroup();
    }


}
