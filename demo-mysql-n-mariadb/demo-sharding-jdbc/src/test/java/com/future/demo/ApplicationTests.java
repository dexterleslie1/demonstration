package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import com.future.common.exception.BusinessException;
import com.future.demo.bean.*;
import com.future.demo.mapper.DictMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.mapper.UserMapper;
import com.future.demo.service.OrderService;
import com.future.demo.util.OrderRandomlyUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class ApplicationTests {

    @Resource
    OrderMapper orderMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    DictMapper dictMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    OrderService orderService;

    @Test
    public void contextLoads() {
        int totalCount = 20;

        this.orderMapper.truncate();

        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            Order order = new Order();
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            order.setCreateTime(now);
            order.setUserId(RandomUtil.randomLong(1, Long.MAX_VALUE));
            order.setMerchantId(RandomUtil.randomLong(1, Long.MAX_VALUE));
            order.setTotalAmount(new BigDecimal(1000));
            order.setTotalCount(10);
            Status status = OrderRandomlyUtil.getStatusRandomly();
            order.setStatus(status);
            order.setDeleteStatus(OrderRandomlyUtil.getDeleteStatusRandomly());
            this.orderMapper.add(order);
            orderList.add(order);

            Long id = order.getId();

            order = this.orderMapper.get(id);
            Assertions.assertEquals(id, order.getId());
            Assertions.assertEquals(status, order.getStatus());
            Assertions.assertEquals(now, order.getCreateTime());
        }

        Map<Long, Order> idToOrderMap = orderList.stream().collect(Collectors.toMap(Order::getId, o -> o));
        List<Order> orderListResult = this.orderMapper.listById(orderList.stream().map(Order::getId).collect(Collectors.toList()));
        Assertions.assertEquals(orderList.size(), orderListResult.size());
        Map<Long, Order> finalIdToOrderMap1 = idToOrderMap;
        orderListResult.forEach(o -> {
            Assertions.assertEquals(finalIdToOrderMap1.get(o.getId()).getCreateTime(), o.getCreateTime());
            Assertions.assertEquals(finalIdToOrderMap1.get(o.getId()).getUserId(), o.getUserId());
            Assertions.assertEquals(finalIdToOrderMap1.get(o.getId()).getMerchantId(), o.getMerchantId());
        });

        // region 测试批量插入
        this.orderMapper.truncate();

        orderList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            Order order = new Order();
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            order.setCreateTime(now);
            order.setUserId(RandomUtil.randomLong(1, Long.MAX_VALUE));
            order.setMerchantId(RandomUtil.randomLong(1, Long.MAX_VALUE));
            order.setTotalAmount(new BigDecimal(1000));
            order.setTotalCount(10);
            order.setStatus(OrderRandomlyUtil.getStatusRandomly());
            order.setDeleteStatus(OrderRandomlyUtil.getDeleteStatusRandomly());
            orderList.add(order);
        }
        this.orderMapper.addBatch(orderList);

        orderList = this.orderMapper.listAll();
        idToOrderMap = orderList.stream().collect(Collectors.toMap(Order::getId, o -> o));
        orderListResult = this.orderMapper.listById(orderList.stream().map(Order::getId).collect(Collectors.toList()));
        Assertions.assertEquals(orderList.size(), orderListResult.size());
        Map<Long, Order> finalIdToOrderMap = idToOrderMap;
        orderListResult.forEach(o -> {
            Assertions.assertEquals(finalIdToOrderMap.get(o.getId()).getCreateTime(), o.getCreateTime());
            Assertions.assertEquals(finalIdToOrderMap.get(o.getId()).getUserId(), o.getUserId());
            Assertions.assertEquals(finalIdToOrderMap.get(o.getId()).getMerchantId(), o.getMerchantId());
        });

        // endregion

        // region 综合查询测试

        orderListResult = this.orderMapper.listAll();

        // 根据用户ID查询
        Order expectedOrder = orderListResult.get(RandomUtil.randomInt(0, orderListResult.size()));
        orderList = this.orderMapper.listByUserId(expectedOrder.getUserId(),
                expectedOrder.getStatus(), expectedOrder.getDeleteStatus(), null, null, 0L, 10L);
        Assertions.assertEquals(1, orderList.size());
        Assertions.assertEquals(expectedOrder.getId(), orderList.get(0).getId());

        // 根据商家ID查询
        expectedOrder = orderListResult.get(RandomUtil.randomInt(0, orderListResult.size()));
        orderList = this.orderMapper.listByMerchantId(expectedOrder.getMerchantId(),
                expectedOrder.getStatus(), expectedOrder.getDeleteStatus(), null, null, 0L, 10L);
        Assertions.assertEquals(1, orderList.size());
        Assertions.assertEquals(expectedOrder.getId(), orderList.get(0).getId());

        // endregion

        // region 测试垂直分库

        this.userMapper.truncate();

        String name = "Dexter";
        LocalDateTime createTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        User user = new User();
        user.setName(name);
        user.setCreateTime(createTime);
        this.userMapper.add(user);

        List<User> userList = this.userMapper.listAll();
        Assertions.assertEquals(1, userList.size());
        Assertions.assertEquals(name, userList.get(0).getName());
        Assertions.assertEquals(createTime, userList.get(0).getCreateTime());

        // endregion

        // region 测试公共表

        this.dictMapper.truncate();

        Integer id = 11;
        name = "dict1";
        String value = "value1";

        Dict dict = new Dict();
        dict.setId(id);
        dict.setName(name);
        dict.setValue(value);
        this.dictMapper.add(dict);

        Dict dictResult = this.dictMapper.getById(id);
        Assertions.assertEquals(id, dictResult.getId());
        Assertions.assertEquals(name, dictResult.getName());
        Assertions.assertEquals(value, dictResult.getValue());

        List<Dict> dictList = this.dictMapper.listAll();
        Assertions.assertEquals(1, dictList.size());
        Assertions.assertEquals(id, dictList.get(0).getId());
        Assertions.assertEquals(name, dictList.get(0).getName());
        Assertions.assertEquals(value, dictList.get(0).getValue());

        this.dictMapper.delete(id);
        dictList = this.dictMapper.listAll();
        Assertions.assertEquals(0, dictList.size());

        // endregion

        // region 绑定表

        String productNamePrefix = "商品";
        String descriptionPrefix = "商品描述";

        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String productName = productNamePrefix + i;
            Product product = new Product();
            product.setName(productName);
            product.setCreateTime(createTime);
            this.productMapper.addProduct(product);
            Long productId = product.getId();

            String description = descriptionPrefix + i;
            ProductDescription productDescription = new ProductDescription();
            productDescription.setProductId(productId);
            productDescription.setDescription(description);
            this.productMapper.addProductDescription(productDescription);
            Long productDescriptionId = productDescription.getId();

            product = this.productMapper.getById(productId);
            Assertions.assertEquals(productId, product.getId());
            Assertions.assertEquals(productName, product.getName());
            Assertions.assertEquals(createTime, product.getCreateTime());
            Assertions.assertEquals(productDescriptionId, product.getDescriptionId());
            Assertions.assertEquals(description, product.getDescription());
            productList.add(product);
        }

        List<Long> productIdList = productList.stream().map(Product::getId).collect(Collectors.toList());
        List<Product> productListResult = this.productMapper.listByIds(productIdList);
        Assertions.assertEquals(productList.size(), productListResult.size());
        productList.forEach(o -> {
            Product productResult = productListResult.stream().filter(oInternal -> o.getId().equals(oInternal.getId())).collect(Collectors.toList()).get(0);
            Assertions.assertEquals(o.getId(), productResult.getId());
            Assertions.assertEquals(o.getName(), productResult.getName());
            Assertions.assertEquals(o.getCreateTime(), productResult.getCreateTime());
            Assertions.assertEquals(o.getDescriptionId(), productResult.getDescriptionId());
            Assertions.assertEquals(o.getDescription(), productResult.getDescription());
        });

        // endregion
    }

    /**
     * 测试标准分片策略的范围分片算法
     */
    @Test
    public void testStandardStrategyRangeShardingAlgorithm() {
        List<Order> orderList = this.orderMapper.listByUserIdRange(1L, 10000L);
        Assertions.assertEquals(0, orderList.size());
    }

    /**
     * 测试事务
     */
    @Test
    public void testTransaction() throws BusinessException {
        // 创建用户
        User user = new User();
        user.setName("测试用户1");
        user.setCreateTime(LocalDateTime.now());
        BigDecimal balance = new BigDecimal("1000.00000");
        user.setBalance(balance);
        this.userMapper.add(user);

        Long userId = user.getId();

        // 创建订单成功
        BigDecimal amount = new BigDecimal(100);
        this.orderService.create(userId, amount);
        List<Order> orderList = this.orderMapper.listByUserId(userId, null, null, null, null, null, null);
        Assertions.assertEquals(1, orderList.size());
        user = this.userMapper.get(userId);
        Assertions.assertEquals(balance.subtract(amount), user.getBalance());

        // 创建订单失败，余额不足
        try {
            amount = new BigDecimal(1000);
            this.orderService.create(userId, amount);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertEquals("余额不足", ex.getMessage());
        }

        // 验证事务中的数据是否完整
        orderList = this.orderMapper.listByUserId(userId, null, null, null, null, null, null);
        // 之前的一笔订单数据
        Assertions.assertEquals(1, orderList.size());
        user = this.userMapper.get(userId);
        // 之前的余额数据
        Assertions.assertEquals(balance.subtract(new BigDecimal("100")), user.getBalance());
    }
}
