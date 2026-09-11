package com.future.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 对应 demot.bpkc_mxb_merge_status。
 * Unique Key: company_id, key, type, djlx, dj_id, jl_id, mt_id, mx_id
 */
@Data
@TableName("bpkc_mxb_merge_status")
public class BpkcMxbMergeStatus {

    private Long company_id;
    /** 对应 bpkc_mxb.key */
    @TableField("`key`")
    private String key;
    /** 合并类型：kc（库存）、dck（待出库）、jg_zy（加工占用）等 */
    @TableField("`type`")
    private String type;
    /** 单据类型 */
    private String djlx;
    private Long dj_id;
    private Long jl_id;
    private Long mt_id;
    private Long mx_id;
    /** 记录类型：jl / mt / mx / rk / ck */
    private String jllx;
    private String dh;
    private Integer is_sh;
    private Integer is_zf;
    private Integer is_jd;
    private Integer is_delete;
    private Integer is_qx;
}
