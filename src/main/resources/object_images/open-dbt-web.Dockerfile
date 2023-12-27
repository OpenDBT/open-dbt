# 使用官方 PostgreSQL 镜像作为基础镜像
FROM nginx:latest
# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 复制 nginx.conf 文件到容器中
COPY nginx.conf /etc/nginx/nginx.conf
# 复制 dist 文件夹到容器中
COPY dist/ /usr/share/nginx/html

