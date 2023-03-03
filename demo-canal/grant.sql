create user 'canal'@'%' identified by 'canal';
grant SELECT, REPLICATION SLAVE, REPLICATION CLIENT on *.* to 'canal'@'%';