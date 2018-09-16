baidu_speech.zip 是按我的要求修改好的源码, 可以直接使用, 需要修改yours_main.cpp中的api_key为自已的, 使用官方也可以, 地址如下: 

百度语音SDK地址: http://ai.baidu.com/sdk#asr

百度语音api文档地址: http://ai.baidu.com/docs/#/ASR-Linux-SDK/top

编译环境, 由百度长语音识别需要Linux, 这里使用clion 通过wls编译打包,  clion配置: https://confluence.jetbrains.com/display/CLION/WSL+dev+environment+configuration

把代码导入clion直接编译即可, 编译生成可执行文件: asrDemo2, 我这里修改为: speech2text, 运行时依赖: bds_easr_mfe_cmvn.dat、bds_easr_mfe_dnn.dat, 这两个文件在: baidu_speech\resources\asr_resource, 由百度提供, 单独使用时命令如下:

	./speech2text filename.pcm