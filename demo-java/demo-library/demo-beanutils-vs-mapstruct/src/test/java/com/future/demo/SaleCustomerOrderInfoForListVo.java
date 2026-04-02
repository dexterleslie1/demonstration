package com.future.demo;
import com.alibaba.fastjson2.JSONArray;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;
public class SaleCustomerOrderInfoForListVo {

    @Schema(description = "单据类型")
    @JsonProperty("djlx")
    private String djlx;


    @Schema(description = "单据类型")
    @JsonProperty("djlx_mc")
    private String djlxMc;

    @JsonProperty("djlx_qm")
    private String djlxQm;

    @Schema(description = "单据ID")
    @JsonProperty("dj_id")
    private Long djId;

    @Schema(description = "记录ID")
    @JsonProperty("jl_id")
    private Long jlId;

    @Schema(description = "联系人")
    @JsonProperty("lxr")
    private String lxr;


    @Schema(description = "电话")
    @JsonProperty("lxr_dh")
    private String lxrDh;

    @Schema(description = "地址")
    @JsonProperty("lxr_qbdz")
    private String lxrQbdz;

    @Schema(description = "联系人地址")
    @JsonProperty("address")
    private String address;

    @Schema(description = "联系人省份")
    @JsonProperty("lxr_province")
    private String lxrProvince;

    @Schema(description = "联系人城市")
    @JsonProperty("lxr_city")
    private String lxrCity;

    @Schema(description = "联系人镇区")
    @JsonProperty("lxr_town")
    private String lxrTown;

    @Schema(description = "联系人镇区")
    @JsonProperty("lxr_country")
    private Integer lxrCountry;

    @JsonProperty("lxr_dz")
    private String lxrDz;

    @Schema(description = "草稿")
    @JsonProperty("is_draft")
    private Integer isDraft;

    @Schema(description = "希音企业")
    @JsonProperty("shein_open_key")
    private String sheinOpenKey;

    @Schema(description = "分厂名称")
    @JsonProperty("shein_branch_factory")
    private String sheinBranchFactory;

    @Schema(description = "获取单号方式")
    @JsonProperty("wldhhqfs")
    private String wldhhqfs;

    @Schema(description = "转发Vcans")
    @JsonProperty("is_sync_vcans")
    private Integer isSyncVcans;

    @Schema(description = "云支付")
    @JsonProperty("is_online_pay")
    private Integer isOnlinePay;

    @Schema(description = "云支付交易单号")
    @JsonProperty("out_transaction_id")
    private Long outTransactionId;


    @Schema(description = "日期")
    @JsonProperty("kdsj")
    private String kdsj;

    @Schema(description = "单号")
    @JsonProperty("dh")
    private String dh;

    @Schema(description = "辅助单号")
    @JsonProperty("fz_dh")
    private String fzDh;

    @Schema(description = "NO.")
    @JsonProperty("dj_lsh")
    private String djLsh;

    @Schema(description = "是否审核")
    @JsonProperty("is_sh")
    private Integer isSh;

    @Schema(description = "单据备注")
    @JsonProperty("dj_bz")
    private String djBz;

    @Schema(description = "单据实售总金额")
    @JsonProperty("dj_sszje")
    private BigDecimal djSszje;

    @Schema(description = "单据总预回扣")
    @JsonProperty("dj_zyhk")
    private Integer djZyhk;

    @Schema(description = "取消订单")
    @JsonProperty("is_qx")
    private Integer isQx;

    @Schema(description = "取消人ID")
    @JsonProperty("qxr_id")
    private Long qxrId;

    @Schema(description = "取消日期")
    @JsonProperty("qx_sj")
    private String qxSj;

    @JsonProperty("qx_yy_id")
    private Long qxYyId;

    @JsonProperty("qx_yy_ms")
    private String qxYyMs;

    @JsonProperty("zdw_id")
    private Long zdwId;

    @JsonProperty("zjjdw_id")
    private Long zjjdwId;

    @JsonProperty("cjq_jdr_id")
    private Long cjqJdrId;

    @Schema(description = "接单时间")
    @JsonProperty("cjq_jd_sj")
    private String cjqJdSj;

    @Schema(description = "指派模式")
    @JsonProperty("zpms")
    private String zpms;

    @Schema(description = "协同接单采集")
    @JsonProperty("cjq_xt_list")
    private String cjqXtList;

    @Schema(description = "已打印")
    @JsonProperty("is_ydy")
    private Integer isYdy;

    @Schema(description = "打印/次")
    @JsonProperty("dy_cs")
    private Integer dyCs;

    @JsonProperty("cg_gys_id")
    private Long cgGysId;

    @JsonProperty("gys_mc")
    private String gysMc;

    @Schema(description = "往来单位ID")
    @JsonProperty("wldw_id")
    private Long wldwId;

    @Schema(description = "客户")
    @JsonProperty("wldw_mc")
    private String wldwMc;

    @Schema(description = "客户编号/供应商编号")
    @JsonProperty("wldw_bh")
    private String wldwBh;

    @Schema(description = "往来单位类别ID")
    @JsonProperty("wldw_lb_id")
    private Long wldwLbId;

    @JsonProperty("y_zc_id")
    private Long yZcId;

    @Schema(description = "预/织厂")
    @JsonProperty("y_zc_mc")
    private String yZcMc;

    @Schema(description = "订金")
    @JsonProperty("zh_je")
    private BigDecimal zhJe;

    @Schema(description = "收款单号")
    @JsonProperty("yzf_sk_dh")
    private String yzfSkDh;

    @JsonProperty("yzf_sk_dh_djlx")
    private String yzfSkDhDjlx;

    @Schema(description = "订金管理单号")
    @JsonProperty("djgl_dh")
    private String djglDh;

    @JsonProperty("djgl_dh_djlx")
    private String djglDhDjlx;

    @Schema(description = "已抵用")
    @JsonProperty("ydyje")
    private BigDecimal ydyje;

    @JsonProperty("zh_sjfse")
    private Integer zhSjfse;

    @Schema(description = "账户/开户行")
    @JsonProperty("zh_mc")
    private String zhMc;

    @JsonProperty("zh_tb")
    private String zhTb;

    @JsonProperty("hy_id")
    private Long hyId;

    @Schema(description = "货运")
    @JsonProperty("hy_mc")
    private String hyMc;

    @Schema(description = "货运编号")
    @JsonProperty("hy_bh")
    private String hyBh;

    @JsonProperty("hy_contact_id")
    private Long hyContactId;

    @Schema(description = "货运电话")
    @JsonProperty("hy_phone")
    private String hyPhone;

    @Schema(description = "货运备注")
    @JsonProperty("hy_remark")
    private String hyRemark;

    @Schema(description = "货运运费")
    @JsonProperty("hy_je")
    private BigDecimal hyJe;

    @Schema(description = "货运单号")
    @JsonProperty("hy_dh")
    private String hyDh;

    @Schema(description = "货运联系人")
    @JsonProperty("hy_contact")
    private String hyContact;

    @JsonProperty("hy_ysfs_id")
    private Long hyYsfsId;

    @Schema(description = "货运运输方式")
    @JsonProperty("hy_ysfs")
    private String hyYsfs;

    @Schema(description = "货运计价")
    @JsonProperty("hy_jjfs")
    private String hyJjfs;

    @Schema(description = "商城")
    @JsonProperty("is_shop")
    private Integer isShop;

    @JsonProperty("shop_type")
    private String shopType;

    @Schema(description = "平台名称")
    @JsonProperty("shop_type_mc")
    private String shopTypeMc;

    @JsonProperty("dj_jg_lx")
    private String djJgLx;

    @Schema(description = "整单类型")
    @JsonProperty("dj_jg_lx_mc")
    private String djJgLxMc;

    @JsonProperty("bdyh_je")
    private BigDecimal bdyhJe;

    @JsonProperty("is_has_bdyh")
    private Integer isHasBdyh;

    @JsonProperty("fp_id")
    private Long fpId;

    @Schema(description = "发票公司抬头ID")
    @JsonProperty("kpgs_id")
    private Long kpgsId;

    @Schema(description = "门店ID")
    @JsonProperty("md_id")
    private Long mdId;

    @Schema(description = "门店")
    @JsonProperty("md_mc")
    private String mdMc;

    @JsonProperty("dd_lb_id")
    private Long ddLbId;

    @JsonProperty("dd_lb_fh")
    private String ddLbFh;

    @Schema(description = "订单类别")
    @JsonProperty("dd_lb_mc")
    private String ddLbMc;

    @JsonProperty("thfs_id")
    private Long thfsId;

    @JsonProperty("dj_thfs")
    private String djThfs;

    @JsonProperty("ywy_id")
    private Long ywyId;

    @JsonProperty("jsr_id")
    private Long jsrId;

    @JsonProperty("zdr_id")
    private Long zdrId;

    @JsonProperty("dj_ywy_id")
    private Long djYwyId;

    @Schema(description = "配货状态")
    @JsonProperty("znph_status")
    private Integer znphStatus;

    @Schema(description = "选中指定唛头类型")
    @JsonProperty("btw_type")
    private String btwType;

    @JsonProperty("btw_id")
    private Long btwId;

    @JsonProperty("btw_lang")
    private Integer btwLang;

    @JsonProperty("dj_jd")
    private Integer djJd;

    @JsonProperty("is_jd")
    private Integer isJd;

    @JsonProperty("jdr_id")
    private Long jdrId;

    @Schema(description = "结单日期")
    @JsonProperty("jd_sj")
    private String jdSj;

    @Schema(description = "结单")
    @JsonProperty("jd_zt")
    private String jdZt;

    @Schema(description = "复核")
    @JsonProperty("is_fh")
    private Integer isFh;

    @JsonProperty("fhr_id")
    private Long fhrId;

    @Schema(description = "复核日期")
    @JsonProperty("fh_sj")
    private String fhSj;

    @Schema(description = "仓库ID")
    @JsonProperty("ck_id")
    private Long ckId;

    @Schema(description = "仓库")
    @JsonProperty("ck_mc")
    private String ckMc;

    @Schema(description = "付款方")
    @JsonProperty("jkf_mc")
    private String jkfMc;

    @JsonProperty("jkfs_id")
    private Long jkfsId;

    @Schema(description = "标准价")
    @JsonProperty("bzj")
    private BigDecimal bzj;

    @Schema(description = "最低价")
    @JsonProperty("zdj")
    private BigDecimal zdj;

    @Schema(description = "净重最低价")
    @JsonProperty("jzzdj")
    private BigDecimal jzzdj;

    @Schema(description = "排序")
    @JsonProperty("px")
    private Integer px;

    @Schema(description = "备注")
    @JsonProperty("jl_bz")
    private String jlBz;

    @JsonProperty("other_info")
    //private Map<String,JSONArray> otherInfo;
    private Map<String, Object> otherInfo;
    //private Map<String, List<Object>> otherInfo;

    @Schema(description = "产品类别ID")
    @JsonProperty("cp_lb_id")
    private Long cpLbId;

    @Schema(description = "仓库类别ID")
    @JsonProperty("ck_lb_id")
    private Long ckLbId;

    @Schema(description = "仓/类别")
    @JsonProperty("ck_lb_mc")
    private String ckLbMc;

    @Schema(description = "产品ID")
    @JsonProperty("cp_id")
    private Long cpId;

    @Schema(description = "产品编号")
    @JsonProperty("cp_bh")
    private String cpBh;

    @Schema(description = "产品名称")
    @JsonProperty("cp_mc")
    private String cpMc;

    @JsonProperty("type")
    private String type;

    @Schema(description = "获取产品的加工单位")
    @JsonProperty("cpdw_info")
    private String cpdwInfo;

    @Schema(description = "图")
    @JsonProperty("tp")
    private String tp;

    @Schema(description = "产品颜色ID")
    @JsonProperty("cp_ys_id")
    private Long cpYsId;

    @Schema(description = "色号")
    @JsonProperty("cp_ys_bh")
    private String cpYsBh;

    @Schema(description = "颜色")
    @JsonProperty("cp_ys_mc")
    private String cpYsMc;

    @JsonProperty("bx_cp_id")
    private Long bxCpId;

    @Schema(description = "备选产品编号")
    @JsonProperty("bx_cp_bh")
    private String bxCpBh;

    @Schema(description = "备选产品名称")
    @JsonProperty("bx_cp_mc")
    private String bxCpMc;

    @JsonProperty("bx_cp_ys_id")
    private Long bxCpYsId;

    @Schema(description = "备选产品色号")
    @JsonProperty("bx_cp_ys_bh")
    private String bxCpYsBh;

    @Schema(description = "备选产品颜色")
    @JsonProperty("bx_cp_ys_mc")
    private String bxCpYsMc;

    @Schema(description = "辅助编号")
    @JsonProperty("fzbh")
    private String fzbh;

    @Schema(description = "辅助名称")
    @JsonProperty("fzmc")
    private String fzmc;

    @Schema(description = "辅助颜色")
    @JsonProperty("fzys")
    private String fzys;

    @Schema(description = "辅助色号")
    @JsonProperty("fzsh")
    private String fzsh;

    @Schema(description = "门店辅助编号")
    @JsonProperty("md_fzbh")
    private String mdFzbh;

    @Schema(description = "门店辅助名称")
    @JsonProperty("md_fzmc")
    private String mdFzmc;

    @Schema(description = "门店辅助颜色")
    @JsonProperty("md_fzys")
    private String mdFzys;

    @Schema(description = "门店辅助色号")
    @JsonProperty("md_fzsh")
    private String mdFzsh;

    @Schema(description = "幅宽")
    @JsonProperty("fk")
    private String fk;

    @Schema(description = "克重")
    @JsonProperty("kz")
    private String kz;

    @Schema(description = "幅宽单位")
    @JsonProperty("fk_dw")
    private String fkDw;

    @Schema(description = "克重单位")
    @JsonProperty("kz_dw")
    private String kzDw;

    @JsonProperty("fk_dw_id")
    private Long fkDwId;

    @JsonProperty("kz_dw_id")
    private Long kzDwId;

    @Schema(description = "纱支")
    @JsonProperty("sz")
    private String sz;

    @Schema(description = "密度")
    @JsonProperty("md")
    private String md;

    @Schema(description = "规格")
    @JsonProperty("gg")
    private String gg;

    @Schema(description = "后整")
    @JsonProperty("hz_info")
    private String hzInfo;

    @Schema(description = "成分")
    @JsonProperty("cf")
    private String cf;

    @JsonProperty("jg_lx")
    private String jgLx;

    @Schema(description = "类型")
    @JsonProperty("jg_lx_mc")
    private String jgLxMc;

    @Schema(description = "缸号")
    @JsonProperty("gh")
    private String gh;

    @Schema(description = "卷号")
    @JsonProperty("jh")
    private String jh;

    @Schema(description = "备选缸号")
    @JsonProperty("bxgh")
    private String bxgh;

    @JsonProperty("bxgh_info")
    private String bxghInfo;

    @Schema(description = "染厂色号")
    @JsonProperty("ck_zdy1")
    private String ckZdy1;

    @Schema(description = "工艺批次号")
    @JsonProperty("ck_zdy2")
    private String ckZdy2;

    @Schema(description = "下单客户")
    @JsonProperty("ck_zdy3")
    private String ckZdy3;

    @Schema(description = "仓库_自定义4")
    @JsonProperty("ck_zdy4")
    private String ckZdy4;

    @Schema(description = "仓库_自定义5")
    @JsonProperty("ck_zdy5")
    private String ckZdy5;

    @Schema(description = "染色建议")
    @JsonProperty("dyeing_advice")
    private String dyeingAdvice;

    @Schema(description = "模式")
    @JsonProperty("kcms")
    private String kcms;

    @Schema(description = "空差")
    @JsonProperty("kc")
    private Integer kc;

