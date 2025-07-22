package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.dto.OrderDTO;
import com.future.demo.dto.ProductDTO;
import com.future.demo.entity.Status;
import com.future.demo.service.*;
import com.future.demo.util.OrderRandomlyUtil;
import com.future.random.id.picker.RandomIdPickerService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class ApiController {
    @Resource
    OrderService orderService;
    @Resource
    ProductService productService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    OrderRandomlyUtil orderRandomlyUtil;
    @Resource
    MerchantService merchantService;
//    @Resource
//    CacheService cacheService;
    @Resource
    PickupProductRandomlyWhenPurchasingService pickupProductRandomlyWhenPurchasingService;
    @Resource
    RandomIdPickerService randomIdPickerService;

    /**
     * 普通方式下单
     *
     * @param randomCreateTime 订单创建时间是否随机生成呢？
     * @return
     * @throws Exception
     */
    @GetMapping(value = "create")
    public ObjectResponse<String> create(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "randomCreateTime", required = false, defaultValue = "true") boolean randomCreateTime
    ) throws Exception {
        if (userId == null)
            userId = this.orderRandomlyUtil.getUserIdRandomly();
        if (productId == null)
            productId = pickupProductRandomlyWhenPurchasingService.getOrdinaryProductIdRandomly();
        int amount = 1;
        LocalDateTime createTime = null;
        if (!randomCreateTime)
            createTime = LocalDateTime.now();
        this.orderService.create(userId, productId, amount, createTime);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功下单");
        return response;
    }

    /**
     * 秒杀方式下单
     *
     * @param randomCreateTime 订单创建时间是否随机生成呢？
     * @return
     * @throws Exception
     */
    @GetMapping(value = "createFlashSale")
    public ObjectResponse<String> createFlashSale(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "randomCreateTime", required = false, defaultValue = "true") boolean randomCreateTime
    ) throws Exception {
        if (userId == null)
            userId = this.orderRandomlyUtil.getUserIdRandomly();
        if (productId == null)
            productId = pickupProductRandomlyWhenPurchasingService.getFlashSaleProductIdRandomly();
        int amount = 1;
        LocalDateTime createTime = null;
        if (!randomCreateTime)
            createTime = LocalDateTime.now();
        this.orderService.createFlashSale(userId, productId, amount, createTime);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功秒杀");
        return response;
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @param latestMonth 是否查询从当前时间开始最近一个月内的订单
     * @return
     */
    @GetMapping(value = "listByUserIdAndStatus")
    public ListResponse<OrderDTO> listByUserIdAndStatus(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "latestMonth", required = false, defaultValue = "false") boolean latestMonth,
            @RequestParam(value = "status", required = false) Status status
    ) throws Exception {
        if (userId == null || userId <= 0)
            userId = this.orderRandomlyUtil.getUserIdRandomly();
        LocalDateTime endTime;
        if (latestMonth)
            endTime = LocalDateTime.now();
        else endTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime startTime = endTime.minusMonths(1);
        if (status == null)
            status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(
                this.orderService.listByUserIdAndStatus(
                        userId, status, startTime, endTime));
    }

    /**
     * 用户查询指定日期范围+所有状态的订单
     *
     * @param latestMonth 是否查询从当前时间开始最近一个月内的订单
     * @return
     */
    @GetMapping(value = "listByUserIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByUserIdAndWithoutStatus(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "latestMonth", required = false, defaultValue = "false") boolean latestMonth
    ) throws Exception {
        if (userId == null || userId <= 0)
            userId = this.orderRandomlyUtil.getUserIdRandomly();
        LocalDateTime endTime;
        if (latestMonth)
            endTime = LocalDateTime.now();
        else endTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime startTime = endTime.minusMonths(1);
        return ResponseUtils.successList(
                this.orderService.listByUserIdAndWithoutStatus(
                        userId, startTime, endTime));
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @param latestMonth 是否查询从当前时间开始最近一个月内的订单
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndStatus(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "latestMonth", required = false, defaultValue = "false") boolean latestMonth,
            @RequestParam(value = "status", required = false) Status status
    ) throws Exception {
        if (merchantId == null || merchantId <= 0)
            merchantId = merchantService.getIdRandomly();
        LocalDateTime endTime;
        if (latestMonth)
            endTime = LocalDateTime.now();
        else endTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime startTime = endTime.minusMonths(1);
        if (status == null)
            status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(
                this.orderService.listByMerchantIdAndStatus(
                        merchantId, status, startTime, endTime));
    }

    /**
     * 商家查询指定日期范围+所有状态的订单
     *
     * @param latestMonth 是否查询从当前时间开始最近一个月内的订单
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndWithoutStatus(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "latestMonth", required = false, defaultValue = "false") boolean latestMonth
    ) throws Exception {
        if (merchantId == null || merchantId <= 0)
            merchantId = merchantService.getIdRandomly();
        LocalDateTime endTime;
        if (latestMonth)
            endTime = LocalDateTime.now();
        else endTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime startTime = endTime.minusMonths(1);
        return ResponseUtils.successList(
                this.orderService.listByMerchantIdAndWithoutStatus(
                        merchantId, startTime, endTime));
    }

    /**
     * 根据商品id列表查询商品列表信息
     *
     * @return
     * @throws BusinessException
     */
    @GetMapping("listProductByIds")
    public ListResponse<ProductDTO> listProductByIds() throws Exception {
        List<Long> idList = randomIdPickerService.listIdRandomly("product", 20);
        return ResponseUtils.successList(this.productService.listByIds(idList));
    }

    /**
     * 更加商品id查询商品信息
     *
     * @param id
     * @return
     * @throws BusinessException
     */
    @GetMapping("getProductById")
    public ObjectResponse<ProductDTO> getProductById(
            @RequestParam(value = "id", required = false) Long id
    ) throws BusinessException {
        if (id == null) {
            List<Long> idList = randomIdPickerService.listIdRandomly("product", 1);
            if (idList != null && idList.size() == 1) {
                id = idList.get(0);
            }
        }
        return ResponseUtils.successObject(this.productService.getById(id));
    }

    /**
     * 新增普通商品
     *
     * @return
     * @throws Exception
     */
    @GetMapping("addOrdinaryProduct")
    public ObjectResponse<String> addOrdinaryProduct(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "stockAmount", required = false) Integer stockAmount
    ) throws Exception {
        if (StringUtils.isBlank(name))
            name = RandomStringUtils.randomAlphanumeric(20);
        if (merchantId == null || merchantId <= 0)
            merchantId = this.merchantService.getIdRandomly();
        if (stockAmount == null || stockAmount < 0)
            stockAmount = 300;
        Long productId = this.productService.add(name, merchantId, stockAmount, false, null, null);
        return ResponseUtils.successObject(String.valueOf(productId));
    }

    /**
     * 新增秒杀商品
     *
     * @return
     * @throws Exception
     */
    @GetMapping("addFlashSaleProduct")
    public ObjectResponse<String> addFlashSaleProduct(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "stockAmount", required = false) Integer stockAmount,
            @RequestParam(value = "flashSaleStartTime", required = false) String flashSaleStartTimeStr,
            @RequestParam(value = "flashSaleEndTime", required = false) String flashSaleEndTimeStr
    ) throws Exception {
        if (StringUtils.isBlank(name))
            name = RandomStringUtils.randomAlphanumeric(20);
        if (merchantId == null || merchantId <= 0)
            merchantId = this.merchantService.getIdRandomly();
        if (stockAmount == null || stockAmount < 0)
            stockAmount = 300;

        LocalDateTime flashSaleStartTime = null;
        if (!StringUtils.isBlank(flashSaleStartTimeStr)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            flashSaleStartTime = LocalDateTime.parse(flashSaleStartTimeStr, dateTimeFormatter);
        }
        if (flashSaleStartTime == null)
            flashSaleStartTime = this.productService.getFlashSaleStartTimeRandomly(0);
        LocalDateTime flashSaleEndTime = null;
        if (!StringUtils.isBlank(flashSaleEndTimeStr)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            flashSaleEndTime = LocalDateTime.parse(flashSaleEndTimeStr, dateTimeFormatter);
        }
        if (flashSaleEndTime == null)
            flashSaleEndTime = this.productService.getFlashSaleEndTimeRandomly(flashSaleStartTime);
        Long productId = this.productService.add(name, merchantId, stockAmount, true, flashSaleStartTime, flashSaleEndTime);
        return ResponseUtils.successObject(String.valueOf(productId));
    }
}
