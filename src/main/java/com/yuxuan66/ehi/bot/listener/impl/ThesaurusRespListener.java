package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.db.Entity;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.model.PluginCk;
import com.yuxuan66.ehi.bot.support.database.SQLKit;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.data.MessageUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词库响应
 * @author Sir丶雨轩
 * @date 2020/8/26
 */
public class ThesaurusRespListener implements Listener {


    public static Map<String,List<PluginCk>> thesaurusMap = new HashMap<>();

    public static List<PluginCk> refresh(String group) {
        return SQLKit.getSQLManager().lambdaQuery(PluginCk.class).andEq("group",group).select();
    }

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {


        String groupId = String.valueOf(getGroup(event).getId());

        List<PluginCk> thesaurusList = thesaurusMap.get(groupId );

        if(thesaurusList == null){
            thesaurusList = refresh(groupId );
            thesaurusMap.put(groupId ,thesaurusList);
        }

        if(thesaurusList == null){
            return;
        }


        for (int i = 0; i < thesaurusList.size(); i++) {
            PluginCk tmp = thesaurusList.get(i);
            if (message.contains(tmp.getKey())) {
                String value = tmp.getValue();
                if (tmp.getValue().contains("##")) {

                    String[] msg = value.split("##");
                    value = msg[RandomUtil.randomInt(0, msg.length - 1)];
                }
                if (value.startsWith("@")) {
                    sendMessage(event,value.substring(1));
                } else {
                    sendMessage(event, MessageUtils.newChain(value),false);
                }
            }
        }
    }

    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false, "词库响应", "根据词库管理所设置的信息进行自动回复");
    }

}