    @Schema(description = "匹数")
    @JsonProperty("ps")
    private Integer ps;

    @Schema(description = "数量")
    @JsonProperty("zsl")
    private Double zsl;

    @Schema(description = "数量2")
    @JsonProperty("zsl2")
    private Double zsl2;

    @Schema(description = "计价数量")
    @JsonProperty("jjsl")
    private Double jjsl;

    @Schema(description = "匹重")
    @JsonProperty("pz")
    private Integer pz;

    @Schema(description = "加/减匹")
    @JsonProperty("cxps")
    private Integer cxps;

    @Schema(description = "加/减数")
    @JsonProperty("cxsl")
    private Double cxsl;

    public void setYfPs(Integer yfPs) {
        this.yfPs = yfPs;
    }

    public Integer getYfPs() {
        return yfPs;
    }

    @Schema(description = "应发匹")
    @JsonProperty("yf_ps")
    private Integer yfPs;

    public void setYfSl(Double yfSl) {
        this.yfSl = yfSl;
    }

    public Double getYfSl() {
        return yfSl;
    }

    @Schema(description = "应发数")
    @JsonProperty("yf_sl")
    private Double yfSl;

    @JsonProperty("dw_id")
    private Long dwId;

    @JsonProperty("jjdw_id")
    private Long jjdwId;

    @JsonProperty("dw2_id")
    private Long dw2Id;

    @Schema(description = "入库价")
    @JsonProperty("zydj")
    private BigDecimal zydj;

    @Schema(description = "单价")
    @JsonProperty("dj")
    private BigDecimal dj;

    @Schema(description = "上次价")
    @JsonProperty("scdj")
    private BigDecimal scdj;

    @Schema(description = "金额")
    @JsonProperty("je")
    private BigDecimal je;

    @Schema(description = "税率%")
    @JsonProperty("shuilv")
    private BigDecimal shuilv;

    @Schema(description = "含税单价")
    @JsonProperty("hsdj")
    private BigDecimal hsdj;

    @Schema(description = "含税金额")
    @JsonProperty("hsje")
    private BigDecimal hsje;

    @Schema(description = "税额")
    @JsonProperty("shuie")
    private BigDecimal shuie;

    @Schema(description = "含税总金额")
    @JsonProperty("hszje")
    private BigDecimal hszje;

    @Schema(description = "小缸费")
    @JsonProperty("fy_zdy1")
    private BigDecimal fyZdy1;

    @Schema(description = "胶袋费")
    @JsonProperty("fy_zdy2")
    private BigDecimal fyZdy2;

    @Schema(description = "运费")
    @JsonProperty("fy_zdy3")
    private BigDecimal fyZdy3;

    @Schema(description = "曲线")
    @JsonProperty("fy_zdy4")
    private BigDecimal fyZdy4;

    @Schema(description = "路径")
    @JsonProperty("fy_zdy5")
    private BigDecimal fyZdy5;

    @Schema(description = "总金额/入库成本")
    @JsonProperty("zje")
    private BigDecimal zje;

    @Schema(description = "入库直接填“纸筒”+空差")
    @JsonProperty("kd_zdy1")
    private String kdZdy1;

    @Schema(description = "交货异议")
    @JsonProperty("kd_zdy2")
    private String kdZdy2;

    @Schema(description = "开单_自定义3")
    @JsonProperty("kd_zdy3")
    private String kdZdy3;

    @Schema(description = "开单_自定义4")
    @JsonProperty("kd_zdy4")
    private String kdZdy4;

    @Schema(description = "开单_自定义5")
    @JsonProperty("kd_zdy5")
    private String kdZdy5;

    @Schema(description = "实售空差")
    @JsonProperty("sskc")
    private Integer sskc;

    @Schema(description = "实售数量")
    @JsonProperty("sssl")
    private Double sssl;

    @Schema(description = "实售单价/实售价")
    @JsonProperty("ssdj")
    private BigDecimal ssdj;

    @Schema(description = "实售金额")
    @JsonProperty("ssje")
    private BigDecimal ssje;

    @Schema(description = "实售总金额/总实售额")
    @JsonProperty("sszje")
    private BigDecimal sszje;

    @Schema(description = "预回扣")
    @JsonProperty("yhk")
    private Integer yhk;

    @Schema(description = "实售差数")
    @JsonProperty("sscs")
    private Integer sscs;

    @JsonProperty("sexi")
    private String sexi;

    @JsonProperty("da_sexi")
    private String daSexi;

    @JsonProperty("jjcd")
    private Integer jjcd;

    @Schema(description = "指定等级/强制指定等级")
    @JsonProperty("ybdj")
    private String ybdj;

    @Schema(description = "标识")
    @JsonProperty("yb_bs")
    private String ybBs;

    @Schema(description = "指定染厂结果")
    @JsonProperty("rcjg")
    private String rcjg;

    @Schema(description = "指定织厂结果")
    @JsonProperty("zcjg")
    private String zcjg;

    @JsonProperty("yb_fa")
    private Integer ybFa;

    @Schema(description = "排除瑕疵")
    @JsonProperty("pcxc")
    private String pcxc;

    @Schema(description = "指定方案")
    @JsonProperty("yb_fa_mc")
    private String ybFaMc;

    @Schema(description = "货期")
    @JsonProperty("hq")
    private String hq;

    @Schema(description = "货期/天")
    @JsonProperty("hqts")
    private Integer hqts;

    @Schema(description = "船期")
    @JsonProperty("cq")
    private String cq;

    @Schema(description = "船期/天")
    @JsonProperty("cqts")
    private Integer cqts;

    @Schema(description = "船期剩/天")
    @JsonProperty("cqsyts")
    private Integer cqsyts;

    @Schema(description = "提前延后/天")
    @JsonProperty("tqyhts")
    private String tqyhts;

    @Schema(description = "剩/天")
    @JsonProperty("syts")
    private String syts;

    @JsonProperty("share_info")
    private String shareInfo;

    public Integer getFhps() {
        return fhps;
    }

    public void setFhps(Integer fhps) {
        this.fhps = fhps;
    }

    @Schema(description = "发货匹")
    @JsonProperty("fhps")
    private Integer fhps;

    @Schema(description = "未发匹")
    @JsonProperty("wfps")
    private Integer wfps;

    @Schema(description = "结单匹")
    @JsonProperty("jdps")
    private Integer jdps;

    public Double getFhsl() {
        return fhsl;
    }

    public void setFhsl(Double fhsl) {
        this.fhsl = fhsl;
    }

    @Schema(description = "发货数")
    @JsonProperty("fhsl")
    private Double fhsl;

    @Schema(description = "未发数")
    @JsonProperty("wfsl")
    private Double wfsl;

    @Schema(description = "结单数")
    @JsonProperty("jdsl")
    private Double jdsl;

    @Schema(description = "完成%")
    @JsonProperty("wcl")
    private Double wcl;

    @JsonProperty("wcs")
    private Double wcs;

    @JsonProperty("wcp")
    private String wcp;

    @JsonProperty("car_id")
    private Long carId;

    @JsonProperty("driver_id")
    private Long driverId;

    @Schema(description = "发车时间")
    @JsonProperty("fcsj")
    private String fcsj;

    @JsonProperty("mdd_id")
    private Long mddId;

    @Schema(description = "目的地")
    @JsonProperty("mdd_mc")
    private String mddMc;

    @Schema(description = "收货单位")
    @JsonProperty("receiving_unit")
    private String receivingUnit;

    @JsonProperty("received_unit_id")
    private Long receivedUnitId;

    @JsonProperty("jhr_id")
    private Long jhrId;

    @Schema(description = "拣货人")
    @JsonProperty("jhr_mc")
    private String jhrMc;

    @JsonProperty("fzr_id")
    private Long fzrId;

    @Schema(description = "接单人")
    @JsonProperty("cjq_jdr_mc")
    private String cjqJdrMc;

    @JsonProperty("ywgd_staff_id")
    private Long ywgdStaffId;

    @Schema(description = "首/翻单")
    @JsonProperty("s_fd")
    private String sFd;

    @Schema(description = "做货类型")
    @JsonProperty("zhlx_mc")
    private String zhlxMc;

    @JsonProperty("zhlx_id")
    private Long zhlxId;

    @Schema(description = "工作流")
    @JsonProperty("gzl_mc")
    private String gzlMc;

    @JsonProperty("gzl_id")
    private Long gzlId;

    @JsonProperty("gzl")
    private String gzl;

    @Schema(description = "已采集匹/已配货匹")
    @JsonProperty("cj_ps")
    private Integer cjPs;

    @Schema(description = "已采集数")
    @JsonProperty("cj_zsl")
    private Double cjZsl;

    @JsonProperty("cj_dy_ps")
    private Integer cjDyPs;

    @JsonProperty("cj_dy_zsl")
    private Double cjDyZsl;

    @Schema(description = "开始采集/开始配货")
    @JsonProperty("cj_kssj")
    private String cjKssj;

    @Schema(description = "结束采集/结束配货")
    @JsonProperty("cj_jssj")
    private String cjJssj;

    @Schema(description = "采集工时/配货工时")
    @JsonProperty("cj_gs")
    private Integer cjGs;

    @Schema(description = "采集状态")
    @JsonProperty("cjzt")
    private String cjzt;

    @Schema(description = "未调匹")
    @JsonProperty("cj_wdy_ps")
    private Integer cjWdyPs;

    @Schema(description = "未调数")
    @JsonProperty("cj_wdy_zsl")
    private Double cjWdyZsl;

    @Schema(description = "未采集匹/配货中匹")
    @JsonProperty("cj_wcj_ps")
    private Integer cjWcjPs;

    @Schema(description = "未采集数/配货中数")
    @JsonProperty("cj_wcj_zsl")
    private Double cjWcjZsl;

    @Schema(description = "入库匹")
    @JsonProperty("rkps")
    private Integer rkps;

    @Schema(description = "出库匹")
    @JsonProperty("ckps")
    private Integer ckps;

    @Schema(description = "调配匹")
    @JsonProperty("tpps")
    private Integer tpps;

    @Schema(description = "退货匹")
    @JsonProperty("thps")
    private Integer thps;

    @Schema(description = "订单库存匹")
    @JsonProperty("ddkcp")
    private Integer ddkcp;

    @Schema(description = "入库数/入库数量")
    @JsonProperty("rksl")
    private Double rksl;

    @Schema(description = "出库数")
    @JsonProperty("cksl")
    private Double cksl;

    @Schema(description = "调配数")
    @JsonProperty("tpsl")
    private Double tpsl;

    @Schema(description = "退货数")
    @JsonProperty("thsl")
    private Double thsl;

    @Schema(description = "订单库存数")
    @JsonProperty("ddkcs")
    private Integer ddkcs;

    @Schema(description = "验布长度")
    @JsonProperty("jycd")
    private Integer jycd;

    @Schema(description = "验布公斤")
    @JsonProperty("jygj")
    private Integer jygj;

    @Schema(description = "总扣长度")
    @JsonProperty("zk_cd")
    private Integer zkCd;

    @Schema(description = "总扣重量")
    @JsonProperty("zk_zl")
    private Integer zkZl;

    @Schema(description = "扣后长度")
    @JsonProperty("yb_ksh_cd")
    private Integer ybKshCd;

    @Schema(description = "订单未配匹")
    @JsonProperty("wph_ps")
    private Integer wphPs;

    @Schema(description = "订单未配数")
    @JsonProperty("wph_sl")
    private Double wphSl;

    @Schema(description = "订单已配匹")
    @JsonProperty("yph_ps")
    private Integer yphPs;

    @Schema(description = "订单已配数")
    @JsonProperty("yph_sl")
    private Double yphSl;

    @Schema(description = "仓库未配匹")
    @JsonProperty("ck_wph_ps")
    private Integer ckWphPs;

    @Schema(description = "仓库未配数")
    @JsonProperty("ck_wph_sl")
    private Double ckWphSl;

    @Schema(description = "仓库已配匹")
    @JsonProperty("ck_yph_ps")
    private Integer ckYphPs;

    @Schema(description = "仓库已配数")
    @JsonProperty("ck_yph_sl")
    private Double ckYphSl;

    public void setYfps(Integer yfps) {
        this.yfps = yfps;
    }

    public Integer getYfps() {
        return yfps;
    }

    @JsonProperty("yfps")
    private Integer yfps;

    public void setYfsl(Double yfsl) {
        this.yfsl = yfsl;
    }

    public Double getYfsl() {
        return yfsl;
    }

    @JsonProperty("yfsl")
    private Double yfsl;

    @Schema(description = "订单待分配匹")
    @JsonProperty("dd_dfp_ps")
    private Integer ddDfpPs;

    @Schema(description = "订单待分配数")
    @JsonProperty("dd_dfp_sl")
    private Double ddDfpSl;

    @JsonProperty("kfh_key")
    private String kfhKey;

    @JsonProperty("fh_ck_id")
    private Long fhCkId;

    @Schema(description = "发货仓库")
    @JsonProperty("fh_ck_mc")
    private String fhCkMc;

    @JsonProperty("last_edit_time")
    private String lastEditTime;

    @JsonProperty("is_wfh")
    private Integer isWfh;

    @Schema(description = "下织匹")
    @JsonProperty("xzd_ps")
    private Integer xzdPs;

    @Schema(description = "下织数")
    @JsonProperty("xzd_sl")
    private Double xzdSl;

    @Schema(description = "来货匹")
    @JsonProperty("xzd_lhps")
    private Integer xzdLhps;

    @Schema(description = "来货数")
    @JsonProperty("xzd_lhsl")
    private Double xzdLhsl;

    @Schema(description = "未来匹")
    @JsonProperty("xzd_wlhps")
    private String xzdWlhps;

    @Schema(description = "未来数")
    @JsonProperty("xzd_wlhsl")
    private Double xzdWlhsl;

    @Schema(description = "未下织匹")
    @JsonProperty("xzd_wxzps")
    private Integer xzdWxzps;

    @Schema(description = "未下织数")
    @JsonProperty("xzd_wxzsl")
    private Double xzdWxzsl;

    @Schema(description = "加工匹")
    @JsonProperty("wwjg_ps")
    private Integer wwjgPs;

    @Schema(description = "来货匹")
    @JsonProperty("wwjg_lhps")
    private Integer wwjgLhps;

    @Schema(description = "未来匹")
    @JsonProperty("wwjg_wlhps")
    private Integer wwjgWlhps;

    @Schema(description = "未加工匹")
    @JsonProperty("wwjg_wjgps")
    private Integer wwjgWjgps;

    @Schema(description = "加工数")
    @JsonProperty("wwjg_sl")
    private Double wwjgSl;

    @Schema(description = "来货数")
    @JsonProperty("wwjg_lhsl")
    private Double wwjgLhsl;

    @Schema(description = "未来数")
    @JsonProperty("wwjg_wlhsl")
    private Double wwjgWlhsl;

    @Schema(description = "未加工数")
    @JsonProperty("wwjg_wjgsl")
    private Double wwjgWjgsl;

    @Schema(description = "采购匹")
    @JsonProperty("pbcg_ps")
    private Integer pbcgPs;

    @Schema(description = "来货匹")
    @JsonProperty("pbcg_lhps")
    private Integer pbcgLhps;

    @Schema(description = "未来匹")
    @JsonProperty("pbcg_wlhps")
    private Integer pbcgWlhps;

    @Schema(description = "坯布匹")
    @JsonProperty("pbcg_wcgps")
    private Integer pbcgWcgps;

    @Schema(description = "采购数")
    @JsonProperty("pbcg_sl")
    private Double pbcgSl;

    @Schema(description = "来货数")
    @JsonProperty("pbcg_lhsl")
    private Double pbcgLhsl;

