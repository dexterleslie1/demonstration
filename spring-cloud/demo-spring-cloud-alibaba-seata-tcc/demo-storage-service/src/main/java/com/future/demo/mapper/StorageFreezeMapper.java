package com.future.demo.mapper;

import com.future.demo.entity.AccountFreeze;
import com.future.demo.entity.StorageFreeze;
import org.apache.ibatis.annotations.*;

@Mapper
public interface StorageFreezeMapper {
    @Insert("insert ignore into t_storage_freeze(xid,product_id,freeze_stock,state) values(#{xid},#{productId},#{freezeStock},#{state})")
    int insert(StorageFreeze storageFreeze);

    @Delete("delete from t_storage_freeze where xid=#{xid}")
    int delete(@Param("xid") String xid);

    @Select("select * from t_storage_freeze where xid=#{xid}")
    StorageFreeze getByXid(@Param("xid") String xid);

    @Update("update t_storage_freeze set freeze_stock=#{freezeStock},`state`=#{state} where xid=#{xid}")
    int update(StorageFreeze storageFreeze);
}
