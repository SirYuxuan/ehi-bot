package com.yuxuan66.ehi.bot;

import com.yuxuan66.ehi.bot.consts.Const;
import com.yuxuan66.ehi.bot.listener.EhiListenerHost;
import com.yuxuan66.ehi.bot.support.database.SQLKit;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.utils.BotConfiguration;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class BotApp {

    public static void main(String[] args) {

            Const.BOT.login();
            Events.registerEvents(new EhiListenerHost());

            Const.BOT.join();

    }
}
