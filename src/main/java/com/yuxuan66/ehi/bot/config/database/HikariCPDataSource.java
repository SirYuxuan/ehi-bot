package com.yuxuan66.ehi.bot.config.database;

import cn.hutool.core.convert.Convert;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Properties;

/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class HikariCPDataSource {

    public static HikariDataSource getHikariDataSource(){

        Props props = PropsUtil.get("config/database");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(props.getStr("url"));
        dataSource.setUsername(props.getStr("username"));
        dataSource.setPassword(props.getStr("password"));
        dataSource.setDriverClassName(props.getStr("driverClassName"));

        dataSource.setMinimumIdle(props.getInt("minimumIdle"));
        dataSource.setIdleTimeout(props.getInt("idleTimeout"));
        dataSource.setMaximumPoolSize(props.getInt("maximumPoolSize"));
        dataSource.setAutoCommit(props.getBool("autoCommit"));
        dataSource.setPoolName(props.getStr("poolName"));
        dataSource.setMaxLifetime(props.getInt("maxLifetime"));
        dataSource.setConnectionTimeout(props.getInt("connectionTimeout"));
        dataSource.setConnectionTestQuery(props.getStr("connectionTestQuery"));

        return dataSource;
    }



}
