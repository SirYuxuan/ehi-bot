package com.yuxuan66.ehi.bot.listener.impl;

import com.yuxuan66.ehi.bot.exception.CommandException;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.utils.DNFUtil;
import lombok.SneakyThrows;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class DNFListener implements Listener {
    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false,"DNF小助手","");
    }

    @Override
    public void gotAMessage(Event event, String message) throws CommandException {
        if("金币比例".equalsIgnoreCase(message)){
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = DNFUtil.create();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Group group = ((GroupMessageEvent)event).getGroup();
            assert bufferedImage != null;
            String imgId = group.uploadImage(bufferedImage).getImageId();
            sendMessage(event, MessageUtils.newImage(imgId).plus(""),false);
        }
    }
}
