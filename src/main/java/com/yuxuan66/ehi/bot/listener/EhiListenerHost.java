package com.yuxuan66.ehi.bot.listener;

import com.yuxuan66.ehi.bot.consts.Const;
import com.yuxuan66.ehi.bot.exception.CommandException;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class EhiListenerHost extends SimpleListenerHost {

    @EventHandler
    public ListeningStatus onTempMessage(TempMessageEvent event) throws Exception {
        MessageHandle.handle(event);
        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus onFriendMessage(FriendMessageEvent event) throws Exception {
        MessageHandle.handle(event);
        return ListeningStatus.LISTENING;
    }
    @EventHandler
    public ListeningStatus onGroupMessage(GroupMessageEvent event) throws Exception {
        MessageHandle.handle(event);
        return ListeningStatus.LISTENING;
    }
    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {

        if(exception instanceof CommandException){

            CommandException commandException = (CommandException) exception;
            Event event = commandException.getEvent();

            if(event instanceof GroupMessageEvent){
                GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
                groupMessageEvent.getGroup().sendMessage(commandException.getMessage());
            }

        }
    }

}
