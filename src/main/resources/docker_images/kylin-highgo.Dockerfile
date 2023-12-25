FROM feixiangdexiaozhidan/ky10-server-sp2:v11.0
LABEL maintainer="highgo"


# 安装依赖包并清理
RUN yum install -y --nogpgcheck \
    cyrus-sasl perl-ExtUtils-Embed readline readline-devel zlib zlib-devel pam-devel \
    libxml2-devel libxslt-devel openssl openldap-devel python-devel tcl-devel gcc gcc-c++ openssl-devel cmake make gettext gettext-devel rsync curl \
 && yum clean all

# 复制HighGo数据库安装包
COPY hgdb4.5.7-see-kyl-server10sp1-x86-64-20211021.rpm  /tmp
RUN ls -l /tmp
# 安装HighGo数据库、清理临时文件、创建密码文件、初始化数据库
RUN rpm -ivh /tmp/hgdb4.5.7-see-kyl-server10sp1-x86-64-20211021.rpm \
 && . /root/.bash_profile \
 && rm -rf /hgdb4.5.7-see-kyl-server10sp1-x86-64-20211021.rpm \
 && mkdir -p /opt/HighGo4.5.7-see/ \
 && echo "Hello@123" > /opt/HighGo4.5.7-see/pwfile.txt \
 && echo "Hello@123" >> /opt/HighGo4.5.7-see/pwfile.txt \
 && echo "Hello@123" >> /opt/HighGo4.5.7-see/pwfile.txt \
 && chmod 0600 /opt/HighGo4.5.7-see/pwfile.txt \
 && initdb -D $PGDATA --pwfile=/opt/HighGo4.5.7-see/pwfile.txt \
 && cp /opt/HighGo4.5.7-see/etc/server.* $PGDATA \
 && chmod 0600 $PGDATA/server.*
COPY file/* /opt/HighGo4.5.7-see/data/
RUN chmod 0600 /opt/HighGo4.5.7-see/data/hgdb_0_o.lic

ENV HG_BASE=/opt/HighGo4.5.7-see
ENV HGDB_HOME=/opt/HighGo4.5.7-see
ENV PGPORT=5866
ENV PGDATABASE=highgo
ENV PGDATA=$HGDB_HOME/data
ENV PATH=$HGDB_HOME/bin:$PATH
# 启动 PostgreSQL 服务
#CMD ["/opt/HighGo4.5.7-see/bin/postgres", "-D", "/opt/HighGo4.5.7-see/data"]
CMD ["/bin/bash", "-c", "source /root/.bash_profile && /opt/HighGo4.5.7-see/bin/postgres -D /opt/HighGo4.5.7-see/data"]
