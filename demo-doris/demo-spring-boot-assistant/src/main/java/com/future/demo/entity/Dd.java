package com.future.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 对应 demot.dd，字段与 doris-init.sql / finance DdRow 对齐。
 * Unique Key: company_id, dj_type, dj_type_sub, dj_id, jl_id, mt_id, mx_id
 */
@Data
@TableName("dd")
public class Dd {

    private Long company_id;
    private String dj_type;
    private String dj_type_sub;
    private Long dj_id;
    private Long jl_id;
    private Long mt_id;
    private Long mx_id;
    private Long dd_dj_id;
    private Long dd_jl_id;
    private Long ck_id;
    private Long cw_id;
    private Long cp_id;
    private Long cp_ys_id;
    private String fk;
    private String kz;
    private Long dw_id;
    private String jh;
    private String gh;
    private String gg;
    private String tm;
    private String ph;
    private String ck_zdy1;
    private String ck_zdy2;
    private String ck_zdy3;
    private String ck_zdy4;
    private String dyeing_advice;
    private String ck_zdy5;
    private BigDecimal zsl2;
    private Long dw2_id;
    private Long ywy_id;
    private Long wldw_id;
    private Long bp_gys_id;
    private Long bp_jgs_id;
    private Long bp_kh_id;
    private String yy_djlx;
    private Integer has_count;
    private Integer is_jd;
    private Integer is_sh;
    private Integer is_delete;
    private Integer is_zf;
    private Integer is_qx;
    private Integer djlb_fh;
    private Integer is_fh;
    private String kdsj;
    private String sh_sj;
    private String zf_sj;
    private String dh;
    private String dd_dh;
    private Long zdr_id;
    private Long jsr_id;
    private Long shr_id;
    private Long zfr_id;
    private String jl_bz;
    private String dj_bz;
    private String brand;
    private String style_number;
    private String quarter;
    private Long ywgd_staff_id;
    private Integer dj_lsh;
    private BigDecimal ps;
    private BigDecimal sl;
}
