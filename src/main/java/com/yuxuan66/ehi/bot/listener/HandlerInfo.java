
package com.yuxuan66.ehi.bot.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sir丶雨轩
 * @date 2020/6/24
 */
@Data
@NoArgsConstructor
public class HandlerInfo {

    // 是否是系统级
    private boolean isSys;

    // 功能名称
    private String name;

    // 帮助内容
    private String help;


    public HandlerInfo(boolean isSys, String name, String help) {
        this.isSys = isSys;
        this.name = name;
        this.help = help;
    }
}
