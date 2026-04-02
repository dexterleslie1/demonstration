package com.future.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SaleCustomerOrderFhslResponseVo {

    @JsonProperty("sale_customer_order_goods_id")
    private Long saleCustomerOrderGoodsId;

    public Double getFhsl() {
        return fhsl;
    }

    public void setFhsl(Double fhsl) {
        this.fhsl = fhsl;
    }

    @Schema(description = "发货数")
    @JsonProperty("fhsl")
    private Double fhsl;

    public Integer getFhps() {
        return fhps;
    }

    public void setFhps(Integer fhps) {
        this.fhps = fhps;
    }

    @Schema(description = "发货匹")
    @JsonProperty("fhps")
    private Integer fhps;

    @JsonProperty("is_jd")
    private Integer isJd;

    @Schema(description = "结单日期")
    @JsonProperty("jd_sj")
    private String jdSj;

    @JsonProperty("jdr_id")
    private Long jdrId;

    @Schema(description = "退货数")
    @JsonProperty("thsl")
    private Double thsl;

    @Schema(description = "退货匹")
    @JsonProperty("thps")
    private Integer thps;

    @Schema(description = "未发数")
    @JsonProperty("wfsl")
    private Double wfsl;

    @Schema(description = "未发匹")
    @JsonProperty("wfps")
    private Integer wfps;

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



    @Schema(description = "单据发货数量")
    @JsonProperty("dj_fhsl")
    private Double djFhsl;

    @Schema(description = "单据发货匹数")
    @JsonProperty("dj_fhps")
    private Integer djFhps;

    @Schema(description = "进度")
    @JsonProperty("jd")
    private String jd;



    @Schema(description = "订单未配匹")
    @JsonProperty("wph_ps")
    private Integer wphPs;

    @Schema(description = "订单未配数")
    @JsonProperty("wph_sl")
    private Double wphSl;


    @Schema(description = "进行中进度")
    @JsonProperty("jxz_jd")
    private String jxzJd;

    @Schema(description = "完成%")
    @JsonProperty("wcl")
    private Double wcl;




}
