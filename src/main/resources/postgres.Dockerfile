FROM centos:centos7.6.1810

# 安装依赖包
RUN yum install -y --nogpgcheck cyrus-sasl perl-ExtUtils-Embed readline-devel zlib-devel pam-devel libxml2-devel libxslt-devel openldap-devel python-devel gcc-c++ openssl-devel cmake make gettext gettext-devel curl

# 创建 postgres 用户和数据目录
RUN useradd postgres && mkdir -p /pgdata/data && chown postgres.postgres -R /pgdata/

# 使用 curl 下载 PostgreSQL 源代码并解压
RUN mkdir -p /tmp/postgresql-src && \
    curl -L https://ftp.postgresql.org/pub/source/v11.6/postgresql-11.6.tar.gz | tar -xz -C /tmp/postgresql-src --strip-components=1

# 配置、编译并安装 PostgreSQL
RUN cd /tmp/postgresql-src && \
    ./configure --prefix=/usr/local/pgsql-11.6 \
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
    make && make install

# 切换用户到 root
USER postgres



# 初始化数据库并生成配置文件
RUN /usr/local/pgsql-11.6/bin/initdb -D /pgdata/data



# 设置环境变量
ENV PGHOME=/usr/local/pgsql-11.6
ENV PGDATA=/pgdata/data
ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PGHOME/lib
ENV PATH=$PATH:$PGHOME/bin/
# 添加 PostgreSQL 的 bin 目录到 PATH
ENV PATH=$PATH:/usr/local/pgsql-11.6/bin/
ENV POSTGRES_USER root
ENV POSTGRES_PASSWORD root
ENV POSTGRES_DB highgo


# 启动 PostgreSQL 服务，并指定用户名、密码、数据库和端口
CMD ["/usr/local/pgsql-11.6/bin/postgres", "-D", "/pgdata/data", "-c", "max_connections=100", "-c", "listen_addresses=*", "-c", "port=5433", "-c", "log_statement=all"]

