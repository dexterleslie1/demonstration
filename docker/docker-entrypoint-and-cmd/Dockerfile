# FROM centos:7.6.1810
FROM mariadb:10.4.19

COPY entrypoint.sh /usr/local/bin
CMD ["Output from default CMD when not specify any external CMD"]
ENTRYPOINT ["sh","/usr/local/bin/entrypoint.sh"]

