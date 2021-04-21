/*
 * Copyright (c) [2020] [Sir丶雨轩]
 * [ehi-fast-admin] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.utils.WeatherUtil;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.data.MessageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Sir丶雨轩
 * @date 2020/6/28
 */

public class WeatherListener implements Listener {

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {

        if (message.startsWith("#天气")) {
            String[] arr = message.split(" ");
            if (arr.length == 2 && "全国".equals(arr[1])) {
                String imgPath = WeatherUtil.getImg();
                String imgId =  getGroup(event).uploadImage(URLUtil.toUrlForHttp(imgPath)).getImageId();
                sendMessage(event, MessageUtils.newImage(imgId).plus(""),false);

            } else if (arr.length == 2) {
                sendMessage(event,WeatherUtil.get(arr[1]));
            }
        }
    }

    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false, "天气查询", "功能:\r\n查询全国的天气\r\n使用方法:\r\n#天气 城市名\r\n#天气 全国");
    }


}