    @Schema(description = "未来数")
    @JsonProperty("pbcg_wlhsl")
    private Double pbcgWlhsl;

    @Schema(description = "坯布数")
    @JsonProperty("pbcg_wcgsl")
    private Double pbcgWcgsl;

    @Schema(description = "采购匹")
    @JsonProperty("cpcg_ps")
    private Integer cpcgPs;

    @Schema(description = "来货匹")
    @JsonProperty("cpcg_lhps")
    private Integer cpcgLhps;

    @Schema(description = "未来匹")
    @JsonProperty("cpcg_wlhps")
    private Integer cpcgWlhps;

    @Schema(description = "成品匹")
    @JsonProperty("cpcg_wcgps")
    private Integer cpcgWcgps;

    @Schema(description = "采购数")
    @JsonProperty("cpcg_sl")
    private Double cpcgSl;

    @Schema(description = "来货数")
    @JsonProperty("cpcg_lhsl")
    private Double cpcgLhsl;

    @Schema(description = "未来数")
    @JsonProperty("cpcg_wlhsl")
    private Double cpcgWlhsl;

    @Schema(description = "成品数")
    @JsonProperty("cpcg_wcgsl")
    private Double cpcgWcgsl;

    @Schema(description = "最后发货日期")
    @JsonProperty("zhfhrq")
    private String zhfhrq;

    @Schema(description = "合同编号+")
    @JsonProperty("htbh")
    private String htbh;

    @Schema(description = "合同状态")
    @JsonProperty("htzt")
    private Integer htzt;

    @Schema(description = "签署方")
    @JsonProperty("qsflxrmc")
    private String qsflxrmc;

    @Schema(description = "发起方")
    @JsonProperty("fqflxrmc")
    private String fqflxrmc;

    @Schema(description = "截止时间")
    @JsonProperty("qsjzrq")
    private String qsjzrq;

    @JsonProperty("ht_id")
    private Long htId;

    @JsonProperty("fqflx")
    private Integer fqflx;

    @JsonProperty("fqfsjh")
    private String fqfsjh;

    @JsonProperty("qsflx")
    private Integer qsflx;

    @JsonProperty("qsfsjh")
    private String qsfsjh;

    @Schema(description = "合同状态")
    @JsonProperty("htzt_mc")
    private String htztMc;

    @Schema(description = "附件")
    @JsonProperty("fujian")
    private Integer fujian;

    @JsonProperty("xzgy")
    private String xzgy;

    @JsonProperty("xzyq")
    private List<Map> xzyq;

    @Schema(description = "金额")
    @JsonProperty("sjfh_je")
    private BigDecimal sjfhJe;

    @Schema(description = "总金额")
    @JsonProperty("sjfh_zje")
    private BigDecimal sjfhZje;

    @Schema(description = "含税金额")
    @JsonProperty("sjfh_hsje")
    private BigDecimal sjfhHsje;

    @Schema(description = "含税总金额")
    @JsonProperty("sjfh_hszje")
    private BigDecimal sjfhHszje;

    @Schema(description = "实售金额")
    @JsonProperty("sjfh_ssje")
    private BigDecimal sjfhSsje;

    @Schema(description = "实售总金额")
    @JsonProperty("sjfh_sszje")
    private BigDecimal sjfhSszje;

    @Schema(description = "品牌")
    @JsonProperty("brand")
    private String brand;

    @Schema(description = "款号")
    @JsonProperty("style_number")
    private String styleNumber;

    @Schema(description = "季度ID")
    @JsonProperty("quarter_id")
    private Long quarterId;

    @Schema(description = "季度")
    @JsonProperty("quarter")
    private String quarter;

    @Schema(description = "特殊搭配")
    @JsonProperty("is_tsdp")
    private Integer isTsdp;

    @JsonProperty("lxr_id")
    private Long lxrId;

    @JsonProperty("bz")
    private String bz;

    @Schema(description = "排产匹")
    @JsonProperty("pc_ps")
    private Integer pcPs;

    @Schema(description = "排产数")
    @JsonProperty("pc_sl")
    private Double pcSl;

    @Schema(description = "未排匹")
    @JsonProperty("wpc_ps")
    private Integer wpcPs;

    @Schema(description = "未排数")
    @JsonProperty("wpc_sl")
    private Double wpcSl;

    @Schema(description = "记录来源")
    @JsonProperty("jl_ly")
    private String jlLy;

    @Schema(description = "对色缸号")
    @JsonProperty("dsgh")
    private String dsgh;

    @JsonProperty("kd_id")
    private Long kdId;

    @Schema(description = "换算数")
    @JsonProperty("hssl")
    private Double hssl;

    @Schema(description = "换算异常")
    @JsonProperty("is_hsyc")
    private Integer isHsyc;

    @Schema(description = "希音送货单号")
    @JsonProperty("platform_deliver_no")
    private String platformDeliverNo;

    @Schema(description = "希音签收状态")
    @JsonProperty("platform_is_receipt")
    private Integer platformIsReceipt;

    @Schema(description = "发票类型")
    @JsonProperty("fp_mc")
    private String fpMc;

    @Schema(description = "抬头")
    @JsonProperty("kpgs_mc")
    private String kpgsMc;

    @JsonProperty("wldw_quyu")
    private String wldwQuyu;

    @Schema(description = "核验区域")
    @JsonProperty("heyan_quyu")
    private String heyanQuyu;

    @Schema(description = "发货仓位")
    @JsonProperty("cw_mc")
    private String cwMc;

    @JsonProperty("cw_id")
    private Long cwId;

    @JsonProperty("groupStr")
    private String groupStr;

    @Schema(description = "付款方式/结款方式")
    @JsonProperty("jkfs_mc")
    private String jkfsMc;

    @Schema(description = "客/类别/供/类别")
    @JsonProperty("wldw_lb")
    private String wldwLb;

    @Schema(description = "业务员")
    @JsonProperty("ywy_mc")
    private String ywyMc;

    @Schema(description = "单据业务员")
    @JsonProperty("dj_ywy_mc")
    private String djYwyMc;

    @Schema(description = "经手人")
    @JsonProperty("jsr_mc")
    private String jsrMc;

    @Schema(description = "业务跟单")
    @JsonProperty("ywgd_staff_mc")
    private String ywgdStaffMc;

    @Schema(description = "制单人")
    @JsonProperty("zdr_mc")
    private String zdrMc;

    @Schema(description = "复核人")
    @JsonProperty("fhr_mc")
    private String fhrMc;

    @Schema(description = "结单人")
    @JsonProperty("jdr_mc")
    private String jdrMc;

    @Schema(description = "产/类别")
    @JsonProperty("cp_lb")
    private String cpLb;

    @JsonProperty("zdw_mc")
    private String zdwMc;

    @JsonProperty("zjjdw_mc")
    private String zjjdwMc;

    @Schema(description = "单位")
    @JsonProperty("dw_mc")
    private String dwMc;

    @Schema(description = "计价单位")
    @JsonProperty("jjdw_mc")
    private String jjdwMc;

    @Schema(description = "单位2")
    @JsonProperty("dw2_mc")
    private String dw2Mc;

    @Schema(description = "司机")
    @JsonProperty("driver")
    private String driver;

    @Schema(description = "车牌")
    @JsonProperty("plate")
    private String plate;

    @JsonProperty("IMEI")
    private String IMEI;

    @Schema(description = "取消人")
    @JsonProperty("qxr_mc")
    private String qxrMc;

    @Schema(description = "负责人")
    @JsonProperty("fzr_mc")
    private String fzrMc;

    @JsonProperty("fzxx_wldw_mc")
    private String fzxxWldwMc;

    @Schema(description = "取消原因")
    @JsonProperty("qx_yy")
    private String qxYy;

    @Schema(description = "色系")
    @JsonProperty("color_type")
    private String colorType;

    @Schema(description = "档案色系")
    @JsonProperty("da_color_type")
    private String daColorType;

    @Schema(description = "紧急程度")
    @JsonProperty("jjcd_mc")
    private String jjcdMc;

    @JsonProperty("jjcd_colour")
    private String jjcdColour;

    @JsonProperty("thfs_address")
    private String thfsAddress;

    @JsonProperty("thfs_lxr")
    private String thfsLxr;

    @JsonProperty("thfs_phone")
    private String thfsPhone;

    @Schema(description = "自提")
    @JsonProperty("is_self_pick")
    private Integer isSelfPick;

    @Schema(description = "提货")
    @JsonProperty("thfs")
    private String thfs;

    @JsonProperty("driver_hm")
    private String driverHm;

    @Schema(description = "货期")
    @JsonProperty("xzd_hq")
    private String xzdHq;

    @Schema(description = "加工商")
    @JsonProperty("xzd_wldw_mc")
    private String xzdWldwMc;

    @Schema(description = "来货日期")
    @JsonProperty("xzd_kdsj")
    private String xzdKdsj;

    @Schema(description = "开始日期")
    @JsonProperty("xzd_kssj")
    private String xzdKssj;

    @Schema(description = "织布工时")
    @JsonProperty("xzd_gs")
    private String xzdGs;

    @Schema(description = "货期")
    @JsonProperty("pbcg_hq")
    private String pbcgHq;

    @Schema(description = "供应商")
    @JsonProperty("pbcg_wldw_mc")
    private String pbcgWldwMc;

    @Schema(description = "开始日期")
    @JsonProperty("pbcg_kssj")
    private String pbcgKssj;

    @Schema(description = "来货日期")
    @JsonProperty("pbcg_kdsj")
    private String pbcgKdsj;

    @Schema(description = "采购工时")
    @JsonProperty("pbcg_gs")
    private String pbcgGs;

    @Schema(description = "货期")
    @JsonProperty("cpcg_hq")
    private String cpcgHq;

    @Schema(description = "供应商")
    @JsonProperty("cpcg_wldw_mc")
    private String cpcgWldwMc;

    @Schema(description = "开始日期")
    @JsonProperty("cpcg_kssj")
    private String cpcgKssj;

    @Schema(description = "来货日期")
    @JsonProperty("cpcg_kdsj")
    private String cpcgKdsj;

    @Schema(description = "采购工时")
    @JsonProperty("cpcg_gs")
    private String cpcgGs;

    @Schema(description = "货期")
    @JsonProperty("wwjg_hq")
    private String wwjgHq;

    @JsonProperty("wwjg_wldw_id")
    private Long wwjgWldwId;

    @Schema(description = "加工商")
    @JsonProperty("wwjg_wldw_mc")
    private String wwjgWldwMc;

    @Schema(description = "开始日期")
    @JsonProperty("wwjg_kssj")
    private String wwjgKssj;

    @Schema(description = "来货日期")
    @JsonProperty("wwjg_kdsj")
    private String wwjgKdsj;

    @Schema(description = "加工工时")
    @JsonProperty("wwjg_gs")
    private String wwjgGs;

    @JsonProperty("is_yb")
    private Integer isYb;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("gxlx_mc")
    private String gxlxMc;

    @JsonProperty("jglx_6_px")
    private Integer jglx6Px;

    @JsonProperty("jglx_6_gyyq")
    private String jglx6Gyyq;

    @JsonProperty("jglx_6_rzgy")
    private String jglx6Rzgy;

    @JsonProperty("jglx_6_rzyq")
    private JSONArray jglx6Rzyq;

    @JsonProperty("jglx_6_is_yb")
    private Integer jglx6IsYb;

    @JsonProperty("jglx_6_0_rzgy")
    private String jglx60Rzgy;

    @JsonProperty("jglx_6_0_rzyq")
    private JSONArray jglx60Rzyq;

    @JsonProperty("jglx_6_jgs_id")
    private Long jglx6JgsId;

    @Schema(description = "预/加工商")
    @JsonProperty("jglx_6_jgs_mc")
    private String jglx6JgsMc;

    @JsonProperty("jglx_6_gx_info")
    private JSONArray jglx6GxInfo;

    @JsonProperty("jglx_6_gxlx_id")
    private Long jglx6GxlxId;

    @JsonProperty("jglx_6_jglc_id")
    private Long jglx6JglcId;

    @Schema(description = "加工匹")
    @JsonProperty("jglx_6_wwjg_ps")
    private Integer jglx6WwjgPs;

    @Schema(description = "加工数")
    @JsonProperty("jglx_6_wwjg_sl")
    private Double jglx6WwjgSl;

    @Schema(description = "来货匹")
    @JsonProperty("jglx_6_wwjg_lhps")
    private Integer jglx6WwjgLhps;

    @Schema(description = "来货数")
    @JsonProperty("jglx_6_wwjg_lhsl")
    private Double jglx6WwjgLhsl;

    @Schema(description = "未加工匹")
    @JsonProperty("jglx_6_wwjg_wjgps")
    private Integer jglx6WwjgWjgps;

    @Schema(description = "未加工数")
    @JsonProperty("jglx_6_wwjg_wjgsl")
    private Double jglx6WwjgWjgsl;

    @Schema(description = "未来匹")
    @JsonProperty("jglx_6_wwjg_wlhps")
    private Integer jglx6WwjgWlhps;

    @Schema(description = "未来数")
    @JsonProperty("jglx_6_wwjg_wlhsl")
    private Double jglx6WwjgWlhsl;

    @Schema(description = "开始日期")
    @JsonProperty("jglx_6_wwjg_kssj")
    private String jglx6WwjgKssj;

    @Schema(description = "来货日期")
    @JsonProperty("jglx_6_wwjg_kdsj")
    private String jglx6WwjgKdsj;

    @Schema(description = "加工工时")
    @JsonProperty("jglx_6_wwjg_gs")
    private String jglx6WwjgGs;

    @Schema(description = "货期")
    @JsonProperty("jglx_6_wwjg_hq")
    private String jglx6WwjgHq;

    @JsonProperty("jglx_6_wwjg_wldw_id")
    private Long jglx6WwjgWldwId;

    @Schema(description = "加工商")
    @JsonProperty("jglx_6_wwjg_wldw_mc")
    private String jglx6WwjgWldwMc;

    @JsonProperty("dd_type")
    private JSONArray ddType;

    @Schema(description = "状态")
    @JsonProperty("zt")
    private String zt;

    @JsonProperty("zt_id")
    private Long ztId;

    @Schema(description = "色号颜色/样式")
    @JsonProperty("colour")
    private String colour;

    @Schema(description = "状态更新时间")
    @JsonProperty("ztgxsj")
    private String ztgxsj;

    @JsonProperty("jjcd_type")
    private JSONArray jjcdType;

    @Schema(description = "打样单号")
    @JsonProperty("dy_dh")
    private String dyDh;

    @JsonProperty("dy_dd_dj_id")
    private Long dyDdDjId;

    @JsonProperty("dy_dd_jl_id")
    private Long dyDdJlId;

    @Schema(description = "打样状态")
    @JsonProperty("dy_zt")
    private String dyZt;

    @JsonProperty("dy_zt_id")
    private Long dyZtId;

    @JsonProperty("dy_zt_colour")
    private String dyZtColour;

    @Schema(description = "大货样单号")
    @JsonProperty("dhy_dh")
    private String dhyDh;

    @JsonProperty("dhy_dd_dj_id")
    private Long dhyDdDjId;

    @JsonProperty("dhy_dd_jl_id")
    private Long dhyDdJlId;

    @Schema(description = "大货样状态")
    @JsonProperty("dhy_zt")
    private String dhyZt;

    @JsonProperty("dhy_zt_id")
    private Long dhyZtId;

    @JsonProperty("dhy_zt_colour")
    private String dhyZtColour;

    @Schema(description = "进度")
    @JsonProperty("jd")
    private String jd;

    @JsonProperty("yb_bs_list")
    private JSONArray ybBsList;

    @JsonProperty("rcjg_list")
    private JSONArray rcjgList;

    @JsonProperty("zcjg_list")
    private JSONArray zcjgList;

