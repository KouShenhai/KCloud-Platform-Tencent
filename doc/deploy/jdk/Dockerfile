# 基础镜像，必须第一个写
FROM centos:7
# 作者
LABEL maintainer="koushenhai"
# 介绍
LABEL description="jdk 17"
# 更新软件源
RUN apt-get  update
# 安装支持的字体
RUN  apt-get  install  fontconfig -y
# 清理缓存
RUN apt-get clean
# Windows环境需要将文件拷贝到本地(/usr/share/zoneinfo/Asia/Shanghai)
COPY /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
# setup language 解决中文乱码
# 设置中文支持
ENV  LANG C.UTF-8
# 在当前目录的文件，拷贝过去会自动解压到指定的目录
COPY jdk-17_linux-x64_bin.tar.gz /usr/local
# 运行命令
RUN cd /usr/local && mv jdk-17.0.4.1 /usr/local/jdk
# 设置环境变量
ENV JAVA_HOME /usr/local/jdk
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $PATH:$JAVA_HOME/bin
