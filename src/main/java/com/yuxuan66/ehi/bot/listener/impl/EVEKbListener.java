package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import net.mamoe.mirai.event.Event;

/**
 * @author Sir丶雨轩
 * @date 2021/4/22
 */
public class EVEKbListener implements Listener {
    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false,"EVE KB查询","");
    }

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {
        if(message.startsWith(".kb")){

            String[] strings = message.split(" ");
            if(strings.length < 2){
               sendMessage( event,"对不起 命令格式不正确,请输入角色名");
                return;
            }
            String name = message.replace(".kb ","");

            try{
                String url = "https://zkillboard.com/autocomplete/"+name+"/";
                String result = HttpUtil.get(url);
                JSONArray jsonArray = JSONObject.parseArray(result);
                String cid = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.getString("name").equalsIgnoreCase(name.toString())){
                        cid = jsonObject.getString("id");
                        break;
                    }
                }
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append(name).append(" 击杀报告:\r\n");
                if(StrUtil.isNotBlank(cid)){
                    JSONObject jsonObject = JSONObject.parseObject(HttpUtil.get("https://zkillboard.com/api/stats/characterID/"+cid+"/"));
                    // 击杀船只
                    stringBuffer.append("击杀船只数: ").append(jsonObject.getString("allTimeSum")).append("\r\n");
                    stringBuffer.append("击毁点数: ").append(jsonObject.getString("pointsDestroyed")).append("\r\n");
                    stringBuffer.append("击毁ISK价值: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(jsonObject.getString("iskDestroyed")))).append("\r\n");
                    stringBuffer.append("损失船只数: ").append(jsonObject.getString("shipsLost")).append("\r\n");
                    stringBuffer.append("损失点数: ").append(jsonObject.getString("pointsLost")).append("\r\n");
                    stringBuffer.append("损失ISK价值: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(jsonObject.getString("iskLost")))).append("\r\n");
                    stringBuffer.append("威胁度: ").append(jsonObject.getString("dangerRatio")).append("%\r\n");
                    sendMessage(event,stringBuffer.toString());
                }else{
                    sendMessage(event, "对不起 没有找到您的击杀报告");
                }
            }catch (Exception e){
                sendMessage(event, "对不起 没有找到您的击杀报告");
            }

        }
    }
}
