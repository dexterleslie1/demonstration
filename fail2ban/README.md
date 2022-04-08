# 演示fail2ban配置和使用

## fail2ban日志滚动频率（rollover performed）配置

> [Configuration logrotate for fail2ban](https://en-wiki.ikoula.com/en/Configuration_logrotate_for_fail2ban)

NOTE: 因为fail2ban的 /var/log/fail2ban.log 日志文件使用系统的logrotate滚动日志，所以参考linux的logrotate配置fail2ban的logrotate