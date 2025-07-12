package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.ProductModel;
import com.future.demo.entity.Status;
import com.future.demo.service.CacheService;
import com.future.demo.service.MerchantService;
import com.future.demo.service.OrderService;
import com.future.demo.service.ProductService;
import com.future.demo.util.OrderRandomlyUtil;
import com.future.random.id.picker.RandomIdPickerService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    @Resource
    CacheService cacheService;
    @Resource
    RandomIdPickerService randomIdPickerService;

    /**
     * 普通方式下单
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "create")
    public ObjectResponse<String> create() throws Exception {
        long userId = this.orderRandomlyUtil.getUserIdRandomly();
        long productId = cacheService.getOrdinaryProductIdRandomly();
        int amount = 1;
        this.orderService.create(userId, productId, amount);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功下单");
        return response;
    }

    /**
     * 秒杀方式下单
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "createFlashSale")
    public ObjectResponse<String> createFlashSale() throws Exception {
        long userId = this.orderRandomlyUtil.getUserIdRandomly();
        long productId = cacheService.getFlashSaleProductIdRandomly();
        int amount = 1;
        this.orderService.createFlashSale(userId, productId, amount);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功秒杀");
        return response;
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByUserIdAndStatus")
    public ListResponse<OrderDTO> listByUserIdAndStatus() throws Exception {
        Long userId = this.orderRandomlyUtil.getUserIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(
                this.orderService.listByUserIdAndStatus(
                        userId, status, createTime, endTime));
    }

    /**
     * 用户查询指定日期范围+所有状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByUserIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByUserIdAndWithoutStatus() throws Exception {
        Long userId = this.orderRandomlyUtil.getUserIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        return ResponseUtils.successList(
                this.orderService.listByUserIdAndWithoutStatus(
                        userId, createTime, endTime));
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndStatus() throws Exception {
        Long merchantId = merchantService.getIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(
                this.orderService.listByMerchantIdAndStatus(
                        merchantId, status, createTime, endTime));
    }

    /**
     * 商家查询指定日期范围+所有状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndWithoutStatus() throws Exception {
        Long merchantId = merchantService.getIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        return ResponseUtils.successList(
                this.orderService.listByMerchantIdAndWithoutStatus(
                        merchantId, createTime, endTime));
    }

    /**
     * 根据商品id列表查询商品列表信息
     *
     * @return
     * @throws BusinessException
     */
    @GetMapping("listProductByIds")
    public ListResponse<ProductModel> listProductByIds() throws BusinessException {
        List<Long> idList = randomIdPickerService.listIdRandomly("product", 20);
        return ResponseUtils.successList(this.productService.listByIds(idList));
    }

    /**
     * 新增普通商品
     *
     * @return
     * @throws Exception
     */
    @GetMapping("addOrdinaryProduct")
    public ObjectResponse<String> addOrdinaryProduct() throws Exception {
        String name = RandomStringUtils.randomAlphanumeric(20);
        Long merchantId = this.merchantService.getIdRandomly();
        this.productService.add(name, merchantId, 300, false, null, null);
        return ResponseUtils.successObject("成功新增普通商品");
    }

    /**
     * 新增秒杀商品
     *
     * @return
     * @throws Exception
     */
    @GetMapping("addFlashSaleProduct")
    public ObjectResponse<String> addFlashSaleProduct() throws Exception {
        String name = RandomStringUtils.randomAlphanumeric(20);
        Long merchantId = this.merchantService.getIdRandomly();
        LocalDateTime flashSaleStartTime = this.productService.getFlashSaleStartTimeRandomly(0);
        LocalDateTime flashSaleEndTime = this.productService.getFlashSaleEndTimeRandomly(flashSaleStartTime);
        this.productService.add(name, merchantId, 300, true, flashSaleStartTime, flashSaleEndTime);
        return ResponseUtils.successObject("成功新增秒杀商品");
    }
}
