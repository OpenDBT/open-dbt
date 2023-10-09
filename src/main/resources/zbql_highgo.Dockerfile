FROM centos:7.9.2009
MAINTAINER highgo<123456@qq.com>
ENV MYPATH /tmp
WORKDIR $MYPATH
RUN yum -y install vim
RUN yum -y install net-tools
EXPOSE 80
CMD /bin/bash -c "echo $MYPATH && echo 'success---------ok' && while true; do sleep 3600; done"
