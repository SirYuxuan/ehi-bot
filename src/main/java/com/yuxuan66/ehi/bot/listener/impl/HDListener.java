package com.yuxuan66.ehi.bot.listener.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.yuxuan66.ehi.bot.listener.HandlerInfo;
import com.yuxuan66.ehi.bot.listener.Listener;
import com.yuxuan66.ehi.bot.support.database.SQLKit;
import com.yuxuan66.ehi.bot.utils.HDUtil;
import net.mamoe.mirai.event.Event;
import org.beetl.sql.core.SqlId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class HDListener implements Listener {
    @Override
    public HandlerInfo register() {
        return new HandlerInfo(false,"混沌军团个人资料查询","");
    }

    @Override
    public void gotAMessage(Event event, String message) {
        if("info".equalsIgnoreCase(message)){
            sendMessage(event, HDUtil.info(getQQ(event).toString()));
        }else if("pap".equalsIgnoreCase(message)){
            sendMessage(event,HDUtil.pap(getQQ(event).toString()));
        }else if("rat".equalsIgnoreCase(message)){
            sendMessage(event,HDUtil.rat(getQQ(event).toString()));
        }else if("lp".equalsIgnoreCase(message)){
            // 执行SQL获取LP数量
            Map<String,Object> lpInfo = SQLKit.getSQLManager().selectSingle( SqlId.of("lp","selectLPByQQ"),new HashMap<String,Object>(){{
                put("qq",getQQ(event));
            }}, Map.class);

           if(lpInfo.isEmpty()){
            sendMessage(event,"对不起,您还没有注册系统或没设置QQ号\r\nhttp://www.hd-eve.com");
               return;
           }

            String name = lpInfo.get("nickname").toString();
            if(StrUtil.isBlank(name)){
                name = "未设置";
            }

            String sendMsg = name + " 您的LP统计如下:\r\n" +
                    "共获得:" + lpInfo.get("dkp") + "\r\n" +
                    "已使用:" + lpInfo.get("useDkp") + "\r\n" +
                    "现剩余:" + lpInfo.get("nowDkp") ;
            sendMessage(event, sendMsg);
        }
    }
}
