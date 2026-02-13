package com.future.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cloth_goods", indexes = {
        // 按 companyId+goodsId 查询或排序
        @Index(name = "idx_company_goods", columnList = "companyId,goodsId"),
        // 按 companyId+type 过滤并 ORDER BY goodsId DESC，覆盖 queryByCompanyIdAndType；左前缀可覆盖仅 companyId
        @Index(name = "idx_company_type_goods", columnList = "companyId,type,goodsId"),
        // 按 companyId+name 过滤并 ORDER BY goodsId DESC，覆盖 term/prefix/wildcard 及排序
        @Index(name = "idx_company_name_goods", columnList = "companyId,name,goodsId"),
        // 按 companyId+number 过滤并 ORDER BY goodsId DESC，覆盖 term/prefix/wildcard 及排序
        @Index(name = "idx_company_number_goods", columnList = "companyId,number,goodsId")
})
@Data
public class ClothGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long goodsId;

    // 企业Id
    @Column(nullable = false)
    private Long companyId;

    // cp(成品),pb(坯布)
    @Column(nullable = false, length = 10)
    private String type;

    // 产品名称
    @Column(nullable = false, length = 100)
    private String name;

    // 编号非空不超50位
    @Column(nullable = false, length = 50)
    private String number;
}
