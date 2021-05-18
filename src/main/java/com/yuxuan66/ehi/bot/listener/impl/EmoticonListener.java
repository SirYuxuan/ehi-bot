package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.utils.Command;
import com.yuxuan66.ehi.bot.utils.CommandUtil;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageUtils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class EmoticonListener implements Listener {
    @Override
    public HandlerInfo register() {
        List<String> helperText = new ArrayList<String>() {{
            add("表情包生成:");
            add("1.表情包 金馆长的采访 xx");
            add("2.表情包 熊猫记仇 xx");
            add("3.表情包 熊猫悄悄话 xx");
            add("4.表情包 为所欲为 xx xx xx xx xx xx xx xx xx");
            add("5.表情包 留不住 xx xx xx");
            add("6.表情包 有内鬼 xx");
            add("7.表情包 土拨鼠 xx xx");
            add("8.表情包 诸葛孔明 xx xx");
            add("9.表情包 马冬梅 xx xx xx xx xx xx xx xx xx");
            add("10.表情包 我们是谁 xx xx xx xx xx xx");
            add("11.表情包 压力大爷 xx xx xx");
            add("12.表情包 万恶之源 xx");
            add("13.表情包 王境泽 xx xx xx xx");
            add("14.表情包 乌鸦哥 xx xx xx");
            add("15.表情包 奖状 xx xx xx");
            add("16.表情包 熊猫拒绝 xx");
            add("17.表情包 看书 xx");
            add("18.表情包 王宝强 xx xx xx");
            add("19.表情包 我不听 xx");
            add("20.表情包 妈妈再打我一次 xx xx xx");
            add("21.表情包 想不到吧 xx");
        }};
        return new HandlerInfo(false, "表情包", "功能:\r\n生成斗图表情包\r\n使用方法:\r\n" + ArrayUtil.join(helperText.toArray(), "\r\n"));
    }

    private final Map<String, String> typeMapping = new HashMap<String, String>() {{
        put("金馆长的采访", "122");
        put("熊猫记仇", "100");
        put("熊猫悄悄话", "60");
        put("为所欲为", "93");
        put("留不住", "114");
        put("有内鬼", "115");
        put("土拨鼠", "95");
        put("诸葛孔明", "99");
        put("马冬梅", "102");
        put("我们是谁", "86");
        put("压力大爷", "101");
        put("万恶之源", "65");
        put("王境泽", "94");
        put("乌鸦哥", "105");
        put("奖状", "11");
        put("熊猫拒绝", "89");
        put("看书", "117");
        put("王宝强", "12");
        put("我不听", "76");
        put("妈妈再打我一次", "59");
        put("想不到吧", "66");
    }};

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {
        if(getQQ(event).equals(2275840770L)){
            return;
        }
        if (message.startsWith("表情包")) {
            String[] arr = message.split(" ");
            Map<String, Object> param = new HashMap<>();
            param.put("types", "maker");
            if ("表情包".equals(arr[0]) && arr.length > 2) {
                if (typeMapping.containsKey(arr[1])) {
                    param.put("id", typeMapping.get(arr[1]));
                    for (int i = 0; i < arr.length - 2; i++) {
                        param.put("str" + (i + 1), arr[i + 2]);
                    }
                    getImgMsg(param, event);
                }
            }

        }

    }

    private void getImgMsg(Map<String, Object> param, Event event)  {
        String API_PATH = "https://www.52doutu.cn/api/";
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.post(API_PATH, param));
        if (jsonObject.getIntValue("code") == 200) {
            String imgPath = jsonObject.getString("url");
            if (!imgPath.startsWith("http")) {
                imgPath = "http:" + imgPath;
            }
           String imgId =  getGroup(event).uploadImage(URLUtil.toUrlForHttp(imgPath)).getImageId();
            sendMessage(event, MessageUtils.newImage(imgId).plus(""),false);
        }
    }
}
