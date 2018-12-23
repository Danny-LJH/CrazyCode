package com.oahzuw.baiduai.image;

import com.baidu.aip.imagesearch.AipImageSearch;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * @author: 武钊 <oahzuw@live.com>
 * @date: 2018/12/22 19:18
 * @description:
 */
public class SearchImage {
    /**
     * 你申请的APPID
     */
    public static final String APP_ID = "14393394";
    /**
     * 你申请的API Key
     */
    public static final String API_KEY = "jxpacHlwM1IQtwh94KZ5VMaT";
    /**
     * 你申请的Secret Key
     */
    public static final String SECRET_KEY = "Zd3pHhGt0lDD40xFSGlT9GA5FIVKTvjt";

    private static AipImageSearch client;


    public static void main(String[] args) {
        // 初始化一个AipImageSearch
        client = new AipImageSearch(APP_ID, API_KEY, SECRET_KEY);

        // 训练图片入库
//        insert(client);

        // 测试相似图识别率
        query(client);

    }

    /**
     * 图片入库
     */
    public static void insert(AipImageSearch client) {
        // 传入可选参数调用接口, 参数越详细, 搜索时结果越精准
        HashMap<String, String> options = new HashMap<String, String>();

        // 文件标签, 这个先不填, 实际项目时, 根据实际需求来
        //  options.put("tags", "100,11");

        //  images 目录下的base目录训练图片, test是测试图片, 这里把训练图片全部入库
        File images = new File("images/base");
        // 获取目录下的所有图片
        File[] files = images.listFiles();
        for (File f : files) {
            // 数据格式为json, 可以定义多种属性, 这里以name为例, 所有图片都是刘德华, 加上文件名方便识别
            options.put("brief", "{\"name\":\"刘德华\", \"fileName\":\"" + f.getName() + "\"}");
            // 上传图片入库
            JSONObject res = client.similarAdd(f.getAbsolutePath(), options);
            // 打印上传结果
            System.out.println(res.toString(2));
        }
    }


    /**
     * 相似图片搜索
     */
    public static void query(AipImageSearch client) {
        /**
         * 传入可选参数调用接口, 根据需求来, 参数越详细结果越准确, 这里图片较少, 只写页码和条数
         * 经测试指定页码和条数后, 会根据相似度排序, 最高的排在前面
         */
        HashMap<String, String> options = new HashMap<String, String>();
        // 页码, 这里从第一页开始, 也就是展示相似度最高的两张图片
        options.put("pn", "0");
        // 条数, 只显示两条
        options.put("rn", "2");

        //  images 目录下的base目录训练图片, test是测试图片, 这里把测试图片全部测试
//        File images = new File("images/test"); // 刘德华本人
        File images = new File("images/other"); // 使用周杰伦的测试看下结果如何!
        // 获取目录下的所有图片
        File[] files = images.listFiles();
        for (File f : files) {
            // 输出文件名
            System.out.println(f.getName());
            // 进行相似查询,
            JSONObject res = client.similarSearch(f.getAbsolutePath(), options);
            // 打印上传结果
            System.out.println(res.toString(2));
        }
    }
}
