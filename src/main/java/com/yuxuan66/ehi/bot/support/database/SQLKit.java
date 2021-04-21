package com.yuxuan66.ehi.bot.support.database;

import com.yuxuan66.ehi.bot.config.database.HikariCPDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;


/**
 * @author Sir丶雨轩
 * @date 2021/4/21
 */
public class SQLKit {

    private volatile static SQLManager sqlManager;

    public static SQLManager getSQLManager(){

        synchronized (SQLKit.class){
            if(sqlManager == null){
                SQLManagerBuilder builder = new SQLManagerBuilder(ConnectionSourceHelper.getSingle(HikariCPDataSource.getHikariDataSource()));
                builder.setNc(new UnderlinedNameConversion());
                builder.setInters(new Interceptor[]{new DebugInterceptor()});
                builder.setDbStyle(new MySqlStyle());

                sqlManager = builder.build();
            }
        }

        return sqlManager;
    }


}
