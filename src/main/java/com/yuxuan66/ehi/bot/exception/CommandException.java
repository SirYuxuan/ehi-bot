package com.yuxuan66.ehi.bot.exception;

import lombok.Data;
import net.mamoe.mirai.event.Event;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
@Data
public class CommandException extends Exception{

    private Event event;

    public CommandException(Event event,String message){
        super(message);
        this.event = event;
    }
}
