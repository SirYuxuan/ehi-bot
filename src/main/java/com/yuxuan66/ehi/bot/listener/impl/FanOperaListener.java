package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.ehi.bot.consts.Const;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sir丶雨轩
 * @date 2020/9/6
 */
public class FanOperaListener implements Listener {

    private static String secondToTime(long second) {
        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < days){
            return days+":"+hours+":"+minutes+":"+second;
        }else {
            return hours+":"+minutes+":"+second;
        }
    }

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {
        if (message.contains("番剧识别")) {

            List<String> file = new ArrayList<>();
            for (SingleMessage singleMessage : getMessageChain(event)) {
                if (Image.class.isAssignableFrom(singleMessage.getClass())) {
                    String imagePath = Const.BOT.queryImageUrl((Image) singleMessage);
                    file.add(imagePath);
                }
            }
            if (file.isEmpty()) {
                return;
            }
            if (file.size() > 1) {
                sendMessage(event,"番剧识别每次仅可接收一张图片");
                return;
            }
            String url = file.get(0);
            sendMessage(event,"请稍等正在解析图片.");
            try{
                String body = HttpUtil.get("https://trace.moe/api/search?url="+ URLUtil.encode(url));
                if (JSONUtil.isJson(body)) {
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    JSONObject data = jsonObject.getJSONArray("docs").getJSONObject(0);
                    String title_native = data.getString("title_native");
                    String title_chinese = data.getString("title_chinese");
                    String title_english = data.getString("title_english");
                    String filename = data.getString("filename");
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("欢迎使用雨轩番剧搜索\r\n");
                    stringBuffer.append("中文名:"+title_chinese+"\r\n");
                    stringBuffer.append("英文名:"+title_english+"\r\n");
                    stringBuffer.append("日文名:"+title_native+"\r\n");
                    stringBuffer.append("出现在["+filename+"]\r\n");
                    stringBuffer.append("出现时间["+secondToTime(Convert.toLong(data.getString("from")))+"-"+secondToTime(Convert.toLong(data.getString("to")))+"]\r\n");
                    sendMessage(event,"\r\n"+stringBuffer.toString());
                } else {
                    sendMessage(event,"搜索超出限制,请一分钟后再试.");
                }
            }catch (Exception e){
               sendMessage(event,"搜索超出限制,请一分钟后再试.");
            }
        }
    }

    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false, "番剧识别", "功能:\r\n" +
                "通过番剧截图识别番剧信息\r\n" +
                "使用方法\r\n" +
                "1.番剧识别 图片");
    }

}
