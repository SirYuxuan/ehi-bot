package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.utils.Command;
import com.yuxuan66.ehi.bot.utils.CommandUtil;
import net.mamoe.mirai.event.Event;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.*;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class EVEPriceListener implements Listener {

    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false,"EVE物价查询","");
    }

    @Override
    public void gotAMessage(Event event,String message) throws CommandException {

        Command command = CommandUtil.parse(message);

        int type = -1;

        if(command.getPrefix().startsWith("jita")){
            type = 1;
        }else if(command.getPrefix().startsWith("gjita")){
            type = 2;
        }
        if(type == -1){
            return;
        }

        String url = "https://www.ceve-market.org/api/searchname";

        String result = HttpUtil.post(url, new HashMap<String,Object>(){{
            put("name",command.getName());
        }});

        JSONArray jsonArray = JSONObject.parseArray(result);

        if(jsonArray.isEmpty()){
            sendMessage(event,"?");
            return;
        }



        StringBuilder sendMessage = new StringBuilder(
                "本查询结果为"+(type == 1 ? "欧服" : "国服")+"吉他价格,"+(type == 1 ? "国服" : "欧服")+"查询使用 "+(type == 1 ? "gjita" : "jita")+" xxxx\r\n" +
                "=================");
        for (int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String typeId = jsonObject.getString("typeid");

            String itemStr = HttpUtil.get("https://www.ceve-market.org/"+ (type == 1 ? "tqapi" : "api") +"/marketstat?typeid="+typeId+"&usesystem=30000142");

            Document document = XmlUtil.parseXml(itemStr);
            Node buyNode = document.getDocumentElement().getFirstChild().getFirstChild().getChildNodes().item(0);
            Node sellNode = document.getDocumentElement().getFirstChild().getFirstChild().getChildNodes().item(1);
            String buyMax = buyNode.getChildNodes().item(2).getTextContent();
            String sellMin = sellNode.getChildNodes().item(3).getTextContent();

            if(jsonObject.getString("typename").contains("涂装") && !command.getName().contains("涂装")){
                continue;
            }

            sendMessage.append("\r\n商品名称:").append(jsonObject.getString("typename")).append(" \r\n求购出价:").append(NumberUtil.decimalFormat(",###", Convert.toLong(buyMax))).append(" ISK").append(" \r\n卖方出价:").append(NumberUtil.decimalFormat(",###", Convert.toLong(sellMin))).append(" ISK").append("\r\n");
            sendMessage.append("=============");

        }
        sendMessage.append("\r\n国服【月上星城】招新\r\n");
        sendMessage.append("=============");
        sendMessage(event,sendMessage.toString());
    }


}
