package com.yuxuan66.ehi.bot.model;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
@Data
@Table(name="weather_addr")
public class WeatherAddr {

    private Integer id;
    private String city;
    private String code;
}
