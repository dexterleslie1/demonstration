package com.future.demo.repository;

import com.future.demo.entity.ClothGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothGoodsRepository extends JpaRepository<ClothGoods, Long> {
    
    // 根据goodsId查询
    ClothGoods findByGoodsId(Long goodsId);
    
    // 根据companyId查询
    List<ClothGoods> findByCompanyId(Long companyId);
    
    // 根据type查询
    List<ClothGoods> findByType(String type);
    
    // 根据companyId和type查询
    List<ClothGoods> findByCompanyIdAndType(Long companyId, String type);
    
    // 根据companyId和type查询（分页）
    Page<ClothGoods> findByCompanyIdAndType(Long companyId, String type, Pageable pageable);
    
    // 根据name查询
    List<ClothGoods> findByName(String name);
    
    // 根据number查询
    List<ClothGoods> findByNumber(String number);
    
    // 根据companyId和name（LIKE查询）查询
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.name LIKE :namePattern ORDER BY c.goodsId DESC")
    List<ClothGoods> findByCompanyIdAndNameLike(@Param("companyId") Long companyId, @Param("namePattern") String namePattern);
    
    // 根据companyId和name（LIKE查询）查询（分页）
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.name LIKE :namePattern ORDER BY c.goodsId DESC")
    Page<ClothGoods> findByCompanyIdAndNameLike(@Param("companyId") Long companyId, @Param("namePattern") String namePattern, Pageable pageable);
    
    // 根据companyId和name（前缀查询）查询
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.name LIKE :namePrefix ORDER BY c.goodsId DESC")
    List<ClothGoods> findByCompanyIdAndNameStartingWith(@Param("companyId") Long companyId, @Param("namePrefix") String namePrefix);
    
    // 根据companyId和name（前缀查询）查询（分页）
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.name LIKE :namePrefix ORDER BY c.goodsId DESC")
    Page<ClothGoods> findByCompanyIdAndNameStartingWith(@Param("companyId") Long companyId, @Param("namePrefix") String namePrefix, Pageable pageable);
    
    // 根据companyId和name（精确查询）查询
    List<ClothGoods> findByCompanyIdAndName(Long companyId, String name);
    
    // 根据companyId和name（精确查询）查询（分页）
    Page<ClothGoods> findByCompanyIdAndName(Long companyId, String name, Pageable pageable);
    
    // 根据companyId和number（LIKE查询）查询
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.number LIKE :numberPattern ORDER BY c.goodsId DESC")
    List<ClothGoods> findByCompanyIdAndNumberLike(@Param("companyId") Long companyId, @Param("numberPattern") String numberPattern);
    
    // 根据companyId和number（LIKE查询）查询（分页）
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.number LIKE :numberPattern ORDER BY c.goodsId DESC")
    Page<ClothGoods> findByCompanyIdAndNumberLike(@Param("companyId") Long companyId, @Param("numberPattern") String numberPattern, Pageable pageable);
    
    // 根据companyId和number（前缀查询）查询
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.number LIKE :numberPrefix ORDER BY c.goodsId DESC")
    List<ClothGoods> findByCompanyIdAndNumberStartingWith(@Param("companyId") Long companyId, @Param("numberPrefix") String numberPrefix);
    
    // 根据companyId和number（前缀查询）查询（分页）
    @Query("SELECT c FROM ClothGoods c WHERE c.companyId = :companyId AND c.number LIKE :numberPrefix ORDER BY c.goodsId DESC")
    Page<ClothGoods> findByCompanyIdAndNumberStartingWith(@Param("companyId") Long companyId, @Param("numberPrefix") String numberPrefix, Pageable pageable);
    
    // 根据companyId和number（精确查询）查询
    List<ClothGoods> findByCompanyIdAndNumber(Long companyId, String number);
    
    // 根据companyId和number（精确查询）查询（分页）
    Page<ClothGoods> findByCompanyIdAndNumber(Long companyId, String number, Pageable pageable);
    
    // 获取最大goodsId
    @Query("SELECT MAX(c.goodsId) FROM ClothGoods c")
    Long findMaxGoodsId();
}
