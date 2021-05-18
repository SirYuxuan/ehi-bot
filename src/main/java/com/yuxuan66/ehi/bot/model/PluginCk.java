package com.yuxuan66.ehi.bot.model;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

/**
 * @author Sir丶雨轩
 * @date 2021/4/22
 */
@Data
@Table(name="plugin_ck")
public class PluginCk {

    private Integer id;
    private String group;
    private String key;
    private String value;
    private String createQQ;
}
