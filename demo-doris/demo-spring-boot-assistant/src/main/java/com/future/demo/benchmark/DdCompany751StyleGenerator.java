package com.future.demo.benchmark;

import com.future.demo.entity.Dd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 按 192.168.1.72 cloud_cloth_db.dd 中 company_id=751 的真实分布生成近似数据。
 * <p>
 * 主要规律（基于约 2337 行样本）：
 * <ul>
 *   <li>dj_type / dj_type_sub 按真实频次加权（jl / rk / ck / mt）</li>
 *   <li>jl 行：jl_id&gt;0、mt_id=0，约 70%+ 带 mx_id；mt 行：mt_id&gt;0、jl/mx=0</li>
 *   <li>标志位：多数 is_delete=0、is_zf/is_sh 常为 1、is_jd/is_qx=0</li>
 *   <li>dh = 类型前缀 + yyMMdd + 8 位流水；cp/ck/wldw 等落在小基数集合</li>
 *   <li>ps 多为 1~2，sl 跨度大（约 1~2222）</li>
 * </ul>
 */
public final class DdCompany751StyleGenerator {

    public static final long DEFAULT_COMPANY_ID = 751L;

    private static final DateTimeFormatter DH_DATE = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter DT_MIN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DT_SEC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** (dj_type, dj_type_sub, weight, dhPrefix) — weight 来自公司 751 实际行数 */
    private static final TypeSpec[] TYPE_SPECS = {
            spec("bp_cglhd", "jl", 319, "CLD"),
            spec("kc_bpqtrk", "rk", 313, "QRK"),
            spec("kc_bpqtck", "ck", 214, "QCK"),
            spec("bp_jglhd", "jl", 123, "JLD"),
            spec("bp_cgdd", "jl", 108, "CDD"),
            spec("bp_jglhd", "mt", 81, "JLD"),
            spec("bp_xsdd", "jl", 77, "XDD"),
            spec("bp_xsfhjs", "jl", 75, "XSD"),
            spec("bp_cglhjs", "jl", 71, "CLS"),
            spec("bp_xsfhd", "jl", 67, "XFD"),
            spec("bp_jgthd", "jl", 56, "JTD"),
            spec("kc_bpghbq", "ck", 53, "GBQ"),
            spec("kc_bpghbq", "rk", 53, "GBQ"),
            spec("bp_cgthd", "jl", 48, "CTD"),
            spec("bp_wwjg", "mt", 45, "JGD"),
            spec("bp_wwjg", "jl", 44, "JGD"),
            spec("bp_jglhjs", "jl", 44, "JLS"),
            spec("kc_bptbd", "rk", 43, "DBD"),
            spec("kc_bptbd", "ck", 43, "DBD"),
            spec("kc_bpczd", "ck", 38, "CZD"),
            spec("kc_bpczd", "rk", 36, "CZD"),
            spec("bp_jgthjs", "jl", 35, "JTS"),
            spec("kc_bpfp", "ck", 32, "FPD"),
            spec("kc_bpfp", "rk", 32, "FPD"),
            spec("bp_jgthd", "rk", 31, "JTD"),
            spec("bp_jglhjs", "mt", 30, "JLS"),
            spec("bp_fskcd", "rk", 27, "FSK"),
            spec("bp_cgthjs", "jl", 27, "CTS"),
            spec("kc_bpmxpkd", "ck", 26, "PKD"),
            spec("bp_xsthd", "jl", 24, "XTD"),
            spec("kc_bptmpd", "ck", 18, "TPD"),
            spec("kc_bptbjh", "rk", 16, "DBJ"),
            spec("kc_bptbjh", "ck", 16, "DBJ"),
            spec("bp_xsthjs", "jl", 16, "XTH"),
            spec("kc_bppyd", "rk", 12, "PYD"),
            spec("bp_jgthjs", "rk", 11, "JTS"),
            spec("bp_jgkp", "mt", 10, "JKP"),
            spec("kc_bptmpd", "jl", 7, "TPD"),
            spec("kc_bpzspd", "ck", 5, "ZPD"),
            spec("tmcj", "jl", 4, "TMJ"),
    };

    private static final long[] CP_IDS = {74703L, 74704L, 74705L, 74706L, 74707L, 74708L};
    private static final long[] CK_IDS = {30056L, 30067L, 30069L, 30073L, 30074L, 30060L, 30065L};
    private static final long[] DW_IDS = {1702L, 1L, 2L};
    private static final long[] WLDW_IDS = {0L, 10028087L, 10028108L, 10028095L};
    private static final long[] YWY_IDS = {0L, 24643L, 24635L, 24733L};
    private static final long[] STAFF_IDS = {0L, 24085L};
    private static final String[] FK_VALUES = {"", "cpfk1", "pbfk1", "成品幅宽1"};
    private static final String[] KZ_VALUES = {"", "cpkz1", "pbkz1", "成品克重1"};
    private static final String[] GG_VALUES = {"", "gg1"};
    private static final String[] BRANDS = {"", "", "", "", "", "pp1", "pp2"};
    private static final String[] QUARTERS = {"", "", "", "", "jd1", "春", "jd2"};
    private static final String[] YY_DJLX = {"", "", "", "", "bp_cgdd", "bp_wwjg", "bp_xsdd", "bp_cglhd"};

