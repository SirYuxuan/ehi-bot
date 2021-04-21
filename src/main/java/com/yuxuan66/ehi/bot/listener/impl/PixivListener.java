package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageUtils;

import java.util.Random;

/**
 * 色图插件
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class PixivListener implements Listener {


    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false,"色图功能","");
    }

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {

        if(message.equals(".老婆") || message.equals(".色图") || message.equals(".涩图")){

            String body = HttpUtil.get("https://www.pixiv.net/ranking.php?p="+ RandomUtil.randomInt(1,5) +"&format=json");

            JSONArray data = JSONObject.parseObject(body).getJSONArray("contents");

            JSONObject info = data.getJSONObject(RandomUtil.randomInt(0,data.size()-1));

            String url = info.getString("url");


            HttpRequest request = HttpUtil.createGet(url);
            request.header("Referer","https://www.pixiv.net/");
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            String imgId = groupMessageEvent.getGroup().uploadImage(request.execute().bodyStream()).getImageId();
            groupMessageEvent.getGroup().sendMessage(MessageUtils.newImage(imgId));
        }
    }
}
