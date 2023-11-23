FROM centos:7.9.2009
LABEL maintainer="highgo"

## 安装依赖包
RUN yum install -y --nogpgcheck cyrus-sasl perl-ExtUtils-Embed readline-devel zlib-devel pam-devel libxml2-devel libxslt-devel openldap-devel python-devel gcc-c++ openssl-devel cmake make gettext gettext-devel curl  \
 && yum clean all
# 创建 postgres 用户和数据目录
RUN groupadd postgres && useradd -m -g postgres postgres  && mkdir -p /pgdata/data && chown postgres:postgres -R /pgdata/

# 使用 curl 下载 PostgreSQL 源代码并解压
RUN mkdir -p /tmp/postgresql-src &&  chmod 755 /tmp/postgresql-src && \
    curl -L https://ftp.postgresql.org/pub/source/v14.1/postgresql-14.1.tar.gz | tar -xz -C /tmp/postgresql-src --strip-components=1 \

# 配置、编译并安装 PostgreSQL
RUN cd /tmp/postgresql-src && \
    ./configure --prefix=/usr/local/pgsql-14.1 \
                --with-segsize=16 \
                --with-wal-segsize=512 \
                --with-blocksize=32 \
                --with-wal-blocksize=64 \
                --with-libxslt \
                --enable-thread-safety \
                --with-pgport=5433 \
                --with-libedit-preferred \
                --with-perl \
                --with-openssl \
                --with-libxml \
                --with-libxslt \
                --enable-thread-safety \
                --enable-nls=en_US.UTF-8 && \
    make -j4  && make install
USER postgres
# 初始化数据库并生成配置文件
RUN /usr/local/pgsql-14.1/bin/initdb -D /pgdata/data





# 设置环境变量
ENV PGHOME=/usr/local/pgsql-14.1
ENV PGDATA=/pgdata/data
ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PGHOME/lib
ENV PATH=$PATH:$PGHOME/bin/
# 添加 PostgreSQL 的 bin 目录到 PATH
ENV PATH=$PATH:/usr/local/pgsql-14.1/bin/
ENV POSTGRES_USER root
ENV POSTGRES_PASSWORD root
ENV POSTGRES_DB highgo


# 启动 PostgreSQL 服务，并指定用户名、密码、数据库和端口
CMD ["/usr/local/pgsql-14.1/bin/postgres", "-D", "/pgdata/data", "-c", "max_connections=100", "-c", "listen_addresses=*", "-c", "port=5433", "-c", "log_statement=all"]
#CMD ["/bin/bash", "-c", "source /root/.bash_profile && /usr/local/pgsql-14.1/bin/postgres -D /pgdata/data"]
