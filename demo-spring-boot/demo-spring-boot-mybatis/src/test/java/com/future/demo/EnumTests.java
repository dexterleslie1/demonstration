package com.future.demo;

import com.future.demo.bean.EnumStoringAsEnum;
import com.future.demo.bean.EnumStoringAsInt;
import com.future.demo.bean.EnumStoringAsVarchar;
import com.future.demo.bean.Status;
import com.future.demo.mapper.EnumMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class EnumTests {

    @Resource
    EnumMapper enumMapper;

    /**
     * 测试枚举存储和读取正确性
     */
    @Test
    public void testEnumStoringCorrectness() {
        // 测试枚举读取
        EnumStoringAsInt enumBeanStoringAsInt = new EnumStoringAsInt();
        enumBeanStoringAsInt.setStatus(1);
        this.enumMapper.insertEnumStoringAsInt(enumBeanStoringAsInt);
        long id = enumBeanStoringAsInt.getId();
        enumBeanStoringAsInt = this.enumMapper.selectEnumStoringAsInt(id);
        Assertions.assertEquals(1, enumBeanStoringAsInt.getStatus());

        // 测试枚举读取
        EnumStoringAsVarchar enumStoringAsVarchar = new EnumStoringAsVarchar();
        enumStoringAsVarchar.setStatus(Status.Paying);
        this.enumMapper.insertEnumStoringAsVarchar(enumStoringAsVarchar);
        id = enumStoringAsVarchar.getId();
        enumStoringAsVarchar = this.enumMapper.selectEnumStoringAsVarchar(id);
        Assertions.assertEquals(Status.Paying, enumStoringAsVarchar.getStatus());

        EnumStoringAsEnum enumStoringAsEnum = new EnumStoringAsEnum();
        enumStoringAsEnum.setStatus(Status.InProgress);
        this.enumMapper.insertEnumStoringAsEnum(enumStoringAsEnum);
        id = enumStoringAsEnum.getId();
        enumStoringAsEnum = this.enumMapper.selectEnumStoringAsEnum(id);
        Assertions.assertEquals(Status.InProgress, enumStoringAsEnum.getStatus());
    }

    /**
     * 插入 100w 记录协助存储空间使用率和查询性能测试
     */
    @Test
    public void testEnumStorageSpaceUsage() {
        int totalCount = 1000000;
        // 订单状态 0-创建，1-支付中，2-支付成功，3-支付失败，4-取消订单
        List<Integer> statusList = new ArrayList<Integer>() {{
            this.add(0);
            this.add(1);
            this.add(2);
            this.add(3);
            this.add(4);
        }};
        List<EnumStoringAsInt> enumStoringAsIntList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            EnumStoringAsInt bean = new EnumStoringAsInt();
            int status = statusList.get(RandomUtils.nextInt(0, statusList.size()));
            bean.setStatus(status);
            enumStoringAsIntList.add(bean);
            if (enumStoringAsIntList.size() == 1000) {
                this.enumMapper.batchInsertEnumStoringAsInt(enumStoringAsIntList);
                enumStoringAsIntList = new ArrayList<>();
            }
        }

        Status[] statusArr = Status.values();
        List<EnumStoringAsVarchar> enumStoringAsVarcharList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            EnumStoringAsVarchar bean = new EnumStoringAsVarchar();
            Status status = statusArr[RandomUtils.nextInt(0, statusArr.length)];
            bean.setStatus(status);
            enumStoringAsVarcharList.add(bean);
            if (enumStoringAsVarcharList.size() == 1000) {
                this.enumMapper.batchInsertEnumStoringAsVarchar(enumStoringAsVarcharList);
                enumStoringAsVarcharList = new ArrayList<>();
            }
        }

        List<EnumStoringAsEnum> enumStoringAsEnumList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            EnumStoringAsEnum bean = new EnumStoringAsEnum();
            Status status = statusArr[RandomUtils.nextInt(0, statusArr.length)];
            bean.setStatus(status);
            enumStoringAsEnumList.add(bean);
            if (enumStoringAsEnumList.size() == 1000) {
                this.enumMapper.batchInsertEnumStoringAsEnum(enumStoringAsEnumList);
                enumStoringAsEnumList = new ArrayList<>();
            }
        }
    }
}
