create table if not exists t2(
  id int auto_increment,
  createDate datetime not null,
  primary key(id,createDate)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci
    partition by range(to_days(createDate))(
    partition pdefault values less than (to_days('2015-12-13 00:00:00'))
);