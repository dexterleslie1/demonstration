package com.future.demo.controller;

import com.future.demo.entity.ClothGoods;
import com.future.demo.repository.ClothGoodsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1")
public class ClothGoodsController {

    @Resource
    private ClothGoodsRepository clothGoodsRepository;

    @Value("${benchmark.company-id.min:1}")
    private long companyIdMin;
    @Value("${benchmark.company-id.max:11}")
    private long companyIdMax;

    public static final java.util.concurrent.atomic.AtomicLong idCounter = new java.util.concurrent.atomic.AtomicLong(1);
    private static final Random random = new Random(System.currentTimeMillis());
    private static final String[] TYPES = {"cp", "pb"};

    /** 在配置的 [companyIdMin, companyIdMax] 闭区间内随机取一个 companyId */
    private Long randomCompanyId() {
        int range = (int) (companyIdMax - companyIdMin + 1);
        return companyIdMin + random.nextInt(Math.max(1, range));
    }

    @GetMapping("goods/generate")
    public String generateAndInsert() {
        try {
            // 生成1000个随机产品
            List<ClothGoods> clothGoodsList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                ClothGoods clothGoods = generateRandomClothGoods();
                clothGoodsList.add(clothGoods);
            }

            // 批量插入到PostgreSQL
            clothGoodsRepository.saveAll(clothGoodsList);

            return "成功生成并插入1000个产品到PostgreSQL";
        } catch (Exception e) {
            return "插入失败: " + e.getMessage();
        }
    }

    private ClothGoods generateRandomClothGoods() {
        ClothGoods clothGoods = new ClothGoods();

        // 使用 AtomicLong 计数器递增生成 ID
        long goodsId = idCounter.getAndIncrement();
        clothGoods.setGoodsId(goodsId);

        // 随机企业ID（配置范围）
        clothGoods.setCompanyId(randomCompanyId());

        // 随机类型（cp或pb）
        clothGoods.setType(TYPES[random.nextInt(TYPES.length)]);

        // 生成编号：根据类型生成CP或PB开头的编号
        String prefix = clothGoods.getType().equals("cp") ? "CP" : "PB";
        String suffix = String.format("%09d", goodsId);
        String number = String.format("%s%s", prefix, suffix);
        clothGoods.setNumber(number);

        // 产品名称：产品000000001、产品000000002格式
        clothGoods.setName(String.format("%s%s", "产品", suffix));

        return clothGoods;
    }

    /**
     * 查看当前counter
     *
     * @return
     */
    @GetMapping("goods/counter")
    public String getCounter() {
        return "当前counter为" + idCounter.get();
    }

    /**
     * 性能测试接口：根据companyId和type查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，type随机抽取
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndType")
    public List<ClothGoods> queryByCompanyIdAndType() {
        Long companyId = randomCompanyId();
        
        // 随机抽取类型（cp或pb）
        String type = TYPES[random.nextInt(TYPES.length)];
        
        // 构建分页和排序
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "goodsId"));
        
        // 执行查询
        return clothGoodsRepository.findByCompanyIdAndType(companyId, type, pageable).getContent();
    }

    /**
     * 性能测试接口：根据companyId和name（wildcard查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，name使用wildcard查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNameWildcard")
    public List<ClothGoods> queryByCompanyIdAndNameWildcard() {
        Long companyId = randomCompanyId();

        // "*" + 随机9位数字 + "*" - 匹配包含特定数字的
        int randomNum = random.nextInt((int)idCounter.get());
        String namePattern = "%" + String.format("%09d", randomNum) + "%";
        
        // 构建分页和排序
        Pageable pageable = PageRequest.of(0, 100);
        
        // 执行查询（PostgreSQL使用%代替*）
        return clothGoodsRepository.findByCompanyIdAndNameLike(companyId, namePattern, pageable).getContent();
    }

    /**
     * 性能测试接口：根据companyId和name（prefix查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，name使用prefix查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNamePrefix")
    public List<ClothGoods> queryByCompanyIdAndNamePrefix() {
        Long companyId = randomCompanyId();

        // "产品" + 随机9位数字 - 匹配以"产品"开头且包含更长数字前缀的
        int randomNum = random.nextInt((int)idCounter.get());
        String namePrefix = "产品" + String.format("%09d", randomNum) + "%";

        // 构建分页和排序
        Pageable pageable = PageRequest.of(0, 100);
        
        // 执行查询
        return clothGoodsRepository.findByCompanyIdAndNameStartingWith(companyId, namePrefix, pageable).getContent();
    }

    /**
     * 性能测试接口：根据companyId和name（term查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，name使用term查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNameTerm")
    public List<ClothGoods> queryByCompanyIdAndNameTerm() {
        Long companyId = randomCompanyId();

        // "产品" + 随机9位数字 - 精确匹配
        int randomNum = random.nextInt((int)idCounter.get());
        String nameTerm = "产品" + String.format("%09d", randomNum);

        // 构建分页和排序
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "goodsId"));
        
        // 执行查询
        return clothGoodsRepository.findByCompanyIdAndName(companyId, nameTerm, pageable).getContent();
    }

    /**
     * 性能测试接口：根据companyId和number（wildcard查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，number使用wildcard查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNumberWildcard")
    public List<ClothGoods> queryByCompanyIdAndNumberWildcard() {
        Long companyId = randomCompanyId();

        // "*" + 随机9位数字 + "*" - 匹配包含特定数字的
        int randomNum = random.nextInt((int)idCounter.get());
        String numberPattern = "%" + String.format("%09d", randomNum) + "%";
        
        // 构建分页和排序
        Pageable pageable = PageRequest.of(0, 100);
        
        // 执行查询（PostgreSQL使用%代替*）
        return clothGoodsRepository.findByCompanyIdAndNumberLike(companyId, numberPattern, pageable).getContent();
    }

    /**
     * 性能测试接口：根据companyId和number（prefix查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，number使用prefix查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNumberPrefix")
    public List<ClothGoods> queryByCompanyIdAndNumberPrefix() {
        Long companyId = randomCompanyId();

        // 随机选择CP或PB前缀，然后加上随机数字
        String prefix = TYPES[random.nextInt(TYPES.length)].equals("cp") ? "CP" : "PB";
        int randomNum = random.nextInt((int)idCounter.get());
        String numberPrefix = prefix + String.format("%09d", randomNum) + "%";

        // 构建分页和排序
        Pageable pageable = PageRequest.of(0, 100);
        
        // 执行查询
        return clothGoodsRepository.findByCompanyIdAndNumberStartingWith(companyId, numberPrefix, pageable).getContent();
    }

    /**
     * 性能测试接口：根据companyId和number（term查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，number使用term查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNumberTerm")
    public List<ClothGoods> queryByCompanyIdAndNumberTerm() {
        Long companyId = randomCompanyId();

        // 随机选择CP或PB前缀，然后加上随机数字
        String prefix = TYPES[random.nextInt(TYPES.length)].equals("cp") ? "CP" : "PB";
        int randomNum = random.nextInt((int)idCounter.get());
        String numberTerm = prefix + String.format("%09d", randomNum);

        // 构建分页和排序
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "goodsId"));
        
        // 执行查询
        return clothGoodsRepository.findByCompanyIdAndNumber(companyId, numberTerm, pageable).getContent();
    }
}
