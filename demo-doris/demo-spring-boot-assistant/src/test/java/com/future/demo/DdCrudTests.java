package com.future.demo;

import com.future.demo.entity.Dd;
import com.future.demo.service.DdService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * demot.dd CRUD 测试（需本地 demo-doris FE 9030 可用）。
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DdCrudTests {

    private static final Long COMPANY_ID = 900001L;
    private static final String DJ_TYPE = "crud_test";
    private static final String DJ_TYPE_SUB = "jl";
    private static final Long DJ_ID = 900001L;
    private static final Long JL_ID = 1L;
    private static final Long MT_ID = 0L;
    private static final Long MX_ID = 0L;

    @Resource
    private DdService ddService;

    @Test
    public void testCrud() {
        Dd key = newUk();
        // 清理残留
        ddService.deleteByUk(key);

        // Create
        Dd insert = newUk();
        insert.setDh("DH-CRUD-001");
        insert.setBrand("demo-brand");
        insert.setPs(new BigDecimal("1.0000"));
        insert.setSl(new BigDecimal("10.5000"));
        insert.setIs_delete(0);
        Assert.assertTrue(ddService.insertRow(insert));

        // Read
        Dd loaded = ddService.getByUk(key);
        Assert.assertNotNull(loaded);
        Assert.assertEquals("DH-CRUD-001", loaded.getDh());
        Assert.assertEquals("demo-brand", loaded.getBrand());
        Assert.assertEquals(0, new BigDecimal("10.5000").compareTo(loaded.getSl()));

        // Update（Doris Unique Key MOW：按 UK 更新非键列）
        Dd update = newUk();
        update.setDh("DH-CRUD-002");
        update.setBrand("demo-brand-updated");
        update.setSl(new BigDecimal("20.0000"));
        Assert.assertTrue(ddService.updateByUk(update));

        Dd afterUpdate = ddService.getByUk(key);
        Assert.assertNotNull(afterUpdate);
        Assert.assertEquals("DH-CRUD-002", afterUpdate.getDh());
        Assert.assertEquals("demo-brand-updated", afterUpdate.getBrand());
        Assert.assertEquals(0, new BigDecimal("20.0000").compareTo(afterUpdate.getSl()));

        // Delete
        Assert.assertTrue(ddService.deleteByUk(key));
        Assert.assertNull(ddService.getByUk(key));
        log.info("demot.dd CRUD ok");
    }

    private static Dd newUk() {
        Dd dd = new Dd();
        dd.setCompany_id(COMPANY_ID);
        dd.setDj_type(DJ_TYPE);
        dd.setDj_type_sub(DJ_TYPE_SUB);
        dd.setDj_id(DJ_ID);
        dd.setJl_id(JL_ID);
        dd.setMt_id(MT_ID);
        dd.setMx_id(MX_ID);
        return dd;
    }
}
