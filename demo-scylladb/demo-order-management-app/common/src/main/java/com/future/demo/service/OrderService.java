package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.dto.OrderDTO;
import com.future.demo.dto.OrderDetailDTO;
import com.future.demo.entity.*;
import com.future.demo.mapper.IndexMapper;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @PostConstruct
    public void init() throws BusinessException {
//        // region 准备协助基准测试的数据
//        this.productMapper.truncate();
//
//        List<ProductModel> productModelList = new ArrayList<>();
//        for (int i = 0; i < this.orderRandomlyUtil.productIdArray.length; i++) {
//            long productId = this.orderRandomlyUtil.productIdArray[i];
//            // 准备 db 数据辅助基于数据库的测试
//            ProductModel productModel = new ProductModel();
//            productModel.setId(productId);
//            productModel.setName("产品" + productId);
//            productModel.setStock(productStock);
//
//            long merchantId = this.orderRandomlyUtil.getMerchantIdRandomly();
//            productModel.setMerchantId(merchantId);
//            productModelList.add(productModel);
//
//            if (productModelList.size() == 1000 || (i + 1) == this.orderRandomlyUtil.productIdArray.length) {
//                this.productMapper.insertBatch(productModelList);
//                productModelList = new ArrayList<>();
//            }
//        }
//
//        // endregion
    }

    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;
    @Resource
    IndexMapper indexMapper;

    public void insertBatch() throws BusinessException {
        List<OrderModel> orderModelList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Long userId = this.orderRandomlyUtil.getUserIdRandomly();
            // 创建订单
            OrderModel orderModel = new OrderModel();

            Long orderId = this.snowflakeService.getId("order").getId();
            orderModel.setId(orderId);

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
//        List<ProductModel> productModelList = this.productMapper.list(productIdList);
        List<OrderDetailModel> orderDetailModelList = orderModelList.stream().map(o -> {
//            ProductModel productModel = productModelList.get(RandomUtil.randomInt(0, productModelList.size()));
            Long productId = this.orderRandomlyUtil.getProductIdRandomly();
            Long merchantId = this.orderRandomlyUtil.getMerchantIdRandomly();

            Integer amount = 1;
            Long userId = o.getUserId();

            OrderDetailModel orderDetailModel = new OrderDetailModel();

            Long orderDetailId = this.snowflakeService.getId("orderDetail").getId();
            orderDetailModel.setId(orderDetailId);

            orderDetailModel.setOrderId(o.getId());
            orderDetailModel.setUserId(userId);
            orderDetailModel.setProductId(productId);
            orderDetailModel.setMerchantId(merchantId);
            orderDetailModel.setAmount(amount);
            return orderDetailModel;
        }).collect(Collectors.toList());
        this.orderDetailMapper.insertBatch(orderDetailModelList);
    }

    /**
     * 协助测试批量建立 listByUserId 索引的性能
     */
    public void insertBatchOrderIndexListByUserId() throws BusinessException {
        List<OrderIndexListByUserIdModel> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Long userId = this.orderRandomlyUtil.getUserIdRandomly();
            // 创建订单
            OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();

            Long id = this.snowflakeService.getId("orderIndexListByUserId").getId();
            model.setId(id);
            Long orderId = this.snowflakeService.getId("order").getId();
            model.setOrderId(orderId);

            model.setUserId(userId);
            LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
            model.setCreateTime(createTime);

            DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
            model.setDeleteStatus(deleteStatus);

            Status status = OrderRandomlyUtil.getStatusRandomly();
            model.setStatus(status);
            list.add(model);
        }
        this.indexMapper.insertBatchOrderIndexListByUserId(list);
    }

    /**
     * 根据订单ID查询订单
     *
     * @param orderId
     * @return
     */
    public OrderDTO getById(BigDecimal orderId) {
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
        List<OrderModel> orderModelList = this.orderMapper.listByUserId(userId, null, startTime, endTime, 0L, 20L);
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
        List<OrderModel> orderModelList = this.orderMapper.listByUserId(userId, status, startTime, endTime, 0L, 20L);
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

        List<Long> orderIdList = orderList.stream().map(OrderModel::getId).collect(Collectors.toList());

        List<OrderDTO> orderDTOList = null;
        if (!orderList.isEmpty()) {
//            List<OrderDetailModel> orderDetailList = this.orderDetailMapper.list(orderIdList);
//
//            Map<Long, List<OrderDetailModel>> orderDetailGroupByOrderId = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetailModel::getOrderId));

            Map<Long, List<OrderDetailModel>> orderDetailGroupByOrderId = null;

            orderDTOList = orderList.stream().map(o -> {
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(o, orderDTO);

                Long orderId = o.getId();
                if (orderDetailGroupByOrderId != null && orderDetailGroupByOrderId.containsKey(orderId)) {
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
    public void restoreProductStock() throws BusinessException {
//        this.productMapper.restoreStock(productStock);
    }
}
