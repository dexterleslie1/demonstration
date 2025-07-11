package com.future.demo.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommonMapper {

    @Update("create table if not exists t_${flag}_id_list(\n" +
            "    id              bigint not null primary key auto_increment,\n" +
            "    bizId           bigint not null\n" +
            ") engine=innodb character set utf8mb4 collate utf8mb4_general_ci;")
    void createTable(String flag);

    @Insert("insert ignore into t_flag_created(flag,createTime) values(#{flag},now())")
    int insertFlagCreated(String flag);

    @Insert("<script>" +
            "insert into t_${flag}_id_list(bizId) values\n" +
            "    <foreach item=\"e\" collection=\"idList\" separator=\",\">\n" +
            "        (#{e})\n" +
            "    </foreach>" +
            "</script>")
    void insertIdList(String flag, List<Long> idList);

    @Update("drop table if exists t_${flag}_id_list")
    void resetDropTable(String flag);

    @Delete("delete from t_flag_created where flag=#{flag}")
    int deleteFlagCreated(String flag);

    @Select("select max(id) from t_${flag}_id_list")
    Long getMaxId(String flag);

    @Select("select bizId from t_${flag}_id_list where id>=#{idStart} limit #{size}")
    List<Long> listBizIdList(String flag, Long idStart, int size);
}
