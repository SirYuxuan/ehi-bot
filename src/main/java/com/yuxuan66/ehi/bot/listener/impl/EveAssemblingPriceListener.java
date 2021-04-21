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

import java.sql.SQLException;
import java.util.*;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class EveAssemblingPriceListener implements Listener {
    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false,"Eve装配价格查询","功能:\r\n能对一整个装配进行估价\r\n使用方法:\r\n游戏中装配方案复制到剪贴板,然后私聊发送至机器人,消息最前方要加上国服或者欧服\r\n例子:\r\n国服[多米尼克斯级, Dominix-刷怪20200503]\r\n欧服[多米尼克斯级, Dominix-刷怪20200503]");
    }

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {
        if(message.startsWith("[")){
            String[] arr = message.split("\r");
            if(arr[0].contains(",") && arr[0].endsWith("]")){
                if(!message.startsWith("国服") || ! message.startsWith("欧服")){
                    message = "欧服"+message;
                    sendMessage(event,"由于您没有指定服务器,本次查询默认使用欧服,如需指定服务器,请在消息开头加上,欧服或国服");
                }
            }
        }
        if(message.startsWith("国服") || message.startsWith("欧服")){
            int priceType = message.startsWith("国服")?0:1;
            message = message.substring(2);
            if(message.startsWith("[")){
                String[] arr = message.split("\r");
                if(arr[0].contains(",") && arr[0].endsWith("]")){
                   sendMessage(event,"请稍等,正在为您查询");
                    String warship = arr[0].split(",")[0].replace("[","");
                    String assemblyPlan = arr[0].split(",")[1].replace("]","");

                    // 循环查询物品价格
                    StringBuffer result = new StringBuffer();
                    long price = 0;
                    long priceWar = price(priceType,warship);
                    result.append(warship).append(", 单价:").append(priceWar == -1 ? "无货" : NumberUtil.decimalFormat(",###",priceWar)).append("\r\n");
                    if(priceWar != -1){
                        price += priceWar;
                    }
                    for (int i = 1; i < arr.length; i++) {
                        if(StrUtil.isNotBlank(arr[i])){
                            // 判断是否有数量
                            int num = 1;
                            String name = arr[i];
                            if(arr[i].contains("x")){
                                String[] tmp = arr[i].split("x");
                                if(Convert.toInt(tmp[tmp.length-1],-1) > 0){
                                    num = Convert.toInt(tmp[tmp.length-1],-1);
                                }
                                name = "";
                                for (int j = 0; j < tmp.length; j++) {
                                    if(Convert.toInt(tmp[tmp.length-1],-1) > 0 && j == tmp.length-1){
                                        break;
                                    }
                                    name += tmp[j];
                                }

                            }
                            long priceTmp = price(priceType,name);
                            if(priceTmp != -1){
                                price += priceTmp*num;
                            }
                            result.append(name).append(" x").append(num).append(", 单价:").append(priceTmp == -1 ? "无货" : NumberUtil.decimalFormat(",###",priceTmp)).append(" ,总价:").append(priceTmp == -1 ? "无货" :NumberUtil.decimalFormat(",###",(priceTmp * num))).append("\r\n");
                        }
                    }
                    sendMessage(event,"您查询的舰船为:" + warship+",装配方案:" + assemblyPlan);
                    result.append("商品预估总价:").append(NumberUtil.decimalFormat(",###", price)).append(" ISK");
                    // 每10行拆分一次
                    List<String> sendMessage = new ArrayList<>();
                    String[] a1 = result.toString().split("\r\n");
                    String tmp = "";
                    for (int i = 0; i < a1.length; i++) {
                        tmp += a1[i] + "\r\n";
                        if((i != 0 && i % 10 == 0) || i == a1.length - 1){
                            sendMessage.add(tmp);
                            tmp = "";
                        }
                    }
                    sendMessage.forEach(item->sendMessage(event,item));



                }
            }
        }
    }

    private long price(int priceType,String name){
        name = name.trim();
        if(priceType == 1){
            String url = "https://evemarketer.com/api/v1/types/search?q=" + name + "&language=zh&important_names=false";
            JSONArray jsonObject = JSONArray.parseArray(HttpUtil.get(url));

            if (jsonObject.isEmpty()) {
                return -1;
            }
            String id = "";
            if (jsonObject.size() > 1) {
                for (Object o : jsonObject) {
                    JSONObject tmp = JSONObject.parseObject(JSONObject.toJSONString(o));
                    if (name.equals(tmp.getString("name"))) {
                        id = tmp.getString("id");
                        break;
                    }
                }
                if (StrUtil.isBlank(id)) {
                    return -1;
                }

            }
            if (StrUtil.isBlank(id)) {
                JSONObject jsonObject1 = jsonObject.getJSONObject(0);
                id = jsonObject1.getString("id");
            }
            // 商品id

            url = "https://evemarketer.com/api/v1/markets/types/" + id + "?region_id=10000002&language=zh&important_names=false";

            JSONObject result = JSONObject.parseObject(HttpUtil.get(url));

            JSONArray sell = result.getJSONArray("sell");

            if (sell.isEmpty() ) {
                return -1;
            }
            List<Long> priceList = new ArrayList<>();

            for (int i = 0; i < sell.size(); i++) {
                JSONObject goods = sell.getJSONObject(i);
                priceList.add(goods.getLongValue("price"));
            }
            Collections.sort(priceList);
            return priceList.get(0);
        }else{
            String url = "https://www.ceve-market.org/api/searchname";
            Map<String, Object> param = new HashMap<>();
            param.put("name", name);
            String result = HttpUtil.post(url, param);
            JSONArray jsonArray = JSONObject.parseArray(result);
            if(jsonArray.isEmpty()){
                return -1;
            }
            String typeId = jsonArray.getJSONObject(0).getString("typeid");
            String itemStr = HttpUtil.get("https://www.ceve-market.org/api/market/region/0/type/" + typeId + ".json");
            JSONObject item = JSONObject.parseObject(itemStr);
            return item.getJSONObject("sell").getLongValue("min");
        }
    }
}