    @Schema(description = "进行中进度")
    @JsonProperty("jxz_jd")
    private String jxzJd;

    @Schema(description = "欠缺进度")
    @JsonProperty("qq_jd")
    private String qqJd;

    @Schema(description = "工作流进度")
    @JsonProperty("gzl_jd")
    private String gzlJd;

    @Schema(description = "唛头")
    @JsonProperty("maitou")
    private List<Map> maitou;

    @Schema(description = "总匹数")
    @JsonProperty("dj_ps")
    private Integer djPs;

    @Schema(description = "总数量")
    @JsonProperty("dj_zsl")
    private Double djZsl;

    @Schema(description = "单据金额")
    @JsonProperty("dj_je")
    private BigDecimal djJe;

    @Schema(description = "单据未发匹数")
    @JsonProperty("dj_wfps")
    private Integer djWfps;

    @Schema(description = "单据未发数量")
    @JsonProperty("dj_wfsl")
    private Double djWfsl;

    @Schema(description = "单据发货匹数")
    @JsonProperty("dj_fhps")
    private Integer djFhps;

    @Schema(description = "单据发货数量")
    @JsonProperty("dj_fhsl")
    private Double djFhsl;

    @Schema(description = "单据坯布采购未采购匹数")
    @JsonProperty("dj_pbcg_wcgps")
    private Integer djPbcgWcgps;

    @Schema(description = "单据坯布采购未采购数量")
    @JsonProperty("dj_pbcg_wcgsl")
    private Double djPbcgWcgsl;

    @Schema(description = "单据产品采购未采购匹数")
    @JsonProperty("dj_cpcg_wcgps")
    private Integer djCpcgWcgps;

    @Schema(description = "单据产品采购未采购数量")
    @JsonProperty("dj_cpcg_wcgsl")
    private Double djCpcgWcgsl;

    @JsonProperty("__jg_zlhps")
    private Integer jgZlhps;

    @JsonProperty("__jg_zlhsl")
    private Double jgZlhsl;

    @Schema(description = "预收订金/原价")
    @JsonProperty("ysdj")
    private BigDecimal ysdj;

    @JsonProperty("oa_xtsq_id")
    private Long oaXtsqId;

    @Schema(description = "产品类型名称")
    @JsonProperty("cp_lx_mc")
    private String cpLxMc;

    public String getCpCjDescription() {
        return cpCjDescription;
    }

    public void setCpCjDescription(String cpCjDescription) {
        this.cpCjDescription = cpCjDescription;
    }

    public String getDjlx() {
        return djlx;
    }

    public void setDjlx(String djlx) {
        this.djlx = djlx;
    }

    public String getDjlxMc() {
        return djlxMc;
    }

    public void setDjlxMc(String djlxMc) {
        this.djlxMc = djlxMc;
    }

    public String getDjlxQm() {
        return djlxQm;
    }

    public void setDjlxQm(String djlxQm) {
        this.djlxQm = djlxQm;
    }

    public Long getDjId() {
        return djId;
    }

    public void setDjId(Long djId) {
        this.djId = djId;
    }

    public Long getJlId() {
        return jlId;
    }

    public void setJlId(Long jlId) {
        this.jlId = jlId;
    }

    public String getLxr() {
        return lxr;
    }

    public void setLxr(String lxr) {
        this.lxr = lxr;
    }

    public String getLxrDh() {
        return lxrDh;
    }

    public void setLxrDh(String lxrDh) {
        this.lxrDh = lxrDh;
    }

    public String getLxrQbdz() {
        return lxrQbdz;
    }

    public void setLxrQbdz(String lxrQbdz) {
        this.lxrQbdz = lxrQbdz;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLxrProvince() {
        return lxrProvince;
    }

    public void setLxrProvince(String lxrProvince) {
        this.lxrProvince = lxrProvince;
    }

    public String getLxrCity() {
        return lxrCity;
    }

    public void setLxrCity(String lxrCity) {
        this.lxrCity = lxrCity;
    }

    public String getLxrTown() {
        return lxrTown;
    }

    public void setLxrTown(String lxrTown) {
        this.lxrTown = lxrTown;
    }

    public Integer getLxrCountry() {
        return lxrCountry;
    }

    public void setLxrCountry(Integer lxrCountry) {
        this.lxrCountry = lxrCountry;
    }

    public String getLxrDz() {
        return lxrDz;
    }

    public void setLxrDz(String lxrDz) {
        this.lxrDz = lxrDz;
    }

    public Integer getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Integer isDraft) {
        this.isDraft = isDraft;
    }

    public String getSheinOpenKey() {
        return sheinOpenKey;
    }

    public void setSheinOpenKey(String sheinOpenKey) {
        this.sheinOpenKey = sheinOpenKey;
    }

    public String getSheinBranchFactory() {
        return sheinBranchFactory;
    }

    public void setSheinBranchFactory(String sheinBranchFactory) {
        this.sheinBranchFactory = sheinBranchFactory;
    }

    public String getWldhhqfs() {
        return wldhhqfs;
    }

    public void setWldhhqfs(String wldhhqfs) {
        this.wldhhqfs = wldhhqfs;
    }

    public Integer getIsSyncVcans() {
        return isSyncVcans;
    }

    public void setIsSyncVcans(Integer isSyncVcans) {
        this.isSyncVcans = isSyncVcans;
    }

    public Integer getIsOnlinePay() {
        return isOnlinePay;
    }

    public void setIsOnlinePay(Integer isOnlinePay) {
        this.isOnlinePay = isOnlinePay;
    }

    public Long getOutTransactionId() {
        return outTransactionId;
    }

    public void setOutTransactionId(Long outTransactionId) {
        this.outTransactionId = outTransactionId;
    }

    public String getKdsj() {
        return kdsj;
    }

