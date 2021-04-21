/*
 * Copyright (c) [2020] [Sir丶雨轩]
 * [ehi-fast-admin] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.yuxuan66.ehi.bot.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.ehi.bot.model.WeatherAddr;
import com.yuxuan66.ehi.bot.support.database.SQLKit;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;

/**
 * 天气工具类
 *
 * @author Sir丶雨轩
 * @date 2020/6/28
 */
public class WeatherUtil {
    public static boolean isChineseByScript(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        if (sc == Character.UnicodeScript.HAN) {
            return true;
        }
        return false;
    }

    public static boolean isAllChinese(String str) {
        for (char c : str.toCharArray()) {
            boolean isChinese = isChineseByScript(c);
            if (!isChinese) {
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    public static String getImg() {
        return Jsoup.connect("http://www.nmc.cn/publish/observations/hourly-temperature.html").get().getElementById("imgpath").attr("src");
    }

    @SneakyThrows
    public static String get(String city) {
        if (!isAllChinese(city)) {
            return "地址输入错误";
        }
        WeatherAddr weatherAddr = SQLKit.getSQLManager().lambdaQuery(WeatherAddr.class).andLike("city","%"+city+"%").unique("code");
        String code = weatherAddr.getCode();

        String result = HttpUtil.get("http://www.nmc.cn/rest/weather?stationid=" + code + "&_=" + System.currentTimeMillis());

        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.get("data").getClass().equals(String.class)) {
            return "对不起,没有查询到此地址的天气";
        }
        JSONObject details = jsonObject.getJSONObject("data");
        StringBuffer resultStr = new StringBuffer();
        String max = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(0).getJSONObject("day").getJSONObject("weather").getString("temperature");
        String min = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(0).getJSONObject("night").getJSONObject("weather").getString("temperature");
        String info = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(0).getJSONObject("day").getJSONObject("weather").getString("info");
        String windDirect = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(0).getJSONObject("day").getJSONObject("wind").getString("direct");
        String windPower = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(0).getJSONObject("day").getJSONObject("wind").getString("power");
        String now = details.getJSONObject("real").getJSONObject("weather").getString("temperature");

        String max1 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(1).getJSONObject("day").getJSONObject("weather").getString("temperature");
        String min1 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(1).getJSONObject("night").getJSONObject("weather").getString("temperature");
        String info1 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(1).getJSONObject("day").getJSONObject("weather").getString("info");
        String windDirect1 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(1).getJSONObject("day").getJSONObject("wind").getString("direct");
        String windPower1 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(1).getJSONObject("day").getJSONObject("wind").getString("power");

        String max2 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(2).getJSONObject("day").getJSONObject("weather").getString("temperature");
        String min2 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(2).getJSONObject("night").getJSONObject("weather").getString("temperature");
        String info2 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(2).getJSONObject("day").getJSONObject("weather").getString("info");
        String windDirect2 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(2).getJSONObject("day").getJSONObject("wind").getString("direct");
        String windPower2 = details.getJSONObject("predict").getJSONArray("detail").getJSONObject(2).getJSONObject("day").getJSONObject("wind").getString("power");


        resultStr.append(StrUtil.format(city + "今天{},气温:{}°~{}°,风力:{},当前温度:{}°\r\n", info, min, max, windDirect + " " + windPower, now));
        resultStr.append(StrUtil.format("明天{},气温:{}°~{}°,风力:{}\r\n", info1, min1, max1, windDirect1 + " " + windPower1));
        resultStr.append(StrUtil.format("后天{},气温:{}°~{}°,风力:{}\r\n", info2, min2, max2, windDirect2 + " " + windPower2));
        return resultStr.toString();
    }

    public static void main(String[] args) {
        System.out.println(get("济南"));
    }
}
