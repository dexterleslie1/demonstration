package com.future.demo.benchmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 对齐 InventoryMapper.bpkcMxb 常用过滤参数（非 full_matching / 非 skuOrTmFullyMatching 路径）。
 */
public final class BpkcMxbQueryParam {

    /** 与 {@link DdCompany751StyleGenerator} 生成数据同一基数，保证随机过滤能命中 */
    private static final long[] CP_IDS = {74703L, 74704L, 74705L, 74706L, 74707L, 74708L};
    private static final long[] CK_IDS = {30056L, 30067L, 30069L, 30073L, 30074L, 30060L, 30065L};
    private static final long[] DW_IDS = {1702L, 1L, 2L};
    private static final long[] YWY_IDS = {24643L, 24635L, 24733L};
    private static final long[] WLDW_IDS = {10028087L, 10028108L, 10028095L};
    private static final String[] FK_VALUES = {"cpfk1", "pbfk1", "成品幅宽1"};
    private static final String[] KZ_VALUES = {"cpkz1", "pbkz1", "成品克重1"};
    private static final String[] GG_VALUES = {"gg1"};

    private final long companyId;
    private Long ckId;
    private Long cpId;
    private List<Long> cpIdList;
    private Long cpYsId;
    private Long dwId;
    private String fk;
    private String kz;
    private String gg;
    private String gh;
    private String jh;
    private String ph;
    private Long dw2Id;
    private String ckZdy1;
    private String ckZdy2;
    private String ckZdy3;
    private String ckZdy4;
    private String ckZdy5;
    private Long cwId;
    private Long ywyId;
    private Long bpGysId;
    private Long bpJgsId;
    private Long bpKhId;
    /** null/false：HAVING kc_sl != 0；true：显示零库存 */
    private Boolean xslkc;
    /** false：HAVING dck_sl <= 0；null/true：不过滤待出库 */
    private Boolean xsdck;
    private Integer offset;
    private Integer limit;
    /** true：full_matching 路径（必带 cp_id、dw_id） */
    private Boolean fullMatching;

    public BpkcMxbQueryParam(long companyId) {
        this.companyId = companyId;
    }

    /**
     * 每次调用随机一组过滤条件，模拟明细表不同筛选组合。
     */
    public static BpkcMxbQueryParam random(long companyId) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        BpkcMxbQueryParam p = new BpkcMxbQueryParam(companyId);

        // ~20% 走 full_matching（汇总表跳转明细常见）
        if (r.nextDouble() < 0.20) {
            p.fullMatching = true;
            p.cpId = pick(CP_IDS, r);
            p.dwId = pick(DW_IDS, r);
            if (r.nextBoolean()) {
                p.ckId = pick(CK_IDS, r);
            }
            if (r.nextDouble() < 0.4) {
                p.fk = pick(FK_VALUES, r);
            }
            if (r.nextDouble() < 0.4) {
                p.kz = pick(KZ_VALUES, r);
            }
            if (r.nextDouble() < 0.3) {
                p.gg = pick(GG_VALUES, r);
            }
            if (r.nextDouble() < 0.5) {
                p.gh = "gh" + r.nextInt(1, 100);
            }
            if (r.nextDouble() < 0.2) {
                p.jh = "jh" + r.nextInt(1, 20);
            }
            if (r.nextDouble() < 0.4) {
                p.ph = String.format("%04d%02d%02d",
                        r.nextBoolean() ? 2026 : 2025,
                        r.nextInt(1, 13),
                        r.nextInt(1, 28));
            }
        } else {
            p.fullMatching = false;
            if (r.nextDouble() < 0.55) {
                p.ckId = pick(CK_IDS, r);
            }
            // cp_id IN 列表 vs 单 cp_id vs 不传
            double cpMode = r.nextDouble();
            if (cpMode < 0.25) {
                int n = r.nextInt(1, 4);
                List<Long> list = new ArrayList<>(n);
                List<Long> pool = new ArrayList<>();
                for (long id : CP_IDS) {
                    pool.add(id);
                }
                for (int i = 0; i < n && !pool.isEmpty(); i++) {
                    list.add(pool.remove(r.nextInt(pool.size())));
                }
                p.cpIdList = list;
            } else if (cpMode < 0.70) {
                p.cpId = pick(CP_IDS, r);
            }
            if (r.nextDouble() < 0.45) {
                p.gh = "gh" + r.nextInt(1, 100);
            }
            if (r.nextDouble() < 0.25) {
                p.fk = pick(FK_VALUES, r);
            }
            if (r.nextDouble() < 0.25) {
                p.kz = pick(KZ_VALUES, r);
            }
            if (r.nextDouble() < 0.20) {
                p.gg = pick(GG_VALUES, r);
            }
            if (r.nextDouble() < 0.20) {
                p.jh = r.nextBoolean() ? ("卷号" + r.nextInt(1, 80)) : ("jh" + r.nextInt(1, 20));
            }
            if (r.nextDouble() < 0.35) {
                p.ph = String.format("%04d%02d%02d",
                        r.nextBoolean() ? 2026 : 2025,
                        r.nextInt(1, 13),
                        r.nextInt(1, 28));
            }
            if (r.nextDouble() < 0.25) {
                p.ywyId = pick(YWY_IDS, r);
            }
            if (r.nextDouble() < 0.20) {
                p.bpGysId = pick(WLDW_IDS, r);
            }
            if (r.nextDouble() < 0.10) {
                p.bpKhId = pick(WLDW_IDS, r);
            }
        }

