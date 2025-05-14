package com.future.demo.mapper;

import com.future.demo.bean.EnumStoringAsEnum;
import com.future.demo.bean.EnumStoringAsInt;
import com.future.demo.bean.EnumStoringAsVarchar;
import com.future.demo.bean.Status;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EnumMapper {
    @Insert("insert into t_enum_storing_as_int (`status`) values (#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertEnumStoringAsInt(EnumStoringAsInt bean);

    @Select("<script>" +
            "insert into t_enum_storing_as_int(status)\n" +
            "        values\n" +
            "        <foreach item=\"e\" collection=\"beans\" separator=\",\">\n" +
            "            (#{e.status})\n" +
            "        </foreach>" +
            "</script>")
    void batchInsertEnumStoringAsInt(List<EnumStoringAsInt> beans);

    @Select("select * from t_enum_storing_as_int where id=#{id}")
    EnumStoringAsInt selectEnumStoringAsInt(@Param("id") long id);

    @Select("select count(id) from t_enum_storing_as_int where `status`=#{status}")
    int countEnumStoringAsIntByStatus(@Param("status") int status);

    @Insert("insert into t_enum_storing_as_varchar (`status`) values (#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertEnumStoringAsVarchar(EnumStoringAsVarchar bean);

    @Select("<script>" +
            "insert into t_enum_storing_as_varchar(status)\n" +
            "        values\n" +
            "        <foreach item=\"e\" collection=\"beans\" separator=\",\">\n" +
            "            (#{e.status})\n" +
            "        </foreach>" +
            "</script>")
    void batchInsertEnumStoringAsVarchar(List<EnumStoringAsVarchar> beans);

    @Select("select * from t_enum_storing_as_varchar where id=#{id}")
    EnumStoringAsVarchar selectEnumStoringAsVarchar(@Param("id") long id);

    @Select("select count(id) from t_enum_storing_as_varchar where `status`=#{status}")
    int countEnumStoringAsVarcharByStatus(@Param("status") Status status);

    @Insert("insert into t_enum_storing_as_enum (`status`) values (#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertEnumStoringAsEnum(EnumStoringAsEnum bean);

    @Select("<script>" +
            "insert into t_enum_storing_as_enum(status)\n" +
            "        values\n" +
            "        <foreach item=\"e\" collection=\"beans\" separator=\",\">\n" +
            "            (#{e.status})\n" +
            "        </foreach>" +
            "</script>")
    void batchInsertEnumStoringAsEnum(List<EnumStoringAsEnum> beans);

    @Select("select * from t_enum_storing_as_enum where id=#{id}")
    EnumStoringAsEnum selectEnumStoringAsEnum(@Param("id") long id);

    @Select("select count(id) from t_enum_storing_as_enum where `status`=#{status}")
    int countEnumStoringAsEnumByStatus(@Param("status") Status status);
}
