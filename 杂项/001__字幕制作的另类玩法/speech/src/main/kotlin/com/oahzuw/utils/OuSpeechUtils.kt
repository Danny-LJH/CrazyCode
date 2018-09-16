package com.oahzuw.utils

import com.alibaba.fastjson.JSON
import java.io.File
import java.util.*

/**
 * 调用编译好的语音转文本程序, 控制台提取结果, srt字幕格式如下:
1
00:00:42,811 --> 00:00:44,980
On the house.

2
00:00:45,013 --> 00:00:47,783
Welcome back, Dash! Hiya, Sam.

......
 *
 */
fun doTransform(filename: String) {
    // 创建文件用于保存字幕
    var fileEn = File(filename.replace(".mp4.pcm", "_En.srt"))
    var file = File(filename.replace(".mp4.pcm", ".srt"))
    println("字幕文件为: ${file.absolutePath}")
    // 如果文件不存在创建文件
    if (!fileEn.exists()) fileEn.createNewFile()
    if (!file.exists()) file.createNewFile()
    // 行号记数, 用于生成字幕文件
    var lineNum = 1
    val proc = Runtime.getRuntime().exec("./speech2text $filename")
    Scanner(proc.inputStream).use {
        while (it.hasNextLine()) {
            // 获取控制台输出数据
            var line = it.nextLine()

            when {
                // 识别开始打印日志
                line.equals("开始识别") -> {
                    println("开始识别")
                }
                // 识别结束打印日志
                line.equals("长语音结束") -> {
                    println("长语音结束")
                }
                // 识别出错打印日志
                line.startsWith("识别错误") -> {
                    println("识别错误, 请调式程序! ")
                }
                // json数据每段语音对应的文字, {" 开始为json数据
                line.startsWith("{\"") -> {
                    // srt字幕标号
                    fileEn.appendText("$lineNum\n")
                    file.appendText("$lineNum\n")

                    // 格式化并保存为字幕文件
                    var jsonObject = JSON.parseObject(line)
                    // 当前音频中说话开始时间, 并格式化
                    var startTime = timeFormat(jsonObject.getString("sn_start_time"))
                    // 当前音频中说话结束时间, 并格式化,
                    var endTime = timeFormat(jsonObject.getString("sn_end_time"))

                    // srt字幕时间轴
                    fileEn.appendText("$startTime --> $endTime\n")
                    file.appendText("$startTime --> $endTime\n")

                    // 说话内容, json数组, 可能是多条
                    jsonObject.getJSONArray("results_recognition").iterator().forEach {
                        // srt字幕内容
                        println("识别到文本(英文): $it")
                        fileEn.appendText("$it\n")

                        var result = doTranslate(it.toString())
                        println("识别到文本(中文): $result")
                        file.appendText("$result\n")
                    }
                    // 写一个空行, srt字幕格式
                    fileEn.appendText("\n")
                    file.appendText("\n")
                    lineNum++
                }
            }
        }
    }
}

/**
 * 时间格式为: 00:01:15.189 小时:分钟:秒,毫秒(在秒与毫秒之间,或.都行), 时间不满小时的需要补00:
 * 百度api返回的时间格式在秒与毫秒之间是".", 由于支持不做处理
 */
fun timeFormat(time: String): String {
    var regex = Regex("\\d{2}:\\d{2}:\\d{2}.\\d+")
    when (regex.matches(time)) {
        true -> return time
        // 测试得知百度返回的格式为: 00:28.780, 直接补00:即可, 如出现异常再增加其他情况
        false -> return "00:$time"
    }
}

fun main(args: Array<String>) {
    println(timeFormat("00:28.780"))
}