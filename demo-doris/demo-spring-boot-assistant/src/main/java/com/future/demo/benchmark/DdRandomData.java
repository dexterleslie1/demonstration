package com.future.demo.benchmark;

import com.future.demo.entity.Dd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * demot.dd 压测随机行生成（JDBC / Stream Load 共用）。
 */
public final class DdRandomData {

    public static final String DJ_TYPE = "jmh_insert";

    private static final String[] DJ_TYPE_SUBS = {"jl", "mx", "mt", "ck"};
    private static final String[] BRANDS = {"nike", "adidas", "uniqlo", "hm", "zara"};
    private static final String[] QUARTERS = {"Q1", "Q2", "Q3", "Q4"};

    private DdRandomData() {
    }

    public static Dd next(AtomicLong djIdSeq) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Dd row = new Dd();

        row.setCompany_id(910000L + r.nextLong(1, 1001));
        row.setDj_type(DJ_TYPE);
        row.setDj_type_sub(DJ_TYPE_SUBS[r.nextInt(DJ_TYPE_SUBS.length)]);
        row.setDj_id(djIdSeq.incrementAndGet());
        row.setJl_id(r.nextLong(1, 10_000));
        row.setMt_id(r.nextLong(0, 1000));
        row.setMx_id(r.nextLong(0, 1000));

        row.setDd_dj_id(r.nextLong(1, 100_000));
        row.setDd_jl_id(r.nextLong(1, 10_000));
        row.setCk_id(r.nextLong(1, 500));
        row.setCw_id(r.nextLong(1, 200));
        row.setCp_id(r.nextLong(1, 50_000));
        row.setCp_ys_id(r.nextLong(1, 1000));
        row.setFk(randomStr(r, "FK", 6));
        row.setKz(randomStr(r, "KZ", 4));
        row.setDw_id(r.nextLong(1, 100));
        row.setJh(randomStr(r, "JH", 8));
        row.setGh(randomStr(r, "GH", 6));
        row.setGg(randomStr(r, "GG", 4));
        row.setTm(randomStr(r, "TM", 12));
        row.setPh(randomStr(r, "PH", 8));
        row.setCk_zdy1(randomStr(r, "Z1", 4));
        row.setCk_zdy2(randomStr(r, "Z2", 4));
        row.setCk_zdy3(randomStr(r, "Z3", 4));
        row.setCk_zdy4(randomStr(r, "Z4", 4));
        row.setDyeing_advice(randomStr(r, "DYE", 8));
        row.setCk_zdy5(randomStr(r, "Z5", 4));
        row.setZsl2(randomDecimal(r, 0, 1000));
        row.setDw2_id(r.nextLong(1, 100));
        row.setYwy_id(r.nextLong(1, 5000));
        row.setWldw_id(r.nextLong(1, 5000));
        row.setBp_gys_id(r.nextLong(1, 3000));
        row.setBp_jgs_id(r.nextLong(1, 3000));
        row.setBp_kh_id(r.nextLong(1, 3000));
        row.setYy_djlx(randomStr(r, "YY", 3));
        row.setHas_count(r.nextInt(0, 2));
        row.setIs_jd(r.nextInt(0, 2));
        row.setIs_sh(r.nextInt(0, 2));
        row.setIs_delete(0);
        row.setIs_zf(r.nextInt(0, 2));
        row.setIs_qx(r.nextInt(0, 2));
        row.setDjlb_fh(r.nextInt(0, 5));
        row.setIs_fh(r.nextInt(0, 2));
        row.setKdsj(randomDatetime(r));
        row.setSh_sj(randomDatetime(r));
        row.setZf_sj(null);
        row.setDh("DH-" + r.nextLong(1_000_000, 9_999_999));
        row.setDd_dh("DD-" + r.nextLong(1_000_000, 9_999_999));
        row.setZdr_id(r.nextLong(1, 10_000));
        row.setJsr_id(r.nextLong(1, 10_000));
        row.setShr_id(r.nextLong(1, 10_000));
        row.setZfr_id(r.nextLong(1, 10_000));
        row.setJl_bz(randomStr(r, "BZ", 10));
        row.setDj_bz(randomStr(r, "DJ", 10));
        row.setBrand(BRANDS[r.nextInt(BRANDS.length)]);
        row.setStyle_number(randomStr(r, "ST", 8));
        row.setQuarter(QUARTERS[r.nextInt(QUARTERS.length)]);
        row.setYwgd_staff_id(r.nextLong(1, 5000));
        row.setDj_lsh(r.nextInt(1, 99999));
        row.setPs(randomDecimal(r, 0, 100));
        row.setSl(randomDecimal(r, 0, 10_000));
        return row;
    }

    private static String randomStr(ThreadLocalRandom r, String prefix, int digits) {
        long bound = (long) Math.pow(10, Math.min(digits, 9));
        return prefix + r.nextLong(bound / 10, bound);
    }

    private static BigDecimal randomDecimal(ThreadLocalRandom r, double min, double max) {
        return BigDecimal.valueOf(r.nextDouble(min, max)).setScale(4, RoundingMode.HALF_UP);
    }

    private static String randomDatetime(ThreadLocalRandom r) {
        int y = r.nextInt(2020, 2027);
        int m = r.nextInt(1, 13);
        int d = r.nextInt(1, 29);
        int h = r.nextInt(0, 24);
        int mi = r.nextInt(0, 60);
        int s = r.nextInt(0, 60);
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", y, m, d, h, mi, s);
    }
}
