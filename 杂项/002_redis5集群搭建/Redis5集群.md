 [百家号阅读地址](https://mbd.baidu.com/newspage/data/landingshare?context=%7B%22nid%22%3A%22news_8772075753710568267%22%2C%22sourceFrom%22%3A%22bjh%22%2C%22url_data%22%3A%22bjhauthor%22%7D)

**这里使用redis 5搭建集群, 新特性使搭建集群特别简单, 详情看如下步骤**

## 1, 下载编译

这里环境是ubuntu18.04,  系统需要安装: make,  gcc

```shell
# redis官方地址: redis.io, 这里选择最新稳定版5.0.3
# 进opt目录
cd /opt
# 下载
wget http://download.redis.io/releases/redis-5.0.3.tar.gz
# 解压
tar zxvf redis-5.0.3.tar.gz
# 编译
cd redis-5.0.3 && make
# 安装到指定目录
make PREFIX=/opt/redis install
# 复制配置文件到安装目录
cp redis.conf  /opt/redis/bin 
# 接下来就是配置了
```

## 2, 配置

redis5支持使用redis-cli配置集群无需使用ruby脚本, 接下来请看详细配置:

```shell
# 确认在redis的bin目录下, 这里承接上文, 编辑配置文件
# 首先进入/opt/redis/bin目录
cd /opt/redis/bin
# 编辑配置文件
vim redis.conf

# 找到以下参数进行修改, 参数说明如下:
# 修改端口号
port 7000
# 开启集群模式
cluster-enabled yes
# 集群配置文件, 这个文件无需要创建, 集群启动后自动生成
cluster-config-file nodes.conf
# 集群超时间, 超出这个时间认为宕机
cluster-node-timeout 5000
# 开启AOF模式
appendonly yes
# 以守护进程方式启动
daemonize yes

```

配置完成后复制6份,  **6份的原因是: 3主3备集群**, 目录名与端口号对应, 其他不修改, 命令如下: 

```shell
# 进入redis上层目录, 这里是opt
cd /opt && ls 
# 终端打印如下
redis  redis-5.0.3  redis-5.0.3.tar.gz

# 创建 cluster 目录
mkdir cluster

# 复制6份到 cluster 目录
cp -r redis/bin cluster/7000
cp -r redis/bin cluster/7001
cp -r redis/bin cluster/7002
cp -r redis/bin cluster/7003
cp -r redis/bin cluster/7004
cp -r redis/bin cluster/7005

# !一定确认配置文件里面的端口号与目录名一致是: 7000 - 7005

```

## 3, 启动集群

### a,  启动所有redis

```shell
# 正式配置为集群前需要先启动每个redis, 创建一个启动脚本:
# 这里承接上文, 脚本启动脚本所有目录为:/opt/cluster, 可以使用pwd查看所有目录是否正确

# 进入cluster目录
cd /opt/cluster
# 确认目录下的redis
ls
# 终端打印
7000  7001  7002  7003  7004  7005

# 创建脚本文件
touch start_cluster.sh

# 内容如下:
cd 7000
./redis-server ./redis.conf
cd ../7001
./redis-server ./redis.conf
cd ../7002
./redis-server ./redis.conf
cd ../7003
./redis-server ./redis.conf
cd ../7004
./redis-server ./redis.conf
cd ../7005
./redis-server ./redis.conf

# 使用脚本启动6个redis
./start_cluster.sh
# 查看是否启动成功
ps aux | grep redis
# 终端打印信息如下 ( 这是截取的部分信息, 终端原因最后一位端口号没出来 ):
# 出现6个redis-server的进程就说明启动成功了
./redis-server 127.0.0.1:700
./redis-server 127.0.0.1:700
./redis-server 127.0.0.1:700
./redis-server 127.0.0.1:700
./redis-server 127.0.0.1:700
./redis-server 127.0.0.1:700
```

### b, 创建集群

```shell
# 创建集群这里使用redis-cli, 命令如下:
# 需要进入任意redis目录, 这里以7000为例
cd 7000 #当前目录为: /opt/cluster/7000
# 执行命令连接集群, 此命令只需执行一次, 以后启动集群只需要执行start_cluster.sh即可
./redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 \
127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 \
--cluster-replicas 1

# 输出确认信息: 节点信息、主从信息、槽信息, 详情如下:
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
Adding replica 127.0.0.1:7003 to 127.0.0.1:7000
Adding replica 127.0.0.1:7004 to 127.0.0.1:7001
Adding replica 127.0.0.1:7005 to 127.0.0.1:7002
>>> Trying to optimize slaves allocation for anti-affinity
[WARNING] Some slaves are in the same host as their master
M: 3c5a402a0a891e6ed621480e5178e171ca8e762d 127.0.0.1:7000
   slots:[0-5460] (5461 slots) master
M: 319a8d36d0624e1bb97f37bbf1460fd69ca35581 127.0.0.1:7001
   slots:[5461-10922] (5462 slots) master
M: 80bd8e0f96ecab9570d2f20ad3e56fe21ce30f73 127.0.0.1:7002
   slots:[10923-16383] (5461 slots) master
S: 95a37be5cec02135d7774ebe3ad9da0e4fe6e135 127.0.0.1:7003
   replicates 319a8d36d0624e1bb97f37bbf1460fd69ca35581
S: 1b8ae39eec62776850273162d1de89cfb3cbe2cb 127.0.0.1:7004
   replicates 80bd8e0f96ecab9570d2f20ad3e56fe21ce30f73
S: f6b2ec04f8776d66dee2b4ea9873fbc7119a5c73 127.0.0.1:7005
   replicates 3c5a402a0a891e6ed621480e5178e171ca8e762d
Can I set the above configuration? (type 'yes' to accept):
# 如果信息无误, 输入yes回车即可

# 如果集群创建成功输出如下信息
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.

# redis-cli查看集群是否创建成功, 使用redis-cli连接任意节点, 这里以端口为7000的节点为例:
# 连接节点
./redis-cli -c -p 7000
# 查看集群信息
cluster nodes
# 终端打印如下:
1b8ae39eec62776850273162d1de89cfb3cbe2cb 127.0.0.1:7004@17004 slave 80bd8e0f96ecab9570d2f20ad3e56fe21ce30f73 0 1546525428416 5 connected
f6b2ec04f8776d66dee2b4ea9873fbc7119a5c73 127.0.0.1:7005@17005 slave 3c5a402a0a891e6ed621480e5178e171ca8e762d 0 1546525429000 6 connected
80bd8e0f96ecab9570d2f20ad3e56fe21ce30f73 127.0.0.1:7002@17002 master - 0 1546525429424 3 connected 10923-16383
95a37be5cec02135d7774ebe3ad9da0e4fe6e135 127.0.0.1:7003@17003 slave 319a8d36d0624e1bb97f37bbf1460fd69ca35581 0 1546525429000 4 connected
319a8d36d0624e1bb97f37bbf1460fd69ca35581 127.0.0.1:7001@17001 master - 0 1546525430432 2 connected 5461-10922
3c5a402a0a891e6ed621480e5178e171ca8e762d 127.0.0.1:7000@17000 myself,master - 0 1546525429000 1 connected 0-5460
```

至此redis集群搭建完成, 接下来测试!



## 4, 测试

redis-cli 连接集群任意节点, 测试数据存取, 这里以端口为7000的节点为例:

```shell
# 连接节点
./redis-cli -c -p 7000

# 测试信息如下:
127.0.0.1:7000> set a b
-> Redirected to slot [15495] located at 127.0.0.1:7002
OK
127.0.0.1:7002> set b c
-> Redirected to slot [3300] located at 127.0.0.1:7000
OK
127.0.0.1:7000> set c d
-> Redirected to slot [7365] located at 127.0.0.1:7001
OK
127.0.0.1:7001> set d e
-> Redirected to slot [11298] located at 127.0.0.1:7002
OK

# 根据以上结果发现设置的值会自动计算槽信息并存储到指定节点上
```



以上就是使用redis5的redis-cli搭建集群的全过程, 比使用ruby简单了很多, 自已动手操作,  还是很有成就感的!



