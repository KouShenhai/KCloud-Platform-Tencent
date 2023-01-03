* 打开windows cmd命令窗
* cd 本地maven安装地址
* mvn install:install-file -Dfile=jar绝对地址 -DgroupId=自定义groupId -DartifactId=自定义artifactId -Dversion=自定义版本 -Dpackaging=jar
* 执行命令
* 查看运行日志，找到文件默认生成位置，复制到自己的repository仓库
* 项目依赖引入