        // 多数场景不显示零库存；约 15% 显示
        p.xslkc = r.nextDouble() < 0.15;
        // 约 20% 过滤待出库
        if (r.nextDouble() < 0.20) {
            p.xsdck = false;
        }

        // 分页：对齐明细表常见 pageSize=200
        int[] pageSizes = {50, 100, 200};
        p.limit = pageSizes[r.nextInt(pageSizes.length)];
        p.offset = r.nextInt(0, 5) * p.limit;

        return p;
    }

    /** 无额外过滤、仅 company + 默认 HAVING + 首页，便于冒烟 */
    public static BpkcMxbQueryParam companyOnly(long companyId) {
        BpkcMxbQueryParam p = new BpkcMxbQueryParam(companyId);
        p.fullMatching = false;
        p.xslkc = false;
        p.limit = 200;
        p.offset = 0;
        return p;
    }

    private static long pick(long[] arr, ThreadLocalRandom r) {
        return arr[r.nextInt(arr.length)];
    }

    private static String pick(String[] arr, ThreadLocalRandom r) {
        return arr[r.nextInt(arr.length)];
    }

    public long getCompanyId() {
        return companyId;
    }

    public Long getCkId() {
        return ckId;
    }

    public Long getCpId() {
        return cpId;
    }

    public List<Long> getCpIdList() {
        return cpIdList;
    }

    public Long getCpYsId() {
        return cpYsId;
    }

    public Long getDwId() {
        return dwId;
    }

    public String getFk() {
        return fk;
    }

    public String getKz() {
        return kz;
    }

    public String getGg() {
        return gg;
    }

    public String getGh() {
        return gh;
    }

    public String getJh() {
        return jh;
    }

    public String getPh() {
        return ph;
    }

    public Long getDw2Id() {
        return dw2Id;
    }

    public String getCkZdy1() {
        return ckZdy1;
    }

    public String getCkZdy2() {
        return ckZdy2;
    }

    public String getCkZdy3() {
        return ckZdy3;
    }

    public String getCkZdy4() {
        return ckZdy4;
    }

    public String getCkZdy5() {
        return ckZdy5;
    }

    public Long getCwId() {
        return cwId;
    }

    public Long getYwyId() {
        return ywyId;
    }

    public Long getBpGysId() {
        return bpGysId;
    }

    public Long getBpJgsId() {
        return bpJgsId;
    }

    public Long getBpKhId() {
        return bpKhId;
    }

    public Boolean getXslkc() {
        return xslkc;
    }

    public Boolean getXsdck() {
        return xsdck;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public Boolean getFullMatching() {
        return fullMatching;
    }

    @Override
    public String toString() {
        return "BpkcMxbQueryParam{companyId=" + companyId
                + ", fullMatching=" + fullMatching
                + ", ckId=" + ckId
                + ", cpId=" + cpId
                + ", cpIdList=" + cpIdList
                + ", dwId=" + dwId
                + ", fk=" + fk
                + ", kz=" + kz
                + ", gg=" + gg
                + ", gh=" + gh
                + ", jh=" + jh
                + ", ph=" + ph
                + ", ywyId=" + ywyId
                + ", bpGysId=" + bpGysId
                + ", bpKhId=" + bpKhId
                + ", xslkc=" + xslkc
                + ", xsdck=" + xsdck
                + ", offset=" + offset
                + ", limit=" + limit
                + ", cpYsId=" + cpYsId
                + ", cwId=" + cwId
                + ", dw2Id=" + dw2Id
                + ", ckZdy=" + Arrays.asList(ckZdy1, ckZdy2, ckZdy3, ckZdy4, ckZdy5)
                + "}";
    }
}
