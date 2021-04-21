package com.yuxuan66.ehi.bot.utils;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
@Data
@ToString
public class Command {

    private String prefix;

    private String name;

    private int len;
}
