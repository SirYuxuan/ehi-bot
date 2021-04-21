/*
 * Copyright (c) [2020] [Sir丶雨轩]
 * [CoolQQGroupManagerPlugin] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.yuxuan66.ehi.bot.utils;

import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * dnf 相关工具
 *
 * @author Sir丶雨轩
 * @date 2020/6/23
 */
public class DNFUtil {

    static String[] crossRegionalInterface = {"http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2322&s=24986&c=-3&cmp=-1&_t={}"
            , "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2326&s=25010&c=-3&cmp=-1&_t={}"
            , "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2324&s=25054&c=-3&cmp=-1&_t={}",
            "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2342&s=25049&c=-3&cmp=-1&_t={}",
            "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2331&s=25021&c=-3&cmp=-1&_t={}",
            "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2331&s=25018&c=-3&cmp=-1&_t={}",
            "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2330&s=25094&c=-3&cmp=-1&_t={}",
            "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2335&s=25081&c=-3&cmp=-1&_t={}",
            "http://www.uu898.com/ashx/GameRetail.ashx?act=a001&g=95&a=2330&s=25095&c=-3&cmp=-1&_t={}"};

    /**
     * 获取所有跨区金币价格
     *
     * @return 跨区金币价格列表
     */
    public static List<String> getCrossRegionGoldProportion() {

        List<String> priceList = new ArrayList<>();
        for (String url : crossRegionalInterface) {
            String jsonText = HttpUtil.get(StrUtil.format(url, System.currentTimeMillis()));
            JSONObject webJSON = JSONUtil.parseObj(jsonText);
            JSONArray jsonArray = webJSON.getJSONObject("list").getJSONArray("datas");
            BigDecimal money = BigDecimal.ZERO;
            for (Object item : jsonArray) {
                String tmp = JSONUtil.toJsonStr(item);
                money = money.add(JSONUtil.parseObj(tmp).getBigDecimal("Scale"));
            }
            money = money.divide(new BigDecimal(String.valueOf(jsonArray.size()))).setScale(2, BigDecimal.ROUND_HALF_UP);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String crossPrice = decimalFormat.format(money);
            priceList.add(crossPrice);
        }
        return priceList;
    }

    /**
     * 根据背景图创建金币比例展示图
     *
     * @throws IOException IOException
     */
    public static BufferedImage create() throws IOException {

        InputStream inputStream = DNFUtil.class.getResourceAsStream("/assess/dnf_back_group.png");
        Image srcImg = ImageIO.read(inputStream);

        int srcImgWidth = srcImg.getWidth(null);
        int srcImgHeight = srcImg.getHeight(null);

        BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufImg.createGraphics();
        g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
        g.setColor(new Color(133, 208, 227, 255));
        g.setFont(new Font("Adobe 黑体 Std", Font.BOLD, 20));

        // 获取所有跨区价格
        List<String> priceList = getCrossRegionGoldProportion();
        int x = srcImgWidth - (srcImgWidth - 280);
        int y = srcImgHeight - (srcImgHeight - 75);
        g.drawString(priceList.get(0), x, y);
        g.drawString(priceList.get(1), x + 140, y);
        g.drawString(priceList.get(2), x, y + 30);
        g.drawString(priceList.get(3), x + 140, y + 28);
        g.drawString(priceList.get(4), x, y + 57);
        g.drawString(priceList.get(5), x + 140, y + 57);
        g.drawString(priceList.get(6), x, y + 82);
        g.drawString(priceList.get(7), x + 140, y + 84);
        g.drawString(priceList.get(8), x, y + 110);

        g.dispose();

        return bufImg;

    }
}