    public void setKdsj(String kdsj) {
        this.kdsj = kdsj;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getFzDh() {
        return fzDh;
    }

    public void setFzDh(String fzDh) {
        this.fzDh = fzDh;
    }

    public String getDjLsh() {
        return djLsh;
    }

    public void setDjLsh(String djLsh) {
        this.djLsh = djLsh;
    }

    public Integer getIsSh() {
        return isSh;
    }

    public void setIsSh(Integer isSh) {
        this.isSh = isSh;
    }

    public String getDjBz() {
        return djBz;
    }

    public void setDjBz(String djBz) {
        this.djBz = djBz;
    }

    public BigDecimal getDjSszje() {
        return djSszje;
    }

    public void setDjSszje(BigDecimal djSszje) {
        this.djSszje = djSszje;
    }

    public Integer getDjZyhk() {
        return djZyhk;
    }

    public void setDjZyhk(Integer djZyhk) {
        this.djZyhk = djZyhk;
    }

    public Integer getIsQx() {
        return isQx;
    }

    public void setIsQx(Integer isQx) {
        this.isQx = isQx;
    }

    public Long getQxrId() {
        return qxrId;
    }

    public void setQxrId(Long qxrId) {
        this.qxrId = qxrId;
    }

    public String getQxSj() {
        return qxSj;
    }

    public void setQxSj(String qxSj) {
        this.qxSj = qxSj;
    }

    public Long getQxYyId() {
        return qxYyId;
    }

    public void setQxYyId(Long qxYyId) {
        this.qxYyId = qxYyId;
    }

    public String getQxYyMs() {
        return qxYyMs;
    }

    public void setQxYyMs(String qxYyMs) {
        this.qxYyMs = qxYyMs;
    }

    public Long getZdwId() {
        return zdwId;
    }

    public void setZdwId(Long zdwId) {
        this.zdwId = zdwId;
    }

    public Long getZjjdwId() {
        return zjjdwId;
    }

    public void setZjjdwId(Long zjjdwId) {
        this.zjjdwId = zjjdwId;
    }

    public Long getCjqJdrId() {
        return cjqJdrId;
    }

    public void setCjqJdrId(Long cjqJdrId) {
        this.cjqJdrId = cjqJdrId;
    }

    public String getCjqJdSj() {
        return cjqJdSj;
    }

    public void setCjqJdSj(String cjqJdSj) {
        this.cjqJdSj = cjqJdSj;
    }

    public String getZpms() {
        return zpms;
    }

    public void setZpms(String zpms) {
        this.zpms = zpms;
    }

    public String getCjqXtList() {
        return cjqXtList;
    }

    public void setCjqXtList(String cjqXtList) {
        this.cjqXtList = cjqXtList;
    }

    public Integer getIsYdy() {
        return isYdy;
    }

    public void setIsYdy(Integer isYdy) {
        this.isYdy = isYdy;
    }

    public Integer getDyCs() {
        return dyCs;
    }

    public void setDyCs(Integer dyCs) {
        this.dyCs = dyCs;
    }

    public Long getCgGysId() {
        return cgGysId;
    }

    public void setCgGysId(Long cgGysId) {
        this.cgGysId = cgGysId;
    }

    public String getGysMc() {
        return gysMc;
    }

    public void setGysMc(String gysMc) {
        this.gysMc = gysMc;
    }

    public Long getWldwId() {
        return wldwId;
    }

    public void setWldwId(Long wldwId) {
        this.wldwId = wldwId;
    }

    public String getWldwMc() {
        return wldwMc;
    }

    public void setWldwMc(String wldwMc) {
        this.wldwMc = wldwMc;
    }

    public String getWldwBh() {
        return wldwBh;
    }

    public void setWldwBh(String wldwBh) {
        this.wldwBh = wldwBh;
    }

    public Long getWldwLbId() {
        return wldwLbId;
    }

    public void setWldwLbId(Long wldwLbId) {
        this.wldwLbId = wldwLbId;
    }

    public Long getyZcId() {
        return yZcId;
    }

    public void setyZcId(Long yZcId) {
        this.yZcId = yZcId;
    }

    public String getyZcMc() {
        return yZcMc;
    }

    public void setyZcMc(String yZcMc) {
        this.yZcMc = yZcMc;
    }

    public BigDecimal getZhJe() {
        return zhJe;
    }

    public void setZhJe(BigDecimal zhJe) {
        this.zhJe = zhJe;
    }

    public String getYzfSkDh() {
        return yzfSkDh;
    }

    public void setYzfSkDh(String yzfSkDh) {
        this.yzfSkDh = yzfSkDh;
    }

    public String getYzfSkDhDjlx() {
        return yzfSkDhDjlx;
    }

    public void setYzfSkDhDjlx(String yzfSkDhDjlx) {
        this.yzfSkDhDjlx = yzfSkDhDjlx;
    }

    public String getDjglDh() {
        return djglDh;
    }

    public void setDjglDh(String djglDh) {
        this.djglDh = djglDh;
    }

    public String getDjglDhDjlx() {
        return djglDhDjlx;
    }

    public void setDjglDhDjlx(String djglDhDjlx) {
        this.djglDhDjlx = djglDhDjlx;
    }

    public BigDecimal getYdyje() {
        return ydyje;
    }

    public void setYdyje(BigDecimal ydyje) {
        this.ydyje = ydyje;
    }

    public Integer getZhSjfse() {
        return zhSjfse;
    }

    public void setZhSjfse(Integer zhSjfse) {
        this.zhSjfse = zhSjfse;
    }

    public String getZhMc() {
        return zhMc;
    }

    public void setZhMc(String zhMc) {
        this.zhMc = zhMc;
    }

    public String getZhTb() {
        return zhTb;
    }

    public void setZhTb(String zhTb) {
        this.zhTb = zhTb;
    }

    public Long getHyId() {
        return hyId;
    }

    public void setHyId(Long hyId) {
        this.hyId = hyId;
    }

    public String getHyMc() {
        return hyMc;
    }

    public void setHyMc(String hyMc) {
        this.hyMc = hyMc;
    }

    public String getHyBh() {
        return hyBh;
    }

    public void setHyBh(String hyBh) {
        this.hyBh = hyBh;
    }

    public Long getHyContactId() {
        return hyContactId;
    }

    public void setHyContactId(Long hyContactId) {
        this.hyContactId = hyContactId;
    }

    public String getHyPhone() {
        return hyPhone;
    }

    public void setHyPhone(String hyPhone) {
        this.hyPhone = hyPhone;
    }

    public String getHyRemark() {
        return hyRemark;
    }

    public void setHyRemark(String hyRemark) {
        this.hyRemark = hyRemark;
    }

    public BigDecimal getHyJe() {
        return hyJe;
    }

    public void setHyJe(BigDecimal hyJe) {
        this.hyJe = hyJe;
    }

    public String getHyDh() {
        return hyDh;
    }

    public void setHyDh(String hyDh) {
        this.hyDh = hyDh;
    }

    public String getHyContact() {
        return hyContact;
    }

    public void setHyContact(String hyContact) {
        this.hyContact = hyContact;
    }

    public Long getHyYsfsId() {
        return hyYsfsId;
    }

    public void setHyYsfsId(Long hyYsfsId) {
        this.hyYsfsId = hyYsfsId;
    }

    public String getHyYsfs() {
        return hyYsfs;
    }

    public void setHyYsfs(String hyYsfs) {
        this.hyYsfs = hyYsfs;
    }

    public String getHyJjfs() {
        return hyJjfs;
    }

    public void setHyJjfs(String hyJjfs) {
        this.hyJjfs = hyJjfs;
    }

    public Integer getIsShop() {
        return isShop;
    }

    public void setIsShop(Integer isShop) {
        this.isShop = isShop;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public String getShopTypeMc() {
        return shopTypeMc;
    }

    public void setShopTypeMc(String shopTypeMc) {
        this.shopTypeMc = shopTypeMc;
    }

    public String getDjJgLx() {
        return djJgLx;
    }

    public void setDjJgLx(String djJgLx) {
        this.djJgLx = djJgLx;
    }

    public String getDjJgLxMc() {
        return djJgLxMc;
    }

    public void setDjJgLxMc(String djJgLxMc) {
        this.djJgLxMc = djJgLxMc;
    }

    public BigDecimal getBdyhJe() {
        return bdyhJe;
    }

    public void setBdyhJe(BigDecimal bdyhJe) {
        this.bdyhJe = bdyhJe;
    }

    public Integer getIsHasBdyh() {
        return isHasBdyh;
    }

    public void setIsHasBdyh(Integer isHasBdyh) {
        this.isHasBdyh = isHasBdyh;
    }

    public Long getFpId() {
        return fpId;
    }

    public void setFpId(Long fpId) {
        this.fpId = fpId;
    }

    public Long getKpgsId() {
        return kpgsId;
    }

    public void setKpgsId(Long kpgsId) {
        this.kpgsId = kpgsId;
    }

    public Long getMdId() {
        return mdId;
    }

    public void setMdId(Long mdId) {
        this.mdId = mdId;
    }

    public String getMdMc() {
        return mdMc;
    }

    public void setMdMc(String mdMc) {
        this.mdMc = mdMc;
    }

    public Long getDdLbId() {
        return ddLbId;
    }

    public void setDdLbId(Long ddLbId) {
        this.ddLbId = ddLbId;
    }

    public String getDdLbFh() {
        return ddLbFh;
    }

    public void setDdLbFh(String ddLbFh) {
        this.ddLbFh = ddLbFh;
    }

    public String getDdLbMc() {
        return ddLbMc;
    }

    public void setDdLbMc(String ddLbMc) {
        this.ddLbMc = ddLbMc;
    }

    public Long getThfsId() {
        return thfsId;
    }

    public void setThfsId(Long thfsId) {
        this.thfsId = thfsId;
    }

    public String getDjThfs() {
        return djThfs;
    }

    public void setDjThfs(String djThfs) {
        this.djThfs = djThfs;
    }

    public Long getYwyId() {
        return ywyId;
    }

    public void setYwyId(Long ywyId) {
        this.ywyId = ywyId;
    }

    public Long getJsrId() {
        return jsrId;
    }

    public void setJsrId(Long jsrId) {
        this.jsrId = jsrId;
    }

    public Long getZdrId() {
        return zdrId;
    }

    public void setZdrId(Long zdrId) {
        this.zdrId = zdrId;
    }

    public Long getDjYwyId() {
        return djYwyId;
    }

    public void setDjYwyId(Long djYwyId) {
        this.djYwyId = djYwyId;
    }

    public Integer getZnphStatus() {
        return znphStatus;
    }

    public void setZnphStatus(Integer znphStatus) {
        this.znphStatus = znphStatus;
    }

    public String getBtwType() {
        return btwType;
    }

    public void setBtwType(String btwType) {
        this.btwType = btwType;
    }

    public Long getBtwId() {
        return btwId;
    }

    public void setBtwId(Long btwId) {
        this.btwId = btwId;
    }

    public Integer getBtwLang() {
        return btwLang;
    }

    public void setBtwLang(Integer btwLang) {
        this.btwLang = btwLang;
    }

    public Integer getDjJd() {
        return djJd;
    }

    public void setDjJd(Integer djJd) {
        this.djJd = djJd;
    }

    public Integer getIsJd() {
        return isJd;
    }

    public void setIsJd(Integer isJd) {
        this.isJd = isJd;
    }

    public Long getJdrId() {
        return jdrId;
    }

    public void setJdrId(Long jdrId) {
        this.jdrId = jdrId;
    }

    public String getJdSj() {
        return jdSj;
    }

    public void setJdSj(String jdSj) {
        this.jdSj = jdSj;
    }

    public String getJdZt() {
        return jdZt;
    }

    public void setJdZt(String jdZt) {
        this.jdZt = jdZt;
    }

    public Integer getIsFh() {
        return isFh;
    }

    public void setIsFh(Integer isFh) {
        this.isFh = isFh;
    }

    public Long getFhrId() {
        return fhrId;
    }

    public void setFhrId(Long fhrId) {
        this.fhrId = fhrId;
    }

    public String getFhSj() {
        return fhSj;
    }

    public void setFhSj(String fhSj) {
        this.fhSj = fhSj;
    }

    public Long getCkId() {
        return ckId;
    }

    public void setCkId(Long ckId) {
        this.ckId = ckId;
    }

    public String getCkMc() {
        return ckMc;
    }

    public void setCkMc(String ckMc) {
        this.ckMc = ckMc;
    }

    public String getJkfMc() {
        return jkfMc;
    }

    public void setJkfMc(String jkfMc) {
        this.jkfMc = jkfMc;
    }

    public Long getJkfsId() {
        return jkfsId;
    }

    public void setJkfsId(Long jkfsId) {
        this.jkfsId = jkfsId;
    }

    public BigDecimal getBzj() {
        return bzj;
    }

    public void setBzj(BigDecimal bzj) {
        this.bzj = bzj;
    }

    public BigDecimal getZdj() {
        return zdj;
    }

    public void setZdj(BigDecimal zdj) {
        this.zdj = zdj;
    }

    public BigDecimal getJzzdj() {
        return jzzdj;
    }

    public void setJzzdj(BigDecimal jzzdj) {
        this.jzzdj = jzzdj;
    }

    public Integer getPx() {
        return px;
    }

    public void setPx(Integer px) {
        this.px = px;
    }

    public String getJlBz() {
        return jlBz;
    }

    public void setJlBz(String jlBz) {
        this.jlBz = jlBz;
    }

    public Map<String, Object> getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(Map<String, Object> otherInfo) {
        this.otherInfo = otherInfo;
    }

    public Long getCpLbId() {
        return cpLbId;
    }

    public void setCpLbId(Long cpLbId) {
        this.cpLbId = cpLbId;
    }

    public Long getCkLbId() {
        return ckLbId;
    }

    public void setCkLbId(Long ckLbId) {
        this.ckLbId = ckLbId;
    }

    public String getCkLbMc() {
        return ckLbMc;
    }

    public void setCkLbMc(String ckLbMc) {
        this.ckLbMc = ckLbMc;
    }

    public Long getCpId() {
        return cpId;
    }

    public void setCpId(Long cpId) {
        this.cpId = cpId;
    }

    public String getCpBh() {
        return cpBh;
    }

    public void setCpBh(String cpBh) {
        this.cpBh = cpBh;
    }

    public String getCpMc() {
        return cpMc;
    }

    public void setCpMc(String cpMc) {
        this.cpMc = cpMc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCpdwInfo() {
        return cpdwInfo;
    }

    public void setCpdwInfo(String cpdwInfo) {
        this.cpdwInfo = cpdwInfo;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public Long getCpYsId() {
        return cpYsId;
    }

    public void setCpYsId(Long cpYsId) {
        this.cpYsId = cpYsId;
    }

    public String getCpYsBh() {
        return cpYsBh;
    }

    public void setCpYsBh(String cpYsBh) {
        this.cpYsBh = cpYsBh;
    }

    public String getCpYsMc() {
        return cpYsMc;
    }

    public void setCpYsMc(String cpYsMc) {
        this.cpYsMc = cpYsMc;
    }

    public Long getBxCpId() {
        return bxCpId;
    }

    public void setBxCpId(Long bxCpId) {
        this.bxCpId = bxCpId;
    }

    public String getBxCpBh() {
        return bxCpBh;
    }

    public void setBxCpBh(String bxCpBh) {
        this.bxCpBh = bxCpBh;
    }

    public String getBxCpMc() {
        return bxCpMc;
    }

    public void setBxCpMc(String bxCpMc) {
        this.bxCpMc = bxCpMc;
    }

    public Long getBxCpYsId() {
        return bxCpYsId;
    }

    public void setBxCpYsId(Long bxCpYsId) {
        this.bxCpYsId = bxCpYsId;
    }

    public String getBxCpYsBh() {
        return bxCpYsBh;
    }

    public void setBxCpYsBh(String bxCpYsBh) {
        this.bxCpYsBh = bxCpYsBh;
    }

    public String getBxCpYsMc() {
        return bxCpYsMc;
    }

    public void setBxCpYsMc(String bxCpYsMc) {
        this.bxCpYsMc = bxCpYsMc;
    }

    public String getFzbh() {
        return fzbh;
    }

    public void setFzbh(String fzbh) {
        this.fzbh = fzbh;
    }

    public String getFzmc() {
        return fzmc;
    }

    public void setFzmc(String fzmc) {
        this.fzmc = fzmc;
    }

    public String getFzys() {
        return fzys;
    }

    public void setFzys(String fzys) {
        this.fzys = fzys;
    }

    public String getFzsh() {
        return fzsh;
    }

    public void setFzsh(String fzsh) {
        this.fzsh = fzsh;
    }

    public String getMdFzbh() {
        return mdFzbh;
    }

    public void setMdFzbh(String mdFzbh) {
        this.mdFzbh = mdFzbh;
    }

    public String getMdFzmc() {
        return mdFzmc;
    }

    public void setMdFzmc(String mdFzmc) {
        this.mdFzmc = mdFzmc;
    }

    public String getMdFzys() {
        return mdFzys;
    }

    public void setMdFzys(String mdFzys) {
        this.mdFzys = mdFzys;
    }

    public String getMdFzsh() {
        return mdFzsh;
    }

    public void setMdFzsh(String mdFzsh) {
        this.mdFzsh = mdFzsh;
    }

    public String getFk() {
        return fk;
    }

    public void setFk(String fk) {
        this.fk = fk;
    }

    public String getKz() {
        return kz;
    }

    public void setKz(String kz) {
        this.kz = kz;
    }

    public String getFkDw() {
        return fkDw;
    }

    public void setFkDw(String fkDw) {
        this.fkDw = fkDw;
    }

    public String getKzDw() {
        return kzDw;
    }

    public void setKzDw(String kzDw) {
        this.kzDw = kzDw;
    }

    public Long getFkDwId() {
        return fkDwId;
    }

    public void setFkDwId(Long fkDwId) {
        this.fkDwId = fkDwId;
    }

    public Long getKzDwId() {
        return kzDwId;
    }

    public void setKzDwId(Long kzDwId) {
        this.kzDwId = kzDwId;
    }

    public String getSz() {
        return sz;
    }

    public void setSz(String sz) {
        this.sz = sz;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public String getGg() {
        return gg;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public String getHzInfo() {
        return hzInfo;
    }

    public void setHzInfo(String hzInfo) {
        this.hzInfo = hzInfo;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public String getJgLx() {
        return jgLx;
    }

    public void setJgLx(String jgLx) {
        this.jgLx = jgLx;
    }

    public String getJgLxMc() {
        return jgLxMc;
    }

    public void setJgLxMc(String jgLxMc) {
        this.jgLxMc = jgLxMc;
    }

    public String getGh() {
        return gh;
    }

    public void setGh(String gh) {
        this.gh = gh;
    }

    public String getJh() {
        return jh;
    }

    public void setJh(String jh) {
        this.jh = jh;
    }

    public String getBxgh() {
        return bxgh;
    }

    public void setBxgh(String bxgh) {
        this.bxgh = bxgh;
    }

    public String getBxghInfo() {
        return bxghInfo;
    }

    public void setBxghInfo(String bxghInfo) {
        this.bxghInfo = bxghInfo;
    }

    public String getCkZdy1() {
        return ckZdy1;
    }

    public void setCkZdy1(String ckZdy1) {
        this.ckZdy1 = ckZdy1;
    }

    public String getCkZdy2() {
        return ckZdy2;
    }

    public void setCkZdy2(String ckZdy2) {
        this.ckZdy2 = ckZdy2;
    }

    public String getCkZdy3() {
        return ckZdy3;
    }

    public void setCkZdy3(String ckZdy3) {
        this.ckZdy3 = ckZdy3;
    }

    public String getCkZdy4() {
        return ckZdy4;
    }

    public void setCkZdy4(String ckZdy4) {
        this.ckZdy4 = ckZdy4;
    }

    public String getCkZdy5() {
        return ckZdy5;
    }

    public void setCkZdy5(String ckZdy5) {
        this.ckZdy5 = ckZdy5;
    }

    public String getDyeingAdvice() {
        return dyeingAdvice;
    }

    public void setDyeingAdvice(String dyeingAdvice) {
        this.dyeingAdvice = dyeingAdvice;
    }

    public String getKcms() {
        return kcms;
    }

    public void setKcms(String kcms) {
        this.kcms = kcms;
    }

    public Integer getKc() {
        return kc;
    }

    public void setKc(Integer kc) {
        this.kc = kc;
    }

    public Integer getPs() {
        return ps;
    }

    public void setPs(Integer ps) {
        this.ps = ps;
    }

    public Double getZsl() {
        return zsl;
    }

    public void setZsl(Double zsl) {
        this.zsl = zsl;
    }

    public Double getZsl2() {
        return zsl2;
    }

    public void setZsl2(Double zsl2) {
        this.zsl2 = zsl2;
    }

    public Double getJjsl() {
        return jjsl;
    }

    public void setJjsl(Double jjsl) {
        this.jjsl = jjsl;
    }

    public Integer getPz() {
        return pz;
    }

    public void setPz(Integer pz) {
        this.pz = pz;
    }

    public Integer getCxps() {
        return cxps;
    }

    public void setCxps(Integer cxps) {
        this.cxps = cxps;
    }

    public Double getCxsl() {
        return cxsl;
    }

    public void setCxsl(Double cxsl) {
        this.cxsl = cxsl;
    }

    public Long getDwId() {
        return dwId;
    }

    public void setDwId(Long dwId) {
        this.dwId = dwId;
    }

    public Long getJjdwId() {
        return jjdwId;
    }

    public void setJjdwId(Long jjdwId) {
        this.jjdwId = jjdwId;
    }

    public Long getDw2Id() {
        return dw2Id;
    }

    public void setDw2Id(Long dw2Id) {
        this.dw2Id = dw2Id;
    }

    public BigDecimal getZydj() {
        return zydj;
    }

    public void setZydj(BigDecimal zydj) {
        this.zydj = zydj;
    }

    public BigDecimal getDj() {
        return dj;
    }

    public void setDj(BigDecimal dj) {
        this.dj = dj;
    }

    public BigDecimal getScdj() {
        return scdj;
    }

    public void setScdj(BigDecimal scdj) {
        this.scdj = scdj;
    }

    public BigDecimal getJe() {
        return je;
    }

    public void setJe(BigDecimal je) {
        this.je = je;
    }

    public BigDecimal getShuilv() {
        return shuilv;
    }

    public void setShuilv(BigDecimal shuilv) {
        this.shuilv = shuilv;
    }

    public BigDecimal getHsdj() {
        return hsdj;
    }

    public void setHsdj(BigDecimal hsdj) {
        this.hsdj = hsdj;
    }

    public BigDecimal getHsje() {
        return hsje;
    }

    public void setHsje(BigDecimal hsje) {
        this.hsje = hsje;
    }

    public BigDecimal getShuie() {
        return shuie;
    }

    public void setShuie(BigDecimal shuie) {
        this.shuie = shuie;
    }

    public BigDecimal getHszje() {
        return hszje;
    }

    public void setHszje(BigDecimal hszje) {
        this.hszje = hszje;
    }

    public BigDecimal getFyZdy1() {
        return fyZdy1;
    }

    public void setFyZdy1(BigDecimal fyZdy1) {
        this.fyZdy1 = fyZdy1;
    }

    public BigDecimal getFyZdy2() {
        return fyZdy2;
    }

    public void setFyZdy2(BigDecimal fyZdy2) {
        this.fyZdy2 = fyZdy2;
    }

    public BigDecimal getFyZdy3() {
        return fyZdy3;
    }

    public void setFyZdy3(BigDecimal fyZdy3) {
        this.fyZdy3 = fyZdy3;
    }

    public BigDecimal getFyZdy4() {
        return fyZdy4;
    }

    public void setFyZdy4(BigDecimal fyZdy4) {
        this.fyZdy4 = fyZdy4;
    }

    public BigDecimal getFyZdy5() {
        return fyZdy5;
    }

    public void setFyZdy5(BigDecimal fyZdy5) {
        this.fyZdy5 = fyZdy5;
    }

    public BigDecimal getZje() {
        return zje;
    }

    public void setZje(BigDecimal zje) {
        this.zje = zje;
    }

    public String getKdZdy1() {
        return kdZdy1;
    }

    public void setKdZdy1(String kdZdy1) {
        this.kdZdy1 = kdZdy1;
    }

    public String getKdZdy2() {
        return kdZdy2;
    }

    public void setKdZdy2(String kdZdy2) {
        this.kdZdy2 = kdZdy2;
    }

    public String getKdZdy3() {
        return kdZdy3;
    }

    public void setKdZdy3(String kdZdy3) {
        this.kdZdy3 = kdZdy3;
    }

    public String getKdZdy4() {
        return kdZdy4;
    }

    public void setKdZdy4(String kdZdy4) {
        this.kdZdy4 = kdZdy4;
    }

    public String getKdZdy5() {
        return kdZdy5;
    }

    public void setKdZdy5(String kdZdy5) {
        this.kdZdy5 = kdZdy5;
    }

    public Integer getSskc() {
        return sskc;
    }

    public void setSskc(Integer sskc) {
        this.sskc = sskc;
    }

    public Double getSssl() {
        return sssl;
    }

    public void setSssl(Double sssl) {
        this.sssl = sssl;
    }

    public BigDecimal getSsdj() {
        return ssdj;
    }

    public void setSsdj(BigDecimal ssdj) {
        this.ssdj = ssdj;
    }

    public BigDecimal getSsje() {
        return ssje;
    }

    public void setSsje(BigDecimal ssje) {
        this.ssje = ssje;
    }

    public BigDecimal getSszje() {
        return sszje;
    }

    public void setSszje(BigDecimal sszje) {
        this.sszje = sszje;
    }

    public Integer getYhk() {
        return yhk;
    }

    public void setYhk(Integer yhk) {
        this.yhk = yhk;
    }

    public Integer getSscs() {
        return sscs;
    }

    public void setSscs(Integer sscs) {
        this.sscs = sscs;
    }

    public String getSexi() {
        return sexi;
    }

    public void setSexi(String sexi) {
        this.sexi = sexi;
    }

    public String getDaSexi() {
        return daSexi;
    }

    public void setDaSexi(String daSexi) {
        this.daSexi = daSexi;
    }

    public Integer getJjcd() {
        return jjcd;
    }

    public void setJjcd(Integer jjcd) {
        this.jjcd = jjcd;
    }

    public String getYbdj() {
        return ybdj;
    }

    public void setYbdj(String ybdj) {
        this.ybdj = ybdj;
    }

    public String getYbBs() {
        return ybBs;
    }

    public void setYbBs(String ybBs) {
        this.ybBs = ybBs;
    }

    public String getRcjg() {
        return rcjg;
    }

    public void setRcjg(String rcjg) {
        this.rcjg = rcjg;
    }

    public String getZcjg() {
        return zcjg;
    }

    public void setZcjg(String zcjg) {
        this.zcjg = zcjg;
    }

    public Integer getYbFa() {
        return ybFa;
    }

    public void setYbFa(Integer ybFa) {
        this.ybFa = ybFa;
    }

    public String getPcxc() {
        return pcxc;
    }

    public void setPcxc(String pcxc) {
        this.pcxc = pcxc;
    }

    public String getYbFaMc() {
        return ybFaMc;
    }

    public void setYbFaMc(String ybFaMc) {
        this.ybFaMc = ybFaMc;
    }

    public String getHq() {
        return hq;
    }

    public void setHq(String hq) {
        this.hq = hq;
    }

    public Integer getHqts() {
        return hqts;
    }

    public void setHqts(Integer hqts) {
        this.hqts = hqts;
    }

    public String getCq() {
        return cq;
    }

    public void setCq(String cq) {
        this.cq = cq;
    }

    public Integer getCqts() {
        return cqts;
    }

    public void setCqts(Integer cqts) {
        this.cqts = cqts;
    }

    public Integer getCqsyts() {
        return cqsyts;
    }

    public void setCqsyts(Integer cqsyts) {
        this.cqsyts = cqsyts;
    }

    public String getTqyhts() {
        return tqyhts;
    }

    public void setTqyhts(String tqyhts) {
        this.tqyhts = tqyhts;
    }

    public String getSyts() {
        return syts;
    }

    public void setSyts(String syts) {
        this.syts = syts;
    }

    public String getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(String shareInfo) {
        this.shareInfo = shareInfo;
    }

    public Integer getWfps() {
        return wfps;
    }

    public void setWfps(Integer wfps) {
        this.wfps = wfps;
    }

    public Integer getJdps() {
        return jdps;
    }

    public void setJdps(Integer jdps) {
        this.jdps = jdps;
    }

    public Double getWfsl() {
        return wfsl;
    }

    public void setWfsl(Double wfsl) {
        this.wfsl = wfsl;
    }

    public Double getJdsl() {
        return jdsl;
    }

    public void setJdsl(Double jdsl) {
        this.jdsl = jdsl;
    }

    public Double getWcl() {
        return wcl;
    }

    public void setWcl(Double wcl) {
        this.wcl = wcl;
    }

    public Double getWcs() {
        return wcs;
    }

    public void setWcs(Double wcs) {
        this.wcs = wcs;
    }

    public String getWcp() {
        return wcp;
    }

    public void setWcp(String wcp) {
        this.wcp = wcp;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getFcsj() {
        return fcsj;
    }

    public void setFcsj(String fcsj) {
        this.fcsj = fcsj;
    }

    public Long getMddId() {
        return mddId;
    }

    public void setMddId(Long mddId) {
        this.mddId = mddId;
    }

    public String getMddMc() {
        return mddMc;
    }

    public void setMddMc(String mddMc) {
        this.mddMc = mddMc;
    }

    public String getReceivingUnit() {
        return receivingUnit;
    }

    public void setReceivingUnit(String receivingUnit) {
        this.receivingUnit = receivingUnit;
    }

    public Long getReceivedUnitId() {
        return receivedUnitId;
    }

    public void setReceivedUnitId(Long receivedUnitId) {
        this.receivedUnitId = receivedUnitId;
    }

    public Long getJhrId() {
        return jhrId;
    }

    public void setJhrId(Long jhrId) {
        this.jhrId = jhrId;
    }

    public String getJhrMc() {
        return jhrMc;
    }

    public void setJhrMc(String jhrMc) {
        this.jhrMc = jhrMc;
    }

    public Long getFzrId() {
        return fzrId;
    }

    public void setFzrId(Long fzrId) {
        this.fzrId = fzrId;
    }

    public String getCjqJdrMc() {
        return cjqJdrMc;
    }

    public void setCjqJdrMc(String cjqJdrMc) {
        this.cjqJdrMc = cjqJdrMc;
    }

    public Long getYwgdStaffId() {
        return ywgdStaffId;
    }

    public void setYwgdStaffId(Long ywgdStaffId) {
        this.ywgdStaffId = ywgdStaffId;
    }

    public String getsFd() {
        return sFd;
    }

    public void setsFd(String sFd) {
        this.sFd = sFd;
    }

    public String getZhlxMc() {
        return zhlxMc;
    }

    public void setZhlxMc(String zhlxMc) {
        this.zhlxMc = zhlxMc;
    }

    public Long getZhlxId() {
        return zhlxId;
    }

    public void setZhlxId(Long zhlxId) {
        this.zhlxId = zhlxId;
    }

    public String getGzlMc() {
        return gzlMc;
    }

    public void setGzlMc(String gzlMc) {
        this.gzlMc = gzlMc;
    }

    public Long getGzlId() {
        return gzlId;
    }

    public void setGzlId(Long gzlId) {
        this.gzlId = gzlId;
    }

    public String getGzl() {
        return gzl;
    }

    public void setGzl(String gzl) {
        this.gzl = gzl;
    }

    public Integer getCjPs() {
        return cjPs;
    }

    public void setCjPs(Integer cjPs) {
        this.cjPs = cjPs;
    }

    public Double getCjZsl() {
        return cjZsl;
    }

    public void setCjZsl(Double cjZsl) {
        this.cjZsl = cjZsl;
    }

    public Integer getCjDyPs() {
        return cjDyPs;
    }

    public void setCjDyPs(Integer cjDyPs) {
        this.cjDyPs = cjDyPs;
    }

    public Double getCjDyZsl() {
        return cjDyZsl;
    }

    public void setCjDyZsl(Double cjDyZsl) {
        this.cjDyZsl = cjDyZsl;
    }

    public String getCjKssj() {
        return cjKssj;
    }

    public void setCjKssj(String cjKssj) {
        this.cjKssj = cjKssj;
    }

    public String getCjJssj() {
        return cjJssj;
    }

    public void setCjJssj(String cjJssj) {
        this.cjJssj = cjJssj;
    }

    public Integer getCjGs() {
        return cjGs;
    }

    public void setCjGs(Integer cjGs) {
        this.cjGs = cjGs;
    }

    public String getCjzt() {
        return cjzt;
    }

    public void setCjzt(String cjzt) {
        this.cjzt = cjzt;
    }

    public Integer getCjWdyPs() {
        return cjWdyPs;
    }

    public void setCjWdyPs(Integer cjWdyPs) {
        this.cjWdyPs = cjWdyPs;
    }

    public Double getCjWdyZsl() {
        return cjWdyZsl;
    }

    public void setCjWdyZsl(Double cjWdyZsl) {
        this.cjWdyZsl = cjWdyZsl;
    }

    public Integer getCjWcjPs() {
        return cjWcjPs;
    }

    public void setCjWcjPs(Integer cjWcjPs) {
        this.cjWcjPs = cjWcjPs;
    }

    public Double getCjWcjZsl() {
        return cjWcjZsl;
    }

    public void setCjWcjZsl(Double cjWcjZsl) {
        this.cjWcjZsl = cjWcjZsl;
    }

    public Integer getRkps() {
        return rkps;
    }

    public void setRkps(Integer rkps) {
        this.rkps = rkps;
    }

    public Integer getCkps() {
        return ckps;
    }

    public void setCkps(Integer ckps) {
        this.ckps = ckps;
    }

    public Integer getTpps() {
        return tpps;
    }

    public void setTpps(Integer tpps) {
        this.tpps = tpps;
    }

    public Integer getThps() {
        return thps;
    }

    public void setThps(Integer thps) {
        this.thps = thps;
    }

    public Integer getDdkcp() {
        return ddkcp;
    }

    public void setDdkcp(Integer ddkcp) {
        this.ddkcp = ddkcp;
    }

    public Double getRksl() {
        return rksl;
    }

    public void setRksl(Double rksl) {
        this.rksl = rksl;
    }

    public Double getCksl() {
        return cksl;
    }

    public void setCksl(Double cksl) {
        this.cksl = cksl;
    }

    public Double getTpsl() {
        return tpsl;
    }

    public void setTpsl(Double tpsl) {
        this.tpsl = tpsl;
    }

    public Double getThsl() {
        return thsl;
    }

    public void setThsl(Double thsl) {
        this.thsl = thsl;
    }

    public Integer getDdkcs() {
        return ddkcs;
    }

    public void setDdkcs(Integer ddkcs) {
        this.ddkcs = ddkcs;
    }

    public Integer getJycd() {
        return jycd;
    }

    public void setJycd(Integer jycd) {
        this.jycd = jycd;
    }

    public Integer getJygj() {
        return jygj;
    }

    public void setJygj(Integer jygj) {
        this.jygj = jygj;
    }

    public Integer getZkCd() {
        return zkCd;
    }

    public void setZkCd(Integer zkCd) {
        this.zkCd = zkCd;
    }

    public Integer getZkZl() {
        return zkZl;
    }

    public void setZkZl(Integer zkZl) {
        this.zkZl = zkZl;
    }

    public Integer getYbKshCd() {
        return ybKshCd;
    }

    public void setYbKshCd(Integer ybKshCd) {
        this.ybKshCd = ybKshCd;
    }

    public Integer getWphPs() {
        return wphPs;
    }

    public void setWphPs(Integer wphPs) {
        this.wphPs = wphPs;
    }

    public Double getWphSl() {
        return wphSl;
    }

    public void setWphSl(Double wphSl) {
        this.wphSl = wphSl;
    }

    public Integer getYphPs() {
        return yphPs;
    }

    public void setYphPs(Integer yphPs) {
        this.yphPs = yphPs;
    }

    public Double getYphSl() {
        return yphSl;
    }

    public void setYphSl(Double yphSl) {
        this.yphSl = yphSl;
    }

    public Integer getCkWphPs() {
        return ckWphPs;
    }

    public void setCkWphPs(Integer ckWphPs) {
        this.ckWphPs = ckWphPs;
    }

    public Double getCkWphSl() {
        return ckWphSl;
    }

    public void setCkWphSl(Double ckWphSl) {
        this.ckWphSl = ckWphSl;
    }

    public Integer getCkYphPs() {
        return ckYphPs;
    }

    public void setCkYphPs(Integer ckYphPs) {
        this.ckYphPs = ckYphPs;
    }

    public Double getCkYphSl() {
        return ckYphSl;
    }

    public void setCkYphSl(Double ckYphSl) {
        this.ckYphSl = ckYphSl;
    }

    public Integer getDdDfpPs() {
        return ddDfpPs;
    }

    public void setDdDfpPs(Integer ddDfpPs) {
        this.ddDfpPs = ddDfpPs;
    }

    public Double getDdDfpSl() {
        return ddDfpSl;
    }

    public void setDdDfpSl(Double ddDfpSl) {
        this.ddDfpSl = ddDfpSl;
    }

    public String getKfhKey() {
        return kfhKey;
    }

    public void setKfhKey(String kfhKey) {
        this.kfhKey = kfhKey;
    }

    public Long getFhCkId() {
        return fhCkId;
    }

    public void setFhCkId(Long fhCkId) {
        this.fhCkId = fhCkId;
    }

    public String getFhCkMc() {
        return fhCkMc;
    }

    public void setFhCkMc(String fhCkMc) {
        this.fhCkMc = fhCkMc;
    }

    public String getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(String lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Integer getIsWfh() {
        return isWfh;
    }

    public void setIsWfh(Integer isWfh) {
        this.isWfh = isWfh;
    }

    public Integer getXzdPs() {
        return xzdPs;
    }

    public void setXzdPs(Integer xzdPs) {
        this.xzdPs = xzdPs;
    }

    public Double getXzdSl() {
        return xzdSl;
    }

    public void setXzdSl(Double xzdSl) {
        this.xzdSl = xzdSl;
    }

    public Integer getXzdLhps() {
        return xzdLhps;
    }

    public void setXzdLhps(Integer xzdLhps) {
        this.xzdLhps = xzdLhps;
    }

    public Double getXzdLhsl() {
        return xzdLhsl;
    }

    public void setXzdLhsl(Double xzdLhsl) {
        this.xzdLhsl = xzdLhsl;
    }

    public String getXzdWlhps() {
        return xzdWlhps;
    }

    public void setXzdWlhps(String xzdWlhps) {
        this.xzdWlhps = xzdWlhps;
    }

    public Double getXzdWlhsl() {
        return xzdWlhsl;
    }

    public void setXzdWlhsl(Double xzdWlhsl) {
        this.xzdWlhsl = xzdWlhsl;
    }

    public Integer getXzdWxzps() {
        return xzdWxzps;
    }

    public void setXzdWxzps(Integer xzdWxzps) {
        this.xzdWxzps = xzdWxzps;
    }

    public Double getXzdWxzsl() {
        return xzdWxzsl;
    }

    public void setXzdWxzsl(Double xzdWxzsl) {
        this.xzdWxzsl = xzdWxzsl;
    }

    public Integer getWwjgPs() {
        return wwjgPs;
    }

    public void setWwjgPs(Integer wwjgPs) {
        this.wwjgPs = wwjgPs;
    }

    public Integer getWwjgLhps() {
        return wwjgLhps;
    }

    public void setWwjgLhps(Integer wwjgLhps) {
        this.wwjgLhps = wwjgLhps;
    }

    public Integer getWwjgWlhps() {
        return wwjgWlhps;
    }

    public void setWwjgWlhps(Integer wwjgWlhps) {
        this.wwjgWlhps = wwjgWlhps;
    }

    public Integer getWwjgWjgps() {
        return wwjgWjgps;
    }

    public void setWwjgWjgps(Integer wwjgWjgps) {
        this.wwjgWjgps = wwjgWjgps;
    }

    public Double getWwjgSl() {
        return wwjgSl;
    }

    public void setWwjgSl(Double wwjgSl) {
        this.wwjgSl = wwjgSl;
    }

    public Double getWwjgLhsl() {
        return wwjgLhsl;
    }

    public void setWwjgLhsl(Double wwjgLhsl) {
        this.wwjgLhsl = wwjgLhsl;
    }

    public Double getWwjgWlhsl() {
        return wwjgWlhsl;
    }

    public void setWwjgWlhsl(Double wwjgWlhsl) {
        this.wwjgWlhsl = wwjgWlhsl;
    }

    public Double getWwjgWjgsl() {
        return wwjgWjgsl;
    }

    public void setWwjgWjgsl(Double wwjgWjgsl) {
        this.wwjgWjgsl = wwjgWjgsl;
    }

    public Integer getPbcgPs() {
        return pbcgPs;
    }

    public void setPbcgPs(Integer pbcgPs) {
        this.pbcgPs = pbcgPs;
    }

    public Integer getPbcgLhps() {
        return pbcgLhps;
    }

    public void setPbcgLhps(Integer pbcgLhps) {
        this.pbcgLhps = pbcgLhps;
    }

    public Integer getPbcgWlhps() {
        return pbcgWlhps;
    }

    public void setPbcgWlhps(Integer pbcgWlhps) {
        this.pbcgWlhps = pbcgWlhps;
    }

    public Integer getPbcgWcgps() {
        return pbcgWcgps;
    }

    public void setPbcgWcgps(Integer pbcgWcgps) {
        this.pbcgWcgps = pbcgWcgps;
    }

    public Double getPbcgSl() {
        return pbcgSl;
    }

    public void setPbcgSl(Double pbcgSl) {
        this.pbcgSl = pbcgSl;
    }

    public Double getPbcgLhsl() {
        return pbcgLhsl;
    }

    public void setPbcgLhsl(Double pbcgLhsl) {
        this.pbcgLhsl = pbcgLhsl;
    }

    public Double getPbcgWlhsl() {
        return pbcgWlhsl;
    }

    public void setPbcgWlhsl(Double pbcgWlhsl) {
        this.pbcgWlhsl = pbcgWlhsl;
    }

    public Double getPbcgWcgsl() {
        return pbcgWcgsl;
    }

    public void setPbcgWcgsl(Double pbcgWcgsl) {
        this.pbcgWcgsl = pbcgWcgsl;
    }

    public Integer getCpcgPs() {
        return cpcgPs;
    }

    public void setCpcgPs(Integer cpcgPs) {
        this.cpcgPs = cpcgPs;
    }

    public Integer getCpcgLhps() {
        return cpcgLhps;
    }

    public void setCpcgLhps(Integer cpcgLhps) {
        this.cpcgLhps = cpcgLhps;
    }

    public Integer getCpcgWlhps() {
        return cpcgWlhps;
    }

    public void setCpcgWlhps(Integer cpcgWlhps) {
        this.cpcgWlhps = cpcgWlhps;
    }

    public Integer getCpcgWcgps() {
        return cpcgWcgps;
    }

    public void setCpcgWcgps(Integer cpcgWcgps) {
        this.cpcgWcgps = cpcgWcgps;
    }

    public Double getCpcgSl() {
        return cpcgSl;
    }

    public void setCpcgSl(Double cpcgSl) {
        this.cpcgSl = cpcgSl;
    }

    public Double getCpcgLhsl() {
        return cpcgLhsl;
    }

    public void setCpcgLhsl(Double cpcgLhsl) {
        this.cpcgLhsl = cpcgLhsl;
    }

    public Double getCpcgWlhsl() {
        return cpcgWlhsl;
    }

    public void setCpcgWlhsl(Double cpcgWlhsl) {
        this.cpcgWlhsl = cpcgWlhsl;
    }

    public Double getCpcgWcgsl() {
        return cpcgWcgsl;
    }

    public void setCpcgWcgsl(Double cpcgWcgsl) {
        this.cpcgWcgsl = cpcgWcgsl;
    }

    public String getZhfhrq() {
        return zhfhrq;
    }

    public void setZhfhrq(String zhfhrq) {
        this.zhfhrq = zhfhrq;
    }

    public String getHtbh() {
        return htbh;
    }

    public void setHtbh(String htbh) {
        this.htbh = htbh;
    }

    public Integer getHtzt() {
        return htzt;
    }

    public void setHtzt(Integer htzt) {
        this.htzt = htzt;
    }

    public String getQsflxrmc() {
        return qsflxrmc;
    }

    public void setQsflxrmc(String qsflxrmc) {
        this.qsflxrmc = qsflxrmc;
    }

    public String getFqflxrmc() {
        return fqflxrmc;
    }

    public void setFqflxrmc(String fqflxrmc) {
        this.fqflxrmc = fqflxrmc;
    }

    public String getQsjzrq() {
        return qsjzrq;
    }

    public void setQsjzrq(String qsjzrq) {
        this.qsjzrq = qsjzrq;
    }

    public Long getHtId() {
        return htId;
    }

    public void setHtId(Long htId) {
        this.htId = htId;
    }

    public Integer getFqflx() {
        return fqflx;
    }

    public void setFqflx(Integer fqflx) {
        this.fqflx = fqflx;
    }

    public String getFqfsjh() {
        return fqfsjh;
    }

    public void setFqfsjh(String fqfsjh) {
        this.fqfsjh = fqfsjh;
    }

    public Integer getQsflx() {
        return qsflx;
    }

    public void setQsflx(Integer qsflx) {
        this.qsflx = qsflx;
    }

    public String getQsfsjh() {
        return qsfsjh;
    }

    public void setQsfsjh(String qsfsjh) {
        this.qsfsjh = qsfsjh;
    }

    public String getHtztMc() {
        return htztMc;
    }

    public void setHtztMc(String htztMc) {
        this.htztMc = htztMc;
    }

    public Integer getFujian() {
        return fujian;
    }

    public void setFujian(Integer fujian) {
        this.fujian = fujian;
    }

    public String getXzgy() {
        return xzgy;
    }

    public void setXzgy(String xzgy) {
        this.xzgy = xzgy;
    }

    public List<Map> getXzyq() {
        return xzyq;
    }

    public void setXzyq(List<Map> xzyq) {
        this.xzyq = xzyq;
    }

    public BigDecimal getSjfhJe() {
        return sjfhJe;
    }

    public void setSjfhJe(BigDecimal sjfhJe) {
        this.sjfhJe = sjfhJe;
    }

    public BigDecimal getSjfhZje() {
        return sjfhZje;
    }

    public void setSjfhZje(BigDecimal sjfhZje) {
        this.sjfhZje = sjfhZje;
    }

    public BigDecimal getSjfhHsje() {
        return sjfhHsje;
    }

    public void setSjfhHsje(BigDecimal sjfhHsje) {
        this.sjfhHsje = sjfhHsje;
    }

    public BigDecimal getSjfhHszje() {
        return sjfhHszje;
    }

    public void setSjfhHszje(BigDecimal sjfhHszje) {
        this.sjfhHszje = sjfhHszje;
    }

    public BigDecimal getSjfhSsje() {
        return sjfhSsje;
    }

    public void setSjfhSsje(BigDecimal sjfhSsje) {
        this.sjfhSsje = sjfhSsje;
    }

    public BigDecimal getSjfhSszje() {
        return sjfhSszje;
    }

    public void setSjfhSszje(BigDecimal sjfhSszje) {
        this.sjfhSszje = sjfhSszje;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStyleNumber() {
        return styleNumber;
    }

    public void setStyleNumber(String styleNumber) {
        this.styleNumber = styleNumber;
    }

    public Long getQuarterId() {
        return quarterId;
    }

    public void setQuarterId(Long quarterId) {
        this.quarterId = quarterId;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public Integer getIsTsdp() {
        return isTsdp;
    }

    public void setIsTsdp(Integer isTsdp) {
        this.isTsdp = isTsdp;
    }

    public Long getLxrId() {
        return lxrId;
    }

    public void setLxrId(Long lxrId) {
        this.lxrId = lxrId;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public Integer getPcPs() {
        return pcPs;
    }

    public void setPcPs(Integer pcPs) {
        this.pcPs = pcPs;
    }

    public Double getPcSl() {
        return pcSl;
    }

    public void setPcSl(Double pcSl) {
        this.pcSl = pcSl;
    }

    public Integer getWpcPs() {
        return wpcPs;
    }

    public void setWpcPs(Integer wpcPs) {
        this.wpcPs = wpcPs;
    }

    public Double getWpcSl() {
        return wpcSl;
    }

    public void setWpcSl(Double wpcSl) {
        this.wpcSl = wpcSl;
    }

    public String getJlLy() {
        return jlLy;
    }

    public void setJlLy(String jlLy) {
        this.jlLy = jlLy;
    }

    public String getDsgh() {
        return dsgh;
    }

    public void setDsgh(String dsgh) {
        this.dsgh = dsgh;
    }

    public Long getKdId() {
        return kdId;
    }

    public void setKdId(Long kdId) {
        this.kdId = kdId;
    }

    public Double getHssl() {
        return hssl;
    }

    public void setHssl(Double hssl) {
        this.hssl = hssl;
    }

    public Integer getIsHsyc() {
        return isHsyc;
    }

    public void setIsHsyc(Integer isHsyc) {
        this.isHsyc = isHsyc;
    }

    public String getPlatformDeliverNo() {
        return platformDeliverNo;
    }

    public void setPlatformDeliverNo(String platformDeliverNo) {
        this.platformDeliverNo = platformDeliverNo;
    }

    public Integer getPlatformIsReceipt() {
        return platformIsReceipt;
    }

    public void setPlatformIsReceipt(Integer platformIsReceipt) {
        this.platformIsReceipt = platformIsReceipt;
    }

    public String getFpMc() {
        return fpMc;
    }

    public void setFpMc(String fpMc) {
        this.fpMc = fpMc;
    }

    public String getKpgsMc() {
        return kpgsMc;
    }

    public void setKpgsMc(String kpgsMc) {
        this.kpgsMc = kpgsMc;
    }

    public String getWldwQuyu() {
        return wldwQuyu;
    }

    public void setWldwQuyu(String wldwQuyu) {
        this.wldwQuyu = wldwQuyu;
    }

    public String getHeyanQuyu() {
        return heyanQuyu;
    }

    public void setHeyanQuyu(String heyanQuyu) {
        this.heyanQuyu = heyanQuyu;
    }

    public String getCwMc() {
        return cwMc;
    }

    public void setCwMc(String cwMc) {
        this.cwMc = cwMc;
    }

    public Long getCwId() {
        return cwId;
    }

    public void setCwId(Long cwId) {
        this.cwId = cwId;
    }

    public String getGroupStr() {
        return groupStr;
    }

    public void setGroupStr(String groupStr) {
        this.groupStr = groupStr;
    }

    public String getJkfsMc() {
        return jkfsMc;
    }

    public void setJkfsMc(String jkfsMc) {
        this.jkfsMc = jkfsMc;
    }

    public String getWldwLb() {
        return wldwLb;
    }

    public void setWldwLb(String wldwLb) {
        this.wldwLb = wldwLb;
    }

    public String getYwyMc() {
        return ywyMc;
    }

    public void setYwyMc(String ywyMc) {
        this.ywyMc = ywyMc;
    }

    public String getDjYwyMc() {
        return djYwyMc;
    }

    public void setDjYwyMc(String djYwyMc) {
        this.djYwyMc = djYwyMc;
    }

    public String getJsrMc() {
        return jsrMc;
    }

    public void setJsrMc(String jsrMc) {
        this.jsrMc = jsrMc;
    }

    public String getYwgdStaffMc() {
        return ywgdStaffMc;
    }

    public void setYwgdStaffMc(String ywgdStaffMc) {
        this.ywgdStaffMc = ywgdStaffMc;
    }

    public String getZdrMc() {
        return zdrMc;
    }

    public void setZdrMc(String zdrMc) {
        this.zdrMc = zdrMc;
    }

    public String getFhrMc() {
        return fhrMc;
    }

    public void setFhrMc(String fhrMc) {
        this.fhrMc = fhrMc;
    }

    public String getJdrMc() {
        return jdrMc;
    }

    public void setJdrMc(String jdrMc) {
        this.jdrMc = jdrMc;
    }

    public String getCpLb() {
        return cpLb;
    }

    public void setCpLb(String cpLb) {
        this.cpLb = cpLb;
    }

    public String getZdwMc() {
        return zdwMc;
    }

    public void setZdwMc(String zdwMc) {
        this.zdwMc = zdwMc;
    }

    public String getZjjdwMc() {
        return zjjdwMc;
    }

    public void setZjjdwMc(String zjjdwMc) {
        this.zjjdwMc = zjjdwMc;
    }

    public String getDwMc() {
        return dwMc;
    }

    public void setDwMc(String dwMc) {
        this.dwMc = dwMc;
    }

    public String getJjdwMc() {
        return jjdwMc;
    }

    public void setJjdwMc(String jjdwMc) {
        this.jjdwMc = jjdwMc;
    }

    public String getDw2Mc() {
        return dw2Mc;
    }

    public void setDw2Mc(String dw2Mc) {
        this.dw2Mc = dw2Mc;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getQxrMc() {
        return qxrMc;
    }

    public void setQxrMc(String qxrMc) {
        this.qxrMc = qxrMc;
    }

    public String getFzrMc() {
        return fzrMc;
    }

    public void setFzrMc(String fzrMc) {
        this.fzrMc = fzrMc;
    }

    public String getFzxxWldwMc() {
        return fzxxWldwMc;
    }

    public void setFzxxWldwMc(String fzxxWldwMc) {
        this.fzxxWldwMc = fzxxWldwMc;
    }

    public String getQxYy() {
        return qxYy;
    }

    public void setQxYy(String qxYy) {
        this.qxYy = qxYy;
    }

    public String getColorType() {
        return colorType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

    public String getDaColorType() {
        return daColorType;
    }

    public void setDaColorType(String daColorType) {
        this.daColorType = daColorType;
    }

    public String getJjcdMc() {
        return jjcdMc;
    }

    public void setJjcdMc(String jjcdMc) {
        this.jjcdMc = jjcdMc;
    }

    public String getJjcdColour() {
        return jjcdColour;
    }

    public void setJjcdColour(String jjcdColour) {
        this.jjcdColour = jjcdColour;
    }

    public String getThfsAddress() {
        return thfsAddress;
    }

    public void setThfsAddress(String thfsAddress) {
        this.thfsAddress = thfsAddress;
    }

    public String getThfsLxr() {
        return thfsLxr;
    }

    public void setThfsLxr(String thfsLxr) {
        this.thfsLxr = thfsLxr;
    }

    public String getThfsPhone() {
        return thfsPhone;
    }

    public void setThfsPhone(String thfsPhone) {
        this.thfsPhone = thfsPhone;
    }

    public Integer getIsSelfPick() {
        return isSelfPick;
    }

    public void setIsSelfPick(Integer isSelfPick) {
        this.isSelfPick = isSelfPick;
    }

    public String getThfs() {
        return thfs;
    }

    public void setThfs(String thfs) {
        this.thfs = thfs;
    }

    public String getDriverHm() {
        return driverHm;
    }

    public void setDriverHm(String driverHm) {
        this.driverHm = driverHm;
    }

    public String getXzdHq() {
        return xzdHq;
    }

    public void setXzdHq(String xzdHq) {
        this.xzdHq = xzdHq;
    }

    public String getXzdWldwMc() {
        return xzdWldwMc;
    }

    public void setXzdWldwMc(String xzdWldwMc) {
        this.xzdWldwMc = xzdWldwMc;
    }

    public String getXzdKdsj() {
        return xzdKdsj;
    }

    public void setXzdKdsj(String xzdKdsj) {
        this.xzdKdsj = xzdKdsj;
    }

    public String getXzdKssj() {
        return xzdKssj;
    }

    public void setXzdKssj(String xzdKssj) {
        this.xzdKssj = xzdKssj;
    }

    public String getXzdGs() {
        return xzdGs;
    }

    public void setXzdGs(String xzdGs) {
        this.xzdGs = xzdGs;
    }

    public String getPbcgHq() {
        return pbcgHq;
    }

    public void setPbcgHq(String pbcgHq) {
        this.pbcgHq = pbcgHq;
    }

    public String getPbcgWldwMc() {
        return pbcgWldwMc;
    }

    public void setPbcgWldwMc(String pbcgWldwMc) {
        this.pbcgWldwMc = pbcgWldwMc;
    }

    public String getPbcgKssj() {
        return pbcgKssj;
    }

    public void setPbcgKssj(String pbcgKssj) {
        this.pbcgKssj = pbcgKssj;
    }

    public String getPbcgKdsj() {
        return pbcgKdsj;
    }

    public void setPbcgKdsj(String pbcgKdsj) {
        this.pbcgKdsj = pbcgKdsj;
    }

    public String getPbcgGs() {
        return pbcgGs;
    }

    public void setPbcgGs(String pbcgGs) {
        this.pbcgGs = pbcgGs;
    }

    public String getCpcgHq() {
        return cpcgHq;
    }

    public void setCpcgHq(String cpcgHq) {
        this.cpcgHq = cpcgHq;
    }

    public String getCpcgWldwMc() {
        return cpcgWldwMc;
    }

    public void setCpcgWldwMc(String cpcgWldwMc) {
        this.cpcgWldwMc = cpcgWldwMc;
    }

    public String getCpcgKssj() {
        return cpcgKssj;
    }

    public void setCpcgKssj(String cpcgKssj) {
        this.cpcgKssj = cpcgKssj;
    }

    public String getCpcgKdsj() {
        return cpcgKdsj;
    }

    public void setCpcgKdsj(String cpcgKdsj) {
        this.cpcgKdsj = cpcgKdsj;
    }

    public String getCpcgGs() {
        return cpcgGs;
    }

    public void setCpcgGs(String cpcgGs) {
        this.cpcgGs = cpcgGs;
    }

    public String getWwjgHq() {
        return wwjgHq;
    }

    public void setWwjgHq(String wwjgHq) {
        this.wwjgHq = wwjgHq;
    }

    public Long getWwjgWldwId() {
        return wwjgWldwId;
    }

    public void setWwjgWldwId(Long wwjgWldwId) {
        this.wwjgWldwId = wwjgWldwId;
    }

    public String getWwjgWldwMc() {
        return wwjgWldwMc;
    }

    public void setWwjgWldwMc(String wwjgWldwMc) {
        this.wwjgWldwMc = wwjgWldwMc;
    }

    public String getWwjgKssj() {
        return wwjgKssj;
    }

    public void setWwjgKssj(String wwjgKssj) {
        this.wwjgKssj = wwjgKssj;
    }

    public String getWwjgKdsj() {
        return wwjgKdsj;
    }

    public void setWwjgKdsj(String wwjgKdsj) {
        this.wwjgKdsj = wwjgKdsj;
    }

    public String getWwjgGs() {
        return wwjgGs;
    }

    public void setWwjgGs(String wwjgGs) {
        this.wwjgGs = wwjgGs;
    }

    public Integer getIsYb() {
        return isYb;
    }

    public void setIsYb(Integer isYb) {
        this.isYb = isYb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGxlxMc() {
        return gxlxMc;
    }

    public void setGxlxMc(String gxlxMc) {
        this.gxlxMc = gxlxMc;
    }

    public Integer getJglx6Px() {
        return jglx6Px;
    }

    public void setJglx6Px(Integer jglx6Px) {
        this.jglx6Px = jglx6Px;
    }

    public String getJglx6Gyyq() {
        return jglx6Gyyq;
    }

    public void setJglx6Gyyq(String jglx6Gyyq) {
        this.jglx6Gyyq = jglx6Gyyq;
    }

    public String getJglx6Rzgy() {
        return jglx6Rzgy;
    }

    public void setJglx6Rzgy(String jglx6Rzgy) {
        this.jglx6Rzgy = jglx6Rzgy;
    }

    public JSONArray getJglx6Rzyq() {
        return jglx6Rzyq;
    }

    public void setJglx6Rzyq(JSONArray jglx6Rzyq) {
        this.jglx6Rzyq = jglx6Rzyq;
    }

    public Integer getJglx6IsYb() {
        return jglx6IsYb;
    }

    public void setJglx6IsYb(Integer jglx6IsYb) {
        this.jglx6IsYb = jglx6IsYb;
    }

    public String getJglx60Rzgy() {
        return jglx60Rzgy;
    }

    public void setJglx60Rzgy(String jglx60Rzgy) {
        this.jglx60Rzgy = jglx60Rzgy;
    }

    public JSONArray getJglx60Rzyq() {
        return jglx60Rzyq;
    }

    public void setJglx60Rzyq(JSONArray jglx60Rzyq) {
        this.jglx60Rzyq = jglx60Rzyq;
    }

    public Long getJglx6JgsId() {
        return jglx6JgsId;
    }

    public void setJglx6JgsId(Long jglx6JgsId) {
        this.jglx6JgsId = jglx6JgsId;
    }

    public String getJglx6JgsMc() {
        return jglx6JgsMc;
    }

    public void setJglx6JgsMc(String jglx6JgsMc) {
        this.jglx6JgsMc = jglx6JgsMc;
    }

    public JSONArray getJglx6GxInfo() {
        return jglx6GxInfo;
    }

    public void setJglx6GxInfo(JSONArray jglx6GxInfo) {
        this.jglx6GxInfo = jglx6GxInfo;
    }

    public Long getJglx6GxlxId() {
        return jglx6GxlxId;
    }

    public void setJglx6GxlxId(Long jglx6GxlxId) {
        this.jglx6GxlxId = jglx6GxlxId;
    }

    public Long getJglx6JglcId() {
        return jglx6JglcId;
    }

    public void setJglx6JglcId(Long jglx6JglcId) {
        this.jglx6JglcId = jglx6JglcId;
    }

    public Integer getJglx6WwjgPs() {
        return jglx6WwjgPs;
    }

    public void setJglx6WwjgPs(Integer jglx6WwjgPs) {
        this.jglx6WwjgPs = jglx6WwjgPs;
    }

    public Double getJglx6WwjgSl() {
        return jglx6WwjgSl;
    }

    public void setJglx6WwjgSl(Double jglx6WwjgSl) {
        this.jglx6WwjgSl = jglx6WwjgSl;
    }

    public Integer getJglx6WwjgLhps() {
        return jglx6WwjgLhps;
    }

    public void setJglx6WwjgLhps(Integer jglx6WwjgLhps) {
        this.jglx6WwjgLhps = jglx6WwjgLhps;
    }

    public Double getJglx6WwjgLhsl() {
        return jglx6WwjgLhsl;
    }

    public void setJglx6WwjgLhsl(Double jglx6WwjgLhsl) {
        this.jglx6WwjgLhsl = jglx6WwjgLhsl;
    }

    public Integer getJglx6WwjgWjgps() {
        return jglx6WwjgWjgps;
    }

    public void setJglx6WwjgWjgps(Integer jglx6WwjgWjgps) {
        this.jglx6WwjgWjgps = jglx6WwjgWjgps;
    }

    public Double getJglx6WwjgWjgsl() {
        return jglx6WwjgWjgsl;
    }

    public void setJglx6WwjgWjgsl(Double jglx6WwjgWjgsl) {
        this.jglx6WwjgWjgsl = jglx6WwjgWjgsl;
    }

    public Integer getJglx6WwjgWlhps() {
        return jglx6WwjgWlhps;
    }

    public void setJglx6WwjgWlhps(Integer jglx6WwjgWlhps) {
        this.jglx6WwjgWlhps = jglx6WwjgWlhps;
    }

    public Double getJglx6WwjgWlhsl() {
        return jglx6WwjgWlhsl;
    }

    public void setJglx6WwjgWlhsl(Double jglx6WwjgWlhsl) {
        this.jglx6WwjgWlhsl = jglx6WwjgWlhsl;
    }

    public String getJglx6WwjgKssj() {
        return jglx6WwjgKssj;
    }

    public void setJglx6WwjgKssj(String jglx6WwjgKssj) {
        this.jglx6WwjgKssj = jglx6WwjgKssj;
    }

    public String getJglx6WwjgKdsj() {
        return jglx6WwjgKdsj;
    }

    public void setJglx6WwjgKdsj(String jglx6WwjgKdsj) {
        this.jglx6WwjgKdsj = jglx6WwjgKdsj;
    }

    public String getJglx6WwjgGs() {
        return jglx6WwjgGs;
    }

    public void setJglx6WwjgGs(String jglx6WwjgGs) {
        this.jglx6WwjgGs = jglx6WwjgGs;
    }

    public String getJglx6WwjgHq() {
        return jglx6WwjgHq;
    }

    public void setJglx6WwjgHq(String jglx6WwjgHq) {
        this.jglx6WwjgHq = jglx6WwjgHq;
    }

    public Long getJglx6WwjgWldwId() {
        return jglx6WwjgWldwId;
    }

    public void setJglx6WwjgWldwId(Long jglx6WwjgWldwId) {
        this.jglx6WwjgWldwId = jglx6WwjgWldwId;
    }

    public String getJglx6WwjgWldwMc() {
        return jglx6WwjgWldwMc;
    }

    public void setJglx6WwjgWldwMc(String jglx6WwjgWldwMc) {
        this.jglx6WwjgWldwMc = jglx6WwjgWldwMc;
    }

    public JSONArray getDdType() {
        return ddType;
    }

    public void setDdType(JSONArray ddType) {
        this.ddType = ddType;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public Long getZtId() {
        return ztId;
    }

    public void setZtId(Long ztId) {
        this.ztId = ztId;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getZtgxsj() {
        return ztgxsj;
    }

    public void setZtgxsj(String ztgxsj) {
        this.ztgxsj = ztgxsj;
    }

    public JSONArray getJjcdType() {
        return jjcdType;
    }

    public void setJjcdType(JSONArray jjcdType) {
        this.jjcdType = jjcdType;
    }

    public String getDyDh() {
        return dyDh;
    }

    public void setDyDh(String dyDh) {
        this.dyDh = dyDh;
    }

    public Long getDyDdDjId() {
        return dyDdDjId;
    }

    public void setDyDdDjId(Long dyDdDjId) {
        this.dyDdDjId = dyDdDjId;
    }

    public Long getDyDdJlId() {
        return dyDdJlId;
    }

    public void setDyDdJlId(Long dyDdJlId) {
        this.dyDdJlId = dyDdJlId;
    }

    public String getDyZt() {
        return dyZt;
    }

    public void setDyZt(String dyZt) {
        this.dyZt = dyZt;
    }

    public Long getDyZtId() {
        return dyZtId;
    }

    public void setDyZtId(Long dyZtId) {
        this.dyZtId = dyZtId;
    }

    public String getDyZtColour() {
        return dyZtColour;
    }

    public void setDyZtColour(String dyZtColour) {
        this.dyZtColour = dyZtColour;
    }

    public String getDhyDh() {
        return dhyDh;
    }

    public void setDhyDh(String dhyDh) {
        this.dhyDh = dhyDh;
    }

    public Long getDhyDdDjId() {
        return dhyDdDjId;
    }

    public void setDhyDdDjId(Long dhyDdDjId) {
        this.dhyDdDjId = dhyDdDjId;
    }

    public Long getDhyDdJlId() {
        return dhyDdJlId;
    }

    public void setDhyDdJlId(Long dhyDdJlId) {
        this.dhyDdJlId = dhyDdJlId;
    }

    public String getDhyZt() {
        return dhyZt;
    }

    public void setDhyZt(String dhyZt) {
        this.dhyZt = dhyZt;
    }

    public Long getDhyZtId() {
        return dhyZtId;
    }

    public void setDhyZtId(Long dhyZtId) {
        this.dhyZtId = dhyZtId;
    }

    public String getDhyZtColour() {
        return dhyZtColour;
    }

    public void setDhyZtColour(String dhyZtColour) {
        this.dhyZtColour = dhyZtColour;
    }

    public String getJd() {
        return jd;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }

    public JSONArray getYbBsList() {
        return ybBsList;
    }

    public void setYbBsList(JSONArray ybBsList) {
        this.ybBsList = ybBsList;
    }

    public JSONArray getRcjgList() {
        return rcjgList;
    }

    public void setRcjgList(JSONArray rcjgList) {
        this.rcjgList = rcjgList;
    }

    public JSONArray getZcjgList() {
        return zcjgList;
    }

    public void setZcjgList(JSONArray zcjgList) {
        this.zcjgList = zcjgList;
    }

    public String getJxzJd() {
        return jxzJd;
    }

    public void setJxzJd(String jxzJd) {
        this.jxzJd = jxzJd;
    }

    public String getQqJd() {
        return qqJd;
    }

    public void setQqJd(String qqJd) {
        this.qqJd = qqJd;
    }

    public String getGzlJd() {
        return gzlJd;
    }

    public void setGzlJd(String gzlJd) {
        this.gzlJd = gzlJd;
    }

    public List<Map> getMaitou() {
        return maitou;
    }

    public void setMaitou(List<Map> maitou) {
        this.maitou = maitou;
    }

    public Integer getDjPs() {
        return djPs;
    }

    public void setDjPs(Integer djPs) {
        this.djPs = djPs;
    }

    public Double getDjZsl() {
        return djZsl;
    }

    public void setDjZsl(Double djZsl) {
        this.djZsl = djZsl;
    }

    public BigDecimal getDjJe() {
        return djJe;
    }

    public void setDjJe(BigDecimal djJe) {
        this.djJe = djJe;
    }

    public Integer getDjWfps() {
        return djWfps;
    }

    public void setDjWfps(Integer djWfps) {
        this.djWfps = djWfps;
    }

    public Double getDjWfsl() {
        return djWfsl;
    }

    public void setDjWfsl(Double djWfsl) {
        this.djWfsl = djWfsl;
    }

    public Integer getDjFhps() {
        return djFhps;
    }

    public void setDjFhps(Integer djFhps) {
        this.djFhps = djFhps;
    }

    public Double getDjFhsl() {
        return djFhsl;
    }

    public void setDjFhsl(Double djFhsl) {
        this.djFhsl = djFhsl;
    }

    public Integer getDjPbcgWcgps() {
        return djPbcgWcgps;
    }

    public void setDjPbcgWcgps(Integer djPbcgWcgps) {
        this.djPbcgWcgps = djPbcgWcgps;
    }

    public Double getDjPbcgWcgsl() {
        return djPbcgWcgsl;
    }

    public void setDjPbcgWcgsl(Double djPbcgWcgsl) {
        this.djPbcgWcgsl = djPbcgWcgsl;
    }

    public Integer getDjCpcgWcgps() {
        return djCpcgWcgps;
    }

    public void setDjCpcgWcgps(Integer djCpcgWcgps) {
        this.djCpcgWcgps = djCpcgWcgps;
    }

    public Double getDjCpcgWcgsl() {
        return djCpcgWcgsl;
    }

    public void setDjCpcgWcgsl(Double djCpcgWcgsl) {
        this.djCpcgWcgsl = djCpcgWcgsl;
    }

    public Integer getJgZlhps() {
        return jgZlhps;
    }

    public void setJgZlhps(Integer jgZlhps) {
        this.jgZlhps = jgZlhps;
    }

    public Double getJgZlhsl() {
        return jgZlhsl;
    }

    public void setJgZlhsl(Double jgZlhsl) {
        this.jgZlhsl = jgZlhsl;
    }

    public BigDecimal getYsdj() {
        return ysdj;
    }

    public void setYsdj(BigDecimal ysdj) {
        this.ysdj = ysdj;
    }

    public Long getOaXtsqId() {
        return oaXtsqId;
    }

    public void setOaXtsqId(Long oaXtsqId) {
        this.oaXtsqId = oaXtsqId;
    }

    public String getCpLxMc() {
        return cpLxMc;
    }

    public void setCpLxMc(String cpLxMc) {
        this.cpLxMc = cpLxMc;
    }

    public String getCpTotalDescription() {
        return cpTotalDescription;
    }

    public void setCpTotalDescription(String cpTotalDescription) {
        this.cpTotalDescription = cpTotalDescription;
    }

    @JsonProperty("cp_cj_description")
    private String cpCjDescription;

    @JsonProperty("cp_total_description")
    private String cpTotalDescription;












}
