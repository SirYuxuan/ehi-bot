package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.util.RandomUtil;
import com.yuxuan66.ehi.bot.consts.Const;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.model.PluginCk;
import com.yuxuan66.ehi.bot.support.database.SQLKit;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词库响应
 *
 * @author Sir丶雨轩
 * @date 2020/8/26
 */
public class ThesaurusListener implements Listener {


    @Override
    public void gotAMessage(Event event, String message) throws CommandException {

        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            if (groupMessageEvent.getSender().getPermission() != MemberPermission.MEMBER || groupMessageEvent.getSender().getId() == 1718018032L) {
                if (message.startsWith("#添加词库")) {
                    String[] arr = message.split(" ");
                    if (arr.length < 2) {
                        sendMessage(event, MessageUtils.newChain("对不起 命令语法错误"), true);
                        return;
                    }

                    PluginCk pluginCk = new PluginCk();
                    pluginCk.setGroup(String.valueOf(groupMessageEvent.getGroup().getId()));
                    pluginCk.setCreateQQ(String.valueOf(groupMessageEvent.getSender().getId()));
                    pluginCk.setKey(arr[1]);
                    pluginCk.setValue(arr[2]);
                    SQLKit.getSQLManager().insert(pluginCk);
                    sendMessage(event,"词库添加成功");
                    ThesaurusRespListener.thesaurusMap.remove(String.valueOf(groupMessageEvent.getGroup().getId()));

                } else if (message.startsWith("#删除词库")) {
                    String[] arr = message.split(" ");
                    if (arr.length != 2) {
                        sendMessage(event, "对不起 命令语法错误");
                        return;
                    }
                }
            }

        }
    }

    private String help() {
        StringBuffer help = new StringBuffer();
        help.append("功能:");
        help.append(Const.LINE);
        help.append("可以添加机器人自动回复的关键字及回复,相同关键字会覆盖,多个回复使用##分割,将会随机使用");
        help.append(Const.LINE);
        help.append("使用方法");
        help.append(Const.LINE);
        help.append("1.#添加词库 xxx xxxx##xxxx");
        help.append(Const.LINE);
        help.append("2.#删除词库 xxx");
        return help.toString();
    }

    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false, "词库管理", help());
    }

}
