package com.yuxuan66.ehi.bot.utils;

import com.yuxuan66.ehi.bot.exception.CommandException;
import net.mamoe.mirai.event.Event;

/**
 * 处理命令行
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class CommandUtil {

    public static Command parse(String text)  {

        String[] arr = text.split(" ");

        StringBuilder name = new StringBuilder();
        for (int i = 1; i < arr.length; i++) {
            name.append(arr[i]).append(" ");
        }
        if(name.length() > 0){
            name.deleteCharAt(name.length() - 1);
        }

        Command command = new Command();
        command.setPrefix(arr[0]);
        command.setName(name.toString());
        command.setLen(arr.length);
        return command;
    }

}
