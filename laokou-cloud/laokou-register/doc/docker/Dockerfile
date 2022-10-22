# 基础镜像
FROM docker.io/sunrdocker/jdk17-jre-font-openssl-alpine:latest
# 信息
LABEL maintainer="koushenhai"
LABEL description="laokou-register"
# 开放端口
EXPOSE 1111
# 挂载
VOLUME /data
# 复制文件
COPY laokou-register.jar app.jar
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
# 执行命令
ENTRYPOINT java -Xms256m -Xmx512m -jar app.jar --spring.profiles.active=prod > app.log