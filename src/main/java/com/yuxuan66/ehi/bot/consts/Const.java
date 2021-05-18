package com.yuxuan66.ehi.bot.consts;

import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.utils.ClassScanner;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.utils.BotConfiguration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
@Slf4j
public class Const {

    public static String LINE = "\r\n";
    public final static List<Listener> MESSAGE_HANDLERS = new ArrayList<>();
    public final static Map<String, HandlerInfo> HANDLER_INFO_MAP = new HashMap<>();

    static {

        // 初始化系统内的Handler
        try {
            List<Class<?>> classList = ClassScanner.scanOnePackage("com.yuxuan66.ehi.bot.listener.impl");
            for (Class<?> messageHandler : classList) {
                if (Listener.class.isAssignableFrom(messageHandler)) {
                    Listener messageHandler1 = (Listener) messageHandler.newInstance();
                    MESSAGE_HANDLERS.add(messageHandler1);
                    HandlerInfo handlerInfo = messageHandler1.register();
                    if (handlerInfo != null) {
                        HANDLER_INFO_MAP.put(handlerInfo.getName(), handlerInfo);
                    }
                }
            }
            //log.info("加载完毕,共加载{}个Handler", MESSAGE_HANDLERS.size());
        } catch (Exception e) {
            //log.error("初始化失败,加载Handler Class失败");
        }

    }

    /**
     * 全局唯一的机器人对象
     */
    public final static Bot BOT = BotFactoryJvm.newBot(2438372649L, "yuxuanll2012", new BotConfiguration() {{
        fileBasedDeviceInfo("deviceInfo.json");
        setProtocol(MiraiProtocol.ANDROID_PHONE);
    }});
}
