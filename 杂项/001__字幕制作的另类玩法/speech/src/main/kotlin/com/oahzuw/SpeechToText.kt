package com.oahzuw

import com.oahzuw.utils.doTransform
import java.io.File

fun main(args: Array<String>) {

    // 处理用户输入的目录, 增加程序灵活性
    var treeWalk = File(args[0]).walk()

    // 遍历获取批定目录下所有pcm音频文件
    treeWalk.iterator().forEach {
        // 只处理pcm文件, 其他忽略
        if (it.isFile && it.absolutePath.endsWith(".pcm"))
            // 调用音频转文字方法, 文件路径为绝对路径
            doTransform(it.absolutePath)
    }

}

