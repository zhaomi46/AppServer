# #tomcat镜像
# FROM tomcat:7.0.86

# #作者
# LABEL maintainer="zgl"

# #安装
# RUN apt-get install -y telnet nc; exit 0

# #
# VOLUME ["/root/tomcat"]

# #TOMCAT环境变量
# ENV CATALINA_BASE:   /usr/local/tomcat \
#     CATALINA_HOME:   /usr/local/tomcat \
#     CATALINA_TMPDIR: /usr/local/tomcat/temp \
#     JRE_HOME:        /usr

# #启动入口
# ENTRYPOINT ["catalina.sh","run"]

# #�������
# # HEALTHCHECK --interval=10s --timeout=3s \
# #   CMD nc -z localhost 5198 >/dev/null || exit 1

# #拷贝jar包到tomcat中
# COPY swsad-0.0.1-SNAPSHOT.jar ${CATALINA_HOME}/webapps/
FROM java:8-alpine

LABEL maintainer="zgl"

ADD swsad-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar","/app.jar"]
