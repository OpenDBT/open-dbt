# 使用官方 PostgreSQL 镜像作为基础镜像
FROM postgres:latest
# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置环境变量
ENV POSTGRES_DB=postgres
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=123QWEasd@

# 复制初始化 SQL 文件到容器中
#COPY ./init.sql /docker-entrypoint-initdb.d/init.sql
