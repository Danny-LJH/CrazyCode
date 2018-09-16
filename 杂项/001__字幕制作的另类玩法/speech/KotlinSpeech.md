## 调用百度长语音识别程序并生成字幕源码

运行在Bash On Windows 环境, bash 环境参考: https://docs.microsoft.com/zh-cn/windows/wsl/install-win10

另外还需要在bash里面配置java环境, 下载地址: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html, 选择jdk-8u181-linux-x64.tar.gz, 环境详细配置如下:
	
	cd /opt
	mkdir dev & cd dev
	# 下载好的jdk-8u181-linux-x64.tar.gz文件移动到/opt/dev目录
	tar zxvf jdk-8u181-linux-x64.tar.gz
	# 会自动解压得到jdk1.8.0_181目录
	cd jdk1.8.0_181
	pwd	# 得到绝对路径
	# 配置JAVA_HOME
	vi ~/bashrc
	# 在文件结尾加入如下命令
	export JAVA_HOME=/opt/dev/jdk1.8.0_181
	export PATH=$JAVA_HOME/bin:$PATH
	# 保存退出
	# 使用环境变量升效
	source ~/bashrc
	# 测试是否成功
	java -version
	# 输出版本号即成功

编译好的jar文件需要与打包的百度语音程序放在同一目录, 这里已准备好了一份, [点击跳转](../speech_bin), 可直接使用. 