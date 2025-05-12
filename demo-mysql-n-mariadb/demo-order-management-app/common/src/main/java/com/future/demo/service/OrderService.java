package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.common.exception.BusinessException;
import com.future.demo.dto.OrderDTO;
import com.future.demo.dto.OrderDetailDTO;
import com.future.demo.entity.*;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Value("${productStock:100000000}")
    Integer productStock;

    @Resource
    OrderRandomlyUtil orderRandomlyUtil;
    @Autowired
    SnowflakeService snowflakeService;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void init() {
        // region 准备协助基准测试的数据

        for (int i = 0; i < this.orderRandomlyUtil.productIdArray.length; i++) {
            long productId = this.orderRandomlyUtil.productIdArray[i];
            // 准备 db 数据辅助基于数据库的测试
            this.productMapper.delete(productId);
            ProductModel productModel = new ProductModel();
            productModel.setId(productId);
            productModel.setName("产品" + productId);
            productModel.setStock(productStock);

            long merchantId = this.orderRandomlyUtil.getMerchantIdRandomly();
            productModel.setMerchantId(merchantId);

            this.productMapper.insert(productModel);
        }

        // endregion
    }

    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;
    @Resource
    ProductMapper productMapper;

    // 基于数据库实现商品秒杀
    public void createOrderBasedDB(Long userId, Long productId, Integer amount) throws Exception {
        // 判断库存是否充足
        ProductModel productModel = this.productMapper.getById(productId);
        if (productModel.getStock() <= 0) {
            throw new BusinessException("库存不足");
        }

        // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
        OrderService proxy = (OrderService) AopContext.currentProxy();
        proxy.createOrderInternal(userId, productId, amount);
    }

    // 抛出异常后回滚事务
    @Transactional(rollbackFor = Exception.class)
    public void createOrderInternal(Long userId, Long productId, Integer amount) throws Exception {
        // 创建订单
        OrderModel orderModel = new OrderModel();

        // biginteger 类型
        Long orderId = this.snowflakeService.getId("order").getId();
        Long userIdStripOff = userId % 16;
        String orderIdBinaryStr = Long.toBinaryString(orderId) +
                StringUtils.leftPad(Long.toBinaryString(userIdStripOff), 4, "0");
        BigInteger orderIdBigInt = new BigInteger(orderIdBinaryStr, 2);
        orderModel.setId(orderIdBigInt);
        // uuid string 类型
        /*String orderId = UUID.randomUUID().toString();
        orderModel.setId(orderId);*/

        orderModel.setUserId(userId);
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        orderModel.setCreateTime(createTime);

        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
        orderModel.setDeleteStatus(deleteStatus);

        Status status = OrderRandomlyUtil.getStatusRandomly();
        orderModel.setStatus(status);

        int count = this.orderMapper.insert(orderModel);
        if (count <= 0) {
            throw new BusinessException("创建订单失败");
        }

        ProductModel productModel = this.productMapper.getById(productId);

        OrderDetailModel orderDetailModel = new OrderDetailModel();

        Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
        orderDetailModel.setId(orderDetailId);

        orderDetailModel.setOrderId(orderModel.getId());
        orderDetailModel.setUserId(userId);
        orderDetailModel.setProductId(productId);
        orderDetailModel.setMerchantId(productModel.getMerchantId());
        orderDetailModel.setAmount(amount);
        count = this.orderDetailMapper.insert(orderDetailModel);
        // 判断用户是否重复下单
        if (count <= 0) {
            throw new BusinessException("用户重复下单");
        }

        // 扣减库存
        count = this.productMapper.decreaseStock(productId, amount);
        if (count <= 0) {
            throw new BusinessException("扣减库存失败");
        }
    }

    public void insertBatch() {
        List<OrderModel> orderModelList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Long userId = this.orderRandomlyUtil.getUserIdRandomly();
            // 创建订单
            OrderModel orderModel = new OrderModel();

            // biginteger 类型
            Long orderId = this.snowflakeService.getId("order").getId();
            Long userIdStripOff = userId % 16;
            String orderIdBinaryStr = Long.toBinaryString(orderId) +
                    StringUtils.leftPad(Long.toBinaryString(userIdStripOff), 4, "0");
            BigInteger orderIdBigInt = new BigInteger(orderIdBinaryStr, 2);
            orderModel.setId(orderIdBigInt);
            // uuid string 类型
            /*String orderId = UUID.randomUUID().toString();
            orderModel.setId(orderId);*/

            orderModel.setUserId(userId);
            LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
            orderModel.setCreateTime(createTime);

            DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
            orderModel.setDeleteStatus(deleteStatus);

            Status status = OrderRandomlyUtil.getStatusRandomly();
            orderModel.setStatus(status);
            orderModelList.add(orderModel);
        }
        this.orderMapper.insertBatch(orderModelList);

        List<Long> productIdList = orderModelList.stream().map(o -> {
            Long productId = this.orderRandomlyUtil.getProductIdRandomly();
            return productId;
        }).distinct().collect(Collectors.toList());
        List<ProductModel> productModelList = this.productMapper.list(productIdList);
        List<OrderDetailModel> orderDetailModelList = orderModelList.stream().map(o -> {
            ProductModel productModel = productModelList.get(RandomUtil.randomInt(0, productModelList.size()));

            Integer amount = 1;
            Long userId = o.getUserId();

            OrderDetailModel orderDetailModel = new OrderDetailModel();

            Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
            orderDetailModel.setId(orderDetailId);

            orderDetailModel.setOrderId(o.getId());
            orderDetailModel.setUserId(userId);
            orderDetailModel.setProductId(productModel.getId());
            orderDetailModel.setMerchantId(productModel.getMerchantId());
            orderDetailModel.setAmount(amount);
            return orderDetailModel;
        }).collect(Collectors.toList());
        this.orderDetailMapper.insertBatch(orderDetailModelList);
    }

    /**
     * 根据订单ID查询订单
     *
     * @param orderId
     * @return
     */
    // long 类型
    /*public OrderDTO getById(Long orderId) {*/
    // int 类型
    /*public OrderDTO getById(Integer orderId) {*/
    // biginteger 类型
    public OrderDTO getById(BigInteger orderId) {
    // uuid string 类型
    /*public OrderDTO getById(String orderId) {*/
        OrderModel orderModel = this.orderMapper.getById(orderId);
        if (orderModel == null) {
            return null;
        }

        List<OrderDTO> orderDTOList = this.convertOrderEntityToOrderDTO(Collections.singletonList(orderModel));
        return orderDTOList.get(0);
    }

    /**
     * 用户查询指定日期范围+所有状态的订单
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByUserIdAndWithoutStatus(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        List<OrderModel> orderModelList = this.orderMapper.listByUserId(userId, null, DeleteStatus.Normal, startTime, endTime, 0L, 20L);
        return this.convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @param userId
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByUserIdAndStatus(
            Long userId,
            Status status,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        List<OrderModel> orderModelList = this.orderMapper.listByUserId(userId, status, DeleteStatus.Normal, startTime, endTime, 0L, 20L);
        return this.convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 商家查询指定日期范围+所有状态的订单
     *
     * @param merchantId
     * @param deleteStatus
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByMerchantIdAndWithoutStatus(
            Long merchantId,
            DeleteStatus deleteStatus,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        List<OrderModel> orderModelList = this.orderMapper.listByMerchantId(merchantId, null, deleteStatus, startTime, endTime, 0L, 20L);
//        List<OrderDetailDTO> orderDetailDTOList;
//        if (orderDetailModelList != null && !orderDetailModelList.isEmpty()) {
//            orderDetailDTOList = orderDetailModelList.stream().map(o -> {
//                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
//                BeanUtils.copyProperties(o, orderDetailDTO);
//                return orderDetailDTO;
//            }).collect(Collectors.toList());
//        } else {
//            orderDetailDTOList = new ArrayList<>();
//        }
//        return orderDetailDTOList;
        return convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @param merchantId
     * @param status
     * @param deleteStatus
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDTO> listByMerchantIdAndStatus(
            Long merchantId,
            Status status,
            DeleteStatus deleteStatus,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        List<OrderModel> orderModelList = this.orderMapper.listByMerchantId(merchantId, status, deleteStatus, startTime, endTime, 0L, 20L);
        return convertOrderEntityToOrderDTO(orderModelList);
    }

    /**
     * 把 OrderEntity 转换为 OrderDTO 类型
     *
     * @param orderList
     * @return
     */
    public List<OrderDTO> convertOrderEntityToOrderDTO(List<OrderModel> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return new ArrayList<>();
        }

        // long 类型
        /*List<Long> orderIdList = orderList.stream().map(OrderModel::getId).collect(Collectors.toList());*/
        // int 类型
        /*List<Integer> orderIdList = orderList.stream().map(OrderModel::getId).collect(Collectors.toList());*/
        // biginteger 类型
        List<BigInteger> orderIdList = orderList.stream().map(OrderModel::getId).collect(Collectors.toList());
        // uuid string 类型
        /*List<String> orderIdList = orderList.stream().map(OrderModel::getId).collect(Collectors.toList());*/

        List<OrderDTO> orderDTOList = null;
        if (!orderList.isEmpty()) {
            List<OrderDetailModel> orderDetailList = this.orderDetailMapper.list(orderIdList);

            // long 类型
            /*Map<Long, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));*/
            // int 类型
            /*Map<Integer, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));*/
            // biginteger 类型
            Map<BigInteger, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));
            // uuid string 类型
            /*Map<String, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));*/

            orderDTOList = orderList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);

                // long 类型
                /*Long orderId = o.getId();*/
                // int 类型
                /*Integer orderId = o.getId();*/
                // biginteger 类型
                BigInteger orderId = o.getId();
                // uuid string 类型
                /*String orderId = o.getId();*/

                if (orderDetailGroupByOrderId.containsKey(orderId)) {
                    List<OrderDetailModel> orderDetailListTemporary = orderDetailGroupByOrderId.get(orderId);
                    List<OrderDetailDTO> orderDetailDTOList = orderDetailListTemporary.stream().map(oInternal1 -> {
                        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                        BeanUtils.copyProperties(oInternal1, orderDetailDTO);
                        return orderDetailDTO;
                    }).collect(Collectors.toList());
                    orderDTO.setOrderDetailList(orderDetailDTOList);
                }
                return orderDTO;
            }).collect(Collectors.toList());
        }

        if (orderDTOList == null) {
            orderDTOList = new ArrayList<>();
        }

        return orderDTOList;
    }

    /**
     * 还原商品库存
     */
    public void restoreProductStock() {
        this.productMapper.restoreStock(productStock);
    }
}