    private final long companyId;
    private final AtomicLong djIdSeq;
    private final AtomicLong jlIdSeq;
    private final AtomicLong mtIdSeq;
    private final AtomicLong mxIdSeq;
    private final AtomicInteger dhSeq;
    private final int totalWeight;

    public DdCompany751StyleGenerator(long companyId) {
        this.companyId = companyId;
        long base = System.currentTimeMillis() % 1_000_000_000L;
        this.djIdSeq = new AtomicLong(10_000_000L + base);
        this.jlIdSeq = new AtomicLong(20_000_000L + base);
        this.mtIdSeq = new AtomicLong(30_000_000L + base);
        this.mxIdSeq = new AtomicLong(40_000_000L + base);
        this.dhSeq = new AtomicInteger(1);
        int tw = 0;
        for (TypeSpec s : TYPE_SPECS) {
            tw += s.weight;
        }
        this.totalWeight = tw;
    }

    public static DdCompany751StyleGenerator forCompany751() {
        return new DdCompany751StyleGenerator(DEFAULT_COMPANY_ID);
    }

    public List<Dd> generate(int count) {
        List<Dd> rows = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            rows.add(next());
        }
        return rows;
    }

    public Dd next() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        TypeSpec type = pickType(r);
        Dd row = new Dd();
        row.setCompany_id(companyId);
        row.setDj_type(type.djType);
        row.setDj_type_sub(type.djTypeSub);
        row.setDj_id(djIdSeq.incrementAndGet());

        fillUkChildren(row, type, r);
        fillFlags(row, r);
        fillBizIds(row, r);
        fillStrings(row, type, r);
        fillQuantities(row, type, r);
        fillTimes(row, r);
        return row;
    }

    private void fillUkChildren(Dd row, TypeSpec type, ThreadLocalRandom r) {
        if ("mt".equals(type.djTypeSub)) {
            row.setJl_id(0L);
            row.setMt_id(mtIdSeq.incrementAndGet());
            row.setMx_id(0L);
            return;
        }
        row.setJl_id(jlIdSeq.incrementAndGet());
        row.setMt_id(0L);
        // jl/rk/ck：多数明细行带 mx_id（公司 751 约 70%~88%）
        if (r.nextDouble() < 0.75) {
            row.setMx_id(mxIdSeq.incrementAndGet());
        } else {
            row.setMx_id(0L);
        }
    }

    private void fillFlags(Dd row, ThreadLocalRandom r) {
        // is_delete≈9%, is_zf≈59%, is_sh≈74%, is_jd/is_qx≈0
        row.setIs_delete(r.nextDouble() < 0.09 ? 1 : 0);
        row.setIs_zf(r.nextDouble() < 0.59 ? 1 : 0);
        row.setIs_sh(r.nextDouble() < 0.74 ? 1 : 0);
        row.setIs_jd(0);
        row.setIs_qx(r.nextDouble() < 0.001 ? 1 : 0);
        row.setHas_count(0);
        row.setDjlb_fh(null);
        row.setIs_fh(null);
    }

    private void fillBizIds(Dd row, ThreadLocalRandom r) {
        row.setDd_dj_id(0L);
        row.setDd_jl_id(0L);
        row.setCk_id(pick(CK_IDS, r));
        row.setCw_id(0L);
        row.setCp_id(pick(CP_IDS, r));
        row.setCp_ys_id(0L);
        row.setDw_id(pick(DW_IDS, r));
        row.setDw2_id(0L);
        row.setYwy_id(pick(YWY_IDS, r));
        row.setWldw_id(pick(WLDW_IDS, r));
        long gys = pick(WLDW_IDS, r);
        row.setBp_gys_id(gys);
        row.setBp_jgs_id(0L);
        row.setBp_kh_id(r.nextDouble() < 0.1 ? pick(WLDW_IDS, r) : 0L);
        long staff = pick(STAFF_IDS, r);
        row.setZdr_id(staff == 0 ? 24085L : staff);
        row.setJsr_id(0L);
        row.setShr_id(row.getIs_sh() != null && row.getIs_sh() == 1 ? 24085L : 0L);
        row.setZfr_id(row.getIs_zf() != null && row.getIs_zf() == 1 ? 24085L : 0L);
        row.setYwgd_staff_id(0L);
        row.setDj_lsh(r.nextInt(1, 200));
    }

    private void fillStrings(Dd row, TypeSpec type, ThreadLocalRandom r) {
        LocalDateTime billTime = randomBillTime(r);
        row.setDh(type.dhPrefix + billTime.format(DH_DATE) + String.format("%08d", dhSeq.getAndIncrement()));
        row.setDd_dh("");
        row.setFk(pick(FK_VALUES, r));
        row.setKz(pick(KZ_VALUES, r));
        row.setJh(r.nextDouble() < 0.10 ? ("卷号" + r.nextInt(1, 80)) : (r.nextDouble() < 0.05 ? ("jh" + r.nextInt(1, 20)) : ""));
        row.setGh(r.nextDouble() < 0.87 ? ("gh" + r.nextInt(1, 100)) : "");
        row.setGg(pick(GG_VALUES, r));
        row.setTm(r.nextDouble() < 0.60 ? String.valueOf(10000000000000L + r.nextLong(0, 9_000_000_000_000L)) : "");
        row.setPh(r.nextDouble() < 0.55 ? billTime.format(DateTimeFormatter.BASIC_ISO_DATE) : "");
        row.setCk_zdy1("");
        row.setCk_zdy2("");
        row.setCk_zdy3("");
        row.setCk_zdy4("");
        row.setCk_zdy5("");
        row.setDyeing_advice("");
        row.setJl_bz("");
        row.setDj_bz("");
        row.setBrand(pick(BRANDS, r));
        row.setStyle_number(r.nextDouble() < 0.05 ? ("kh" + r.nextInt(1, 20)) : "");
        row.setQuarter(pick(QUARTERS, r));
        row.setYy_djlx(pick(YY_DJLX, r));
    }

    private void fillQuantities(Dd row, TypeSpec type, ThreadLocalRandom r) {
        // ps 多为 1/2，偶发更大；fskcd/fp 等可为 0
        BigDecimal ps;
        if ("bp_fskcd".equals(type.djType) || ("kc_bpfp".equals(type.djType) && "ck".equals(type.djTypeSub))) {
            ps = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        } else if (r.nextDouble() < 0.70) {
            ps = bd(1);
        } else if (r.nextDouble() < 0.90) {
            ps = bd(2);
        } else {
            ps = bd(r.nextInt(3, 102));
        }
        row.setPs(ps);

        BigDecimal sl;
        double p = r.nextDouble();
        if (p < 0.35) {
            sl = bd(r.nextInt(1, 12));
        } else if (p < 0.70) {
            sl = bd(r.nextInt(10, 120));
        } else if (p < 0.92) {
            sl = bd(r.nextInt(100, 700));
        } else {
            sl = bd(r.nextInt(700, 2223));
        }
        row.setSl(sl);
        row.setZsl2(r.nextDouble() < 0.2 ? sl : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
    }

    private void fillTimes(Dd row, ThreadLocalRandom r) {
        LocalDateTime kdsj = randomBillTime(r);
        // 库存类多带秒；业务类有的只有到分钟
        boolean withSec = row.getDj_type().startsWith("kc_") || r.nextBoolean();
        row.setKdsj(kdsj.format(withSec ? DT_SEC : DT_MIN));
        if (row.getIs_sh() != null && row.getIs_sh() == 1) {
            row.setSh_sj(kdsj.plusMinutes(r.nextInt(1, 120)).format(withSec ? DT_SEC : DT_MIN));
        } else {
            row.setSh_sj(null);
        }
        if (row.getIs_zf() != null && row.getIs_zf() == 1 && r.nextDouble() < 0.3) {
            row.setZf_sj(kdsj.plusMinutes(r.nextInt(1, 180)).format(withSec ? DT_SEC : DT_MIN));
        } else {
            row.setZf_sj(null);
        }
    }

    private TypeSpec pickType(ThreadLocalRandom r) {
        int hit = r.nextInt(totalWeight);
        int acc = 0;
        for (TypeSpec s : TYPE_SPECS) {
            acc += s.weight;
            if (hit < acc) {
                return s;
            }
        }
        return TYPE_SPECS[0];
    }

    private static LocalDateTime randomBillTime(ThreadLocalRandom r) {
        // 贴近样本：2026-04 ~ 2026-05 为主，并覆盖近两年
        int year = r.nextDouble() < 0.7 ? 2026 : r.nextInt(2024, 2027);
        int month = year == 2026 ? r.nextInt(4, 7) : r.nextInt(1, 13);
        int day = r.nextInt(1, 28);
        return LocalDateTime.of(year, month, day, r.nextInt(0, 24), r.nextInt(0, 60), r.nextInt(0, 60));
    }

    private static BigDecimal bd(int v) {
        return BigDecimal.valueOf(v).setScale(4, RoundingMode.HALF_UP);
    }

    private static <T> T pick(T[] arr, ThreadLocalRandom r) {
        return arr[r.nextInt(arr.length)];
    }

    private static long pick(long[] arr, ThreadLocalRandom r) {
        return arr[r.nextInt(arr.length)];
    }

    private static TypeSpec spec(String djType, String djTypeSub, int weight, String dhPrefix) {
        return new TypeSpec(djType, djTypeSub, weight, dhPrefix);
    }

    private static final class TypeSpec {
        final String djType;
        final String djTypeSub;
        final int weight;
        final String dhPrefix;

        TypeSpec(String djType, String djTypeSub, int weight, String dhPrefix) {
            this.djType = djType;
            this.djTypeSub = djTypeSub;
            this.weight = weight;
            this.dhPrefix = dhPrefix;
        }
    }
}
