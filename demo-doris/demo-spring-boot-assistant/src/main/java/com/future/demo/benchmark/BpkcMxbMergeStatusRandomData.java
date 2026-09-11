package com.future.demo.benchmark;

import com.future.demo.entity.BpkcMxbMergeStatus;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * demot.bpkc_mxb_merge_status 压测随机行生成。
 */
public final class BpkcMxbMergeStatusRandomData {

    public static final String DJLX = "jmh_merge_status";

    private static final String[] TYPES = {"kc", "dck", "jg_zy"};
    private static final String[] JLLXS = {"jl", "mt", "mx", "rk", "ck"};
    private static final long[] COMPANY_IDS = {
            910001L, 910002L, 910003L, 910004L, 910005L,
            910006L, 910007L, 910008L, 910009L, 910010L
    };

    private BpkcMxbMergeStatusRandomData() {
    }

    public static BpkcMxbMergeStatus next(AtomicLong seq) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        long id = seq.incrementAndGet();
        BpkcMxbMergeStatus row = new BpkcMxbMergeStatus();
        row.setCompany_id(COMPANY_IDS[r.nextInt(COMPANY_IDS.length)]);
        row.setKey("jmh-key-" + id);
        row.setType(TYPES[r.nextInt(TYPES.length)]);
        row.setDjlx(DJLX);
        row.setDj_id(id);
        row.setJl_id(r.nextLong(1, 10_000));
        row.setMt_id(r.nextLong(0, 1000));
        row.setMx_id(r.nextLong(0, 1000));
        row.setJllx(JLLXS[r.nextInt(JLLXS.length)]);
        row.setDh("DH-" + id);
        row.setIs_sh(r.nextInt(0, 2));
        row.setIs_zf(0);
        row.setIs_jd(r.nextInt(0, 2));
        row.setIs_delete(0);
        row.setIs_qx(0);
        return row;
    }
}
