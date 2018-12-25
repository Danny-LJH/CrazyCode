package com.oahzuw.utils;

import com.baidu.aip.ocr.AipOcr;

/**
 * @author: 武钊 <oahzuw@live.com>
 * @date: 2018/12/25 17:48
 * @description:
 */
public class BaiDuAiUtils {
    /**
     * 你的 App ID
     */
    public static final String APP_ID = "9283795";
    /**
     * 你的 Api Key
     */
    public static final String API_KEY = "OZBslcteBK1plKGQQMB4NWj1";
    /**
     * 你的 Secret Key
     */
    public static final String SECRET_KEY = "6CLO68sSD6O90A6hkSeZs9w259LPONKC";
    private static AipOcr client;

    static {
        // 初始化一个AipOcr
        client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }

    public static AipOcr getClient() {
        return client;
    }
}
