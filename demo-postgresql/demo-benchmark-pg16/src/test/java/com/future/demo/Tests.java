package com.future.demo;

import com.future.demo.entity.ClothGoods;
import com.future.demo.repository.ClothGoodsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(
        classes = {DemoSpringBootApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class Tests {

    @Resource
    ClothGoodsRepository clothGoodsRepository;

    @Test
    public void testClothGoods() {
        // 删除之前的数据
        clothGoodsRepository.deleteAll();

        /*------------------------------- 新增产品文档 */
        ClothGoods clothGoods1 = new ClothGoods();
        clothGoods1.setGoodsId(1L);
        clothGoods1.setCompanyId(100L);
        clothGoods1.setType("cp");
        clothGoods1.setName("纯棉T恤");
        clothGoods1.setNumber("CP001");
        ClothGoods saved1 = clothGoodsRepository.save(clothGoods1);
        Assertions.assertNotNull(saved1.getId());

        // 根据goodsId验证文档是否新增成功
        ClothGoods found1 = clothGoodsRepository.findByGoodsId(1L);
        Assertions.assertNotNull(found1);
        Assertions.assertEquals(1L, found1.getGoodsId());

        /*------------------------------- 批量新增产品文档 */
        ClothGoods clothGoods2 = new ClothGoods();
        clothGoods2.setGoodsId(2L);
        clothGoods2.setCompanyId(100L);
        clothGoods2.setType("cp");
        clothGoods2.setName("纯棉衬衫");
        clothGoods2.setNumber("CP002");

        ClothGoods clothGoods3 = new ClothGoods();
        clothGoods3.setGoodsId(3L);
        clothGoods3.setCompanyId(200L);
        clothGoods3.setType("pb");
        clothGoods3.setName("坯布A");
        clothGoods3.setNumber("PB001");

        ClothGoods clothGoods4 = new ClothGoods();
        clothGoods4.setGoodsId(4L);
        clothGoods4.setCompanyId(200L);
        clothGoods4.setType("cp");
        clothGoods4.setName("纯棉短裤");
        clothGoods4.setNumber("CP003");

        clothGoodsRepository.saveAll(Arrays.asList(clothGoods2, clothGoods3, clothGoods4));

        List<ClothGoods> clothGoodsList = clothGoodsRepository.findAll();
        Assertions.assertEquals(4, clothGoodsList.size());

        /*------------------------------- 根据goodsId字段查询 */
        ClothGoods found = clothGoodsRepository.findByGoodsId(1L);
        Assertions.assertNotNull(found);
        Assertions.assertEquals(1L, found.getGoodsId());
        Assertions.assertEquals("CP001", found.getNumber());

        // 根据多个id查询
        List<ClothGoods> foundList = clothGoodsRepository.findAllById(Arrays.asList(
                clothGoodsRepository.findByGoodsId(1L).getId(),
                clothGoodsRepository.findByGoodsId(2L).getId(),
                clothGoodsRepository.findByGoodsId(3L).getId()
        ));
        Assertions.assertEquals(3, foundList.size());

        /*------------------------------- 根据companyId字段查询 */
        // 精确查询companyId=100
        List<ClothGoods> company100List = clothGoodsRepository.findByCompanyId(100L);
        Assertions.assertEquals(2, company100List.size());
        Assertions.assertTrue(company100List.stream().allMatch(item -> item.getCompanyId().equals(100L)));

        // 范围查询companyId >= 200
        List<ClothGoods> company200PlusList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> item.getCompanyId() >= 200L)
                .collect(Collectors.toList());
        Assertions.assertEquals(2, company200PlusList.size());
        Assertions.assertTrue(company200PlusList.stream().allMatch(item -> item.getCompanyId() >= 200L));

        // 范围查询companyId在100到200之间（包含边界）
        List<ClothGoods> companyRangeList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> item.getCompanyId() >= 100L && item.getCompanyId() <= 200L)
                .collect(Collectors.toList());
        Assertions.assertEquals(4, companyRangeList.size());

        /*------------------------------- 根据type字段查询 */
        // 查询type='cp'的产品
        List<ClothGoods> cpList = clothGoodsRepository.findByType("cp");
        Assertions.assertEquals(3, cpList.size());
        Assertions.assertTrue(cpList.stream().allMatch(item -> "cp".equals(item.getType())));

        // 查询type='pb'的产品
        List<ClothGoods> pbList = clothGoodsRepository.findByType("pb");
        Assertions.assertEquals(1, pbList.size());
        Assertions.assertEquals("pb", pbList.get(0).getType());
        Assertions.assertEquals("PB001", pbList.get(0).getNumber());

        // 查询多个type值
        List<ClothGoods> multiTypeList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> "cp".equals(item.getType()) || "pb".equals(item.getType()))
                .collect(Collectors.toList());
        Assertions.assertEquals(4, multiTypeList.size());

        /*------------------------------- 根据name字段查询 */
        // 精确查询name='纯棉T恤'
        List<ClothGoods> nameList1 = clothGoodsRepository.findByName("纯棉T恤");
        Assertions.assertEquals(1, nameList1.size());
        Assertions.assertEquals("纯棉T恤", nameList1.get(0).getName());

        // 查询name='纯棉衬衫'
        List<ClothGoods> nameList2 = clothGoodsRepository.findByName("纯棉衬衫");
        Assertions.assertEquals(1, nameList2.size());
        Assertions.assertEquals("纯棉衬衫", nameList2.get(0).getName());

        // 查询name='坯布A'
        List<ClothGoods> nameList3 = clothGoodsRepository.findByName("坯布A");
        Assertions.assertEquals(1, nameList3.size());
        Assertions.assertEquals("坯布A", nameList3.get(0).getName());

        // 查询多个name值
        List<ClothGoods> multiNameList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> "纯棉T恤".equals(item.getName()) || "纯棉衬衫".equals(item.getName()))
                .collect(Collectors.toList());
        Assertions.assertEquals(2, multiNameList.size());

        // 查询name包含"棉"的产品（使用LIKE查询）
        List<ClothGoods> nameLikeList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> item.getName().contains("棉"))
                .collect(Collectors.toList());
        Assertions.assertEquals(3, nameLikeList.size());
        Assertions.assertTrue(nameLikeList.stream().allMatch(item -> item.getName().contains("棉")));

        /*------------------------------- 根据number字段查询 */
        // 精确查询number='CP001'
        List<ClothGoods> numberList1 = clothGoodsRepository.findByNumber("CP001");
        Assertions.assertEquals(1, numberList1.size());
        Assertions.assertEquals("CP001", numberList1.get(0).getNumber());
        Assertions.assertEquals(1L, numberList1.get(0).getGoodsId());

        // 查询number='CP002'
        List<ClothGoods> numberList2 = clothGoodsRepository.findByNumber("CP002");
        Assertions.assertEquals(1, numberList2.size());
        Assertions.assertEquals("CP002", numberList2.get(0).getNumber());

        // 查询number='PB001'
        List<ClothGoods> numberList3 = clothGoodsRepository.findByNumber("PB001");
        Assertions.assertEquals(1, numberList3.size());
        Assertions.assertEquals("PB001", numberList3.get(0).getNumber());

        // 查询number以'CP'开头的产品（使用LIKE查询）
        List<ClothGoods> numberPrefixList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> item.getNumber().startsWith("CP"))
                .collect(Collectors.toList());
        Assertions.assertEquals(3, numberPrefixList.size());
        Assertions.assertTrue(numberPrefixList.stream().allMatch(item -> item.getNumber().startsWith("CP")));

        // 查询多个number值
        List<ClothGoods> multiNumberList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> "CP001".equals(item.getNumber()) || "PB001".equals(item.getNumber()))
                .collect(Collectors.toList());
        Assertions.assertEquals(2, multiNumberList.size());

        // 查询number包含"P00"的产品（使用LIKE查询）
        List<ClothGoods> numberLikeList = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> item.getNumber().contains("P00"))
                .collect(Collectors.toList());
        Assertions.assertEquals(3, numberLikeList.size());
        Assertions.assertTrue(numberLikeList.stream().allMatch(item -> item.getNumber().contains("P00")));

        /*------------------------------- 多字段组合查询 */
        // 查询companyId=100且type='cp'的产品
        List<ClothGoods> combinedList1 = clothGoodsRepository.findByCompanyIdAndType(100L, "cp");
        Assertions.assertEquals(2, combinedList1.size());
        Assertions.assertTrue(combinedList1.stream().allMatch(item -> item.getCompanyId().equals(100L) && "cp".equals(item.getType())));

        // 查询companyId=200或type='pb'的产品
        List<ClothGoods> combinedList2 = clothGoodsRepository.findAll()
                .stream()
                .filter(item -> item.getCompanyId().equals(200L) || "pb".equals(item.getType()))
                .collect(Collectors.toList());
        Assertions.assertEquals(2, combinedList2.size());

        /*------------------------------- 分页和排序查询 */
        List<ClothGoods> page1 = clothGoodsRepository.findAll(
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "goodsId"))
        ).getContent();
        Assertions.assertEquals(2, page1.size());
        Assertions.assertEquals(1L, page1.get(0).getGoodsId());
        Assertions.assertEquals(2L, page1.get(1).getGoodsId());

        // 查询第二页
        List<ClothGoods> page2 = clothGoodsRepository.findAll(
                PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "goodsId"))
        ).getContent();
        Assertions.assertEquals(2, page2.size());
        Assertions.assertEquals(3L, page2.get(0).getGoodsId());
        Assertions.assertEquals(4L, page2.get(1).getGoodsId());

        // 按companyId降序排序
        List<ClothGoods> sortedList = clothGoodsRepository.findAll(
                Sort.by(Sort.Direction.DESC, "companyId")
        );
        Assertions.assertEquals(4, sortedList.size());
        Assertions.assertTrue(sortedList.get(0).getCompanyId() >= sortedList.get(1).getCompanyId());

        /*------------------------------- 修改产品文档 */
        ClothGoods clothGoods1Update = clothGoodsRepository.findByGoodsId(1L);
        clothGoods1Update.setName("纯棉T恤升级版");
        clothGoods1Update.setNumber("CP001");
        ClothGoods updated1 = clothGoodsRepository.save(clothGoods1Update);
        Assertions.assertNotNull(updated1);

        ClothGoods foundUpdated = clothGoodsRepository.findByGoodsId(1L);
        Assertions.assertEquals("纯棉T恤升级版", foundUpdated.getName());

        /*------------------------------- 根据id删除产品文档 */
        ClothGoods toDelete = clothGoodsRepository.findByGoodsId(4L);
        clothGoodsRepository.deleteById(toDelete.getId());

        List<ClothGoods> afterDelete1 = clothGoodsRepository.findAll();
        Assertions.assertEquals(3, afterDelete1.size());

        /*------------------------------- 根据多个id批量删除产品文档 */
        ClothGoods toDelete2 = clothGoodsRepository.findByGoodsId(2L);
        ClothGoods toDelete3 = clothGoodsRepository.findByGoodsId(3L);
        clothGoodsRepository.deleteAllById(Arrays.asList(toDelete2.getId(), toDelete3.getId()));

        List<ClothGoods> afterDelete2 = clothGoodsRepository.findAll();
        Assertions.assertEquals(1, afterDelete2.size());
        Assertions.assertEquals(1L, afterDelete2.get(0).getGoodsId());

        /*------------------------------- 根据自定义query删除产品数据 */
        ClothGoods toDelete4 = clothGoodsRepository.findByGoodsId(1L);
        clothGoodsRepository.deleteById(toDelete4.getId());

        List<ClothGoods> afterDelete3 = clothGoodsRepository.findAll();
        Assertions.assertEquals(0, afterDelete3.size());
    }

}
