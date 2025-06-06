# 基础镜像
FROM  openjdk:8-jre
# author
MAINTAINER roydon

# 挂载目录
VOLUME /home/niuyin
# 创建目录
RUN mkdir -p /home/niuyin
# 指定路径
WORKDIR /home/niuyin
# 复制jar文件到路径
COPY ./jar/niuyin-service-video.jar /home/niuyin/niuyin-service-video.jar
# 启动网关服务
ENTRYPOINT ["java","-jar","niuyin-service-video.jar"]
