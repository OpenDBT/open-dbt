FROM openjdk:8

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装中文字体
RUN    apt-get install -y fontconfig && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
COPY msyh.ttf /usr/share/fonts/ttf-dejavu/msyh.ttf
COPY Fonts/* /usr/share/fonts/ttf-dejavu/
# 设置默认语言环境为C.UTF-8
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

WORKDIR /app
COPY open-dbt.jar /app/open-dbt.jar
CMD ["java", "-jar", "open-dbt.jar", "-Dfile.encoding=utf-8"]
