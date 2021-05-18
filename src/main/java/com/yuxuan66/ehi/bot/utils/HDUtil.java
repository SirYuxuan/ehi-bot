package com.yuxuan66.ehi.bot.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;


/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class HDUtil {

    private static final String BASE_PATH = "http://115.29.203.165:10002/";

    public static  String info(String qq)  {
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.get(BASE_PATH + "qysUser/info?qq="+qq));
        if(jsonObject.getIntValue("code") == 0){
            return jsonObject.getString("data");
        }else{
            return jsonObject.getString("msg");
        }
    }
    public static String rat(String qq) {
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.get(BASE_PATH+"qysUser/rat?qq="+qq));
        if(jsonObject.getIntValue("code") == 0){
            return jsonObject.getString("data");
        }else{
            return jsonObject.getString("msg");
        }
    }
    public static String pap(String qq)  {
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.get(BASE_PATH+"qysUser/pap?qq="+qq));
        if(jsonObject.getIntValue("code") == 0){
            return jsonObject.getString("data");
        }else{
            return jsonObject.getString("msg");
        }
    }
}
