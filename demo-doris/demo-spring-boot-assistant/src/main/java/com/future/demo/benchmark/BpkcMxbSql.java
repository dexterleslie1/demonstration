package com.future.demo.benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * 布匹库存明细表查询 SQL，对齐 finance InventoryMapper.xml {@code bpkcMxb} 生效路径：
 * <ul>
 *   <li>过滤 is_jd/is_delete/is_zf/is_qx=0</li>
 *   <li>单据类型：affectingKcCrkDjlxNCgwlNJgzyNJgwlNXswfNYjdrNYjdcDjlx</li>
 *   <li>full_matching / 普通过滤（otherwise）动态条件（有值才拼 AND，无 IS NULL 短路）</li>
 *   <li>HAVING：xslkc / xsdck</li>
 *   <li>ORDER BY tm ASC，LIMIT/OFFSET</li>
 *   <li>已注释的 dckInfoPerGroup / rkInfoPerGroup JOIN 不纳入</li>
 * </ul>
 */
public final class BpkcMxbSql {

    private BpkcMxbSql() {
    }

    public static final class BuiltQuery {
        public final String sql;
        public final Object[] args;

        BuiltQuery(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }
    }

    /** 出入库单据类型（affectingKcRkDjlx OR affectingKcCkDjlx） */
    private static final String AFFECTING_KC_CRK =
            "("
                    + "((dj_type='kc_bptbd' AND dj_type_sub='rk') OR"
                    + " (dj_type='kc_bpqtrk') OR"
                    + " (dj_type='kc_bpczd' AND dj_type_sub='rk') OR"
                    + " (dj_type='kc_bppyd') OR"
                    + " (dj_type='kc_bpzspd') OR"
                    + " (dj_type='kc_bpfp' AND dj_type_sub='rk') OR"
                    + " (dj_type='kc_bpghbq' AND dj_type_sub='rk') OR"
                    + " (dj_type='bp_cglhd') OR"
                    + " (dj_type='bp_cglhjs' AND yy_djlx!='bp_cglhd') OR"
                    + " (dj_type='bp_jglhd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_jglhjs' AND dj_type_sub='jl' AND yy_djlx!='bp_jglhd') OR"
                    + " (dj_type='bp_jgthd' AND dj_type_sub='rk') OR"
                    + " (dj_type='bp_jgthjs' AND dj_type_sub='rk' AND yy_djlx!='bp_jgthd') OR"
                    + " (dj_type='bp_xsthd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_xsthjs' AND dj_type_sub='jl' AND yy_djlx!='bp_xsthd'))"
                    + " OR "
                    + "((dj_type='kc_bptbd' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bpqtck') OR"
                    + " (dj_type='kc_bpczd' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bpmxpkd') OR"
                    + " (dj_type='kc_bpfp' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bpghbq' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bptmpd' AND dj_type_sub='ck') OR"
                    + " (dj_type='bp_cgthd') OR"
                    + " (dj_type='bp_cgthjs' AND yy_djlx!='bp_cgthd') OR"
                    + " (dj_type='bp_jglhd' AND dj_type_sub='mt') OR"
                    + " (dj_type='bp_jgthd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_jglhjs' AND dj_type_sub='mt' AND yy_djlx!='bp_jglhd') OR"
                    + " (dj_type='bp_jgthjs' AND dj_type_sub='jl' AND yy_djlx!='bp_jgthd') OR"
                    + " (dj_type='bp_jgkp' AND dj_type_sub='mt') OR"
                    + " (dj_type='bp_xsfhd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_xsfhjs' AND dj_type_sub='jl' AND yy_djlx!='bp_xsfhd'))"
                    + ")";

    private static final String AFFECTING_KC_CK =
            "((dj_type='kc_bptbd' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bpqtck') OR"
                    + " (dj_type='kc_bpczd' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bpmxpkd') OR"
                    + " (dj_type='kc_bpfp' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bpghbq' AND dj_type_sub='ck') OR"
                    + " (dj_type='kc_bptmpd' AND dj_type_sub='ck') OR"
                    + " (dj_type='bp_cgthd') OR"
                    + " (dj_type='bp_cgthjs' AND yy_djlx!='bp_cgthd') OR"
                    + " (dj_type='bp_jglhd' AND dj_type_sub='mt') OR"
                    + " (dj_type='bp_jgthd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_jglhjs' AND dj_type_sub='mt' AND yy_djlx!='bp_jglhd') OR"
                    + " (dj_type='bp_jgthjs' AND dj_type_sub='jl' AND yy_djlx!='bp_jgthd') OR"
                    + " (dj_type='bp_jgkp' AND dj_type_sub='mt') OR"
                    + " (dj_type='bp_xsfhd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_xsfhjs' AND dj_type_sub='jl' AND yy_djlx!='bp_xsfhd'))";

    private static final String AFFECTING_KC_RK =
            "((dj_type='kc_bptbd' AND dj_type_sub='rk') OR"
                    + " (dj_type='kc_bpqtrk') OR"
                    + " (dj_type='kc_bpczd' AND dj_type_sub='rk') OR"
                    + " (dj_type='kc_bppyd') OR"
                    + " (dj_type='kc_bpzspd') OR"
                    + " (dj_type='kc_bpfp' AND dj_type_sub='rk') OR"
                    + " (dj_type='kc_bpghbq' AND dj_type_sub='rk') OR"
                    + " (dj_type='bp_cglhd') OR"
                    + " (dj_type='bp_cglhjs' AND yy_djlx!='bp_cglhd') OR"
                    + " (dj_type='bp_jglhd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_jglhjs' AND dj_type_sub='jl' AND yy_djlx!='bp_jglhd') OR"
                    + " (dj_type='bp_jgthd' AND dj_type_sub='rk') OR"
                    + " (dj_type='bp_jgthjs' AND dj_type_sub='rk' AND yy_djlx!='bp_jgthd') OR"
                    + " (dj_type='bp_xsthd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_xsthjs' AND dj_type_sub='jl' AND yy_djlx!='bp_xsthd'))";

    private static final String KC_FLAGS = "is_sh=1 AND is_zf=0 AND is_jd=0 AND is_delete=0 AND is_qx=0";

    private static final String DCK_CONDITION =
            "("
                    + "(dj_type='bp_cgthd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_cgthjs' AND dj_type_sub='jl' AND yy_djlx!='bp_cgthd') OR"
                    + " (dj_type='bp_jgthd' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_jgthjs' AND dj_type_sub='jl' AND yy_djlx!='bp_jgthd') OR"
                    + " (dj_type='bp_jgkp' AND dj_type_sub='jl') OR"
                    + " (dj_type='bp_jglhd' AND dj_type_sub='mt') OR"
                    + " (dj_type='bp_jglhjs' AND dj_type_sub='mt' AND yy_djlx!='bp_jgthd') OR"
                    + " (dj_type='bp_xsfhjs' AND dj_type_sub='jl' AND yy_djlx!='bp_xsfhd') OR"
                    + " (dj_type='bp_xsfhjs_cg') OR"
                    + " (dj_type='bp_xsfhd' AND dj_type_sub='jl') OR"
                    + " (dj_type IN ('kc_bptbd','kc_bpfp','kc_bpczd','kc_bpghbq','kc_bppyd','kc_bpmxpkd','kc_bpzspd','kc_bpqtrk','kc_bpqtck','kc_bptmpd') AND dj_type_sub='ck') OR"
                    + " (dj_type='tmcj')"
                    + ") AND is_sh=0 AND is_jd=0 AND is_delete=0 AND is_zf=0 AND is_qx=0";

    private static final String JG_ZY_CONDITION =
            "(dj_type='bp_wwjg' AND dj_type_sub='mt' AND is_jd=0 AND is_zf=0 AND has_count=0 AND is_delete=0 AND is_qx=0)";

    private static final String SELECT_COL_ALL =
            "CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_id ELSE 0 END AS ck_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN cw_id ELSE 0 END AS cw_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN cp_id ELSE 0 END AS cp_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN cp_ys_id ELSE 0 END AS cp_ys_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN fk ELSE '' END AS fk,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN kz ELSE '' END AS kz,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN dw_id ELSE 0 END AS dw_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN jh ELSE '' END AS jh,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN gh ELSE '' END AS gh,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN gg ELSE '' END AS gg,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ph ELSE '' END AS ph,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN '' ELSE tm END AS tm,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy1 ELSE '' END AS ck_zdy1,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy2 ELSE '' END AS ck_zdy2,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy3 ELSE '' END AS ck_zdy3,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy4 ELSE '' END AS ck_zdy4,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN dyeing_advice ELSE '' END AS dyeing_advice,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy5 ELSE '' END AS ck_zdy5,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN zsl2 ELSE 0 END AS zsl2,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN dw2_id ELSE 0 END AS dw2_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ywy_id ELSE 0 END AS ywy_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN bp_gys_id ELSE 0 END AS bp_gys_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN bp_jgs_id ELSE 0 END AS bp_jgs_id,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN bp_kh_id ELSE 0 END AS bp_kh_id";

    private static final String GROUP_BY_COL_ALL =
            "CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN cw_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN cp_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN cp_ys_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN fk ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN kz ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN dw_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN jh ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN gh ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN gg ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ph ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN '' ELSE tm END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy1 ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy2 ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy3 ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy4 ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN dyeing_advice ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ck_zdy5 ELSE '' END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN zsl2 ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN dw2_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN ywy_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN bp_gys_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN bp_jgs_id ELSE 0 END,"
                    + " CASE WHEN (tm IS NULL OR TRIM(tm) = '') THEN bp_kh_id ELSE 0 END";

    private static final String ID_EXPR =
            "CONCAT(dj_type, '#', dj_type_sub, '#', dj_id, '#', jl_id, '#', mt_id, '#', mx_id)";

    /**
     * 按 bpkcMxb 参数动态拼 SQL（对齐 MyBatis：有条件才拼 AND col = ?）。
     */
    public static BuiltQuery build(BpkcMxbQueryParam p) {
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder(4096);
        sql.append("WITH tmpKc AS ( SELECT ")
                .append(SELECT_COL_ALL).append(',')
                .append(" CAST(SUM(CASE")
                .append("   WHEN ").append(AFFECTING_KC_CK).append(" AND ").append(KC_FLAGS).append(" THEN -ps")
                .append("   WHEN ").append(AFFECTING_KC_RK).append(" AND ").append(KC_FLAGS).append(" THEN ps")
                .append("   ELSE 0 END) AS DECIMAL(20, 2)) AS kc_ps,")
                .append(" CAST(SUM(CASE")
                .append("   WHEN ").append(AFFECTING_KC_CK).append(" AND ").append(KC_FLAGS).append(" THEN -sl")
                .append("   WHEN ").append(AFFECTING_KC_RK).append(" AND ").append(KC_FLAGS).append(" THEN sl")
                .append("   ELSE 0 END) AS DECIMAL(20, 2)) AS kc_sl,")
                .append(" GROUP_CONCAT(DISTINCT CASE")
                .append("   WHEN ((").append(AFFECTING_KC_CK).append(" AND ").append(KC_FLAGS).append(") OR (")
                .append(AFFECTING_KC_RK).append(" AND ").append(KC_FLAGS).append("))")
                .append("   THEN ").append(ID_EXPR).append(" ELSE NULL END")
                .append("   ORDER BY ").append(ID_EXPR).append(" SEPARATOR ',') AS kc_id_arr,")
                .append(" CAST(IFNULL(SUM(CASE WHEN ").append(DCK_CONDITION).append(" THEN ps ELSE 0 END), 0) AS DECIMAL(20, 2)) AS dck_ps,")
                .append(" CAST(IFNULL(SUM(CASE WHEN ").append(DCK_CONDITION).append(" THEN sl ELSE 0 END), 0) AS DECIMAL(20, 2)) AS dck_sl,")
                .append(" GROUP_CONCAT(DISTINCT CASE WHEN ").append(DCK_CONDITION)
                .append(" THEN ").append(ID_EXPR).append(" ELSE NULL END SEPARATOR ',') AS dck_id_arr,")
                .append(" CAST(IFNULL(SUM(CASE WHEN ").append(JG_ZY_CONDITION).append(" THEN ps ELSE 0 END), 0) AS DECIMAL(20, 2)) AS jg_zy_ps,")
                .append(" CAST(IFNULL(SUM(CASE WHEN ").append(JG_ZY_CONDITION).append(" THEN sl ELSE 0 END), 0) AS DECIMAL(20, 2)) AS jg_zy_sl,")
                .append(" GROUP_CONCAT(DISTINCT CASE WHEN ").append(JG_ZY_CONDITION)
                .append(" THEN ").append(ID_EXPR).append(" ELSE NULL END SEPARATOR ',') AS jg_zy_id_arr")
                .append(" FROM dd")
                .append(" WHERE company_id = ?")
                .append("   AND is_jd=0 AND is_delete=0 AND is_zf=0 AND is_qx=0");
        args.add(p.getCompanyId());

        appendFilters(sql, args, p);

        sql.append("   AND (")
                .append("     (dj_type = 'bp_cgdd') OR")
                .append("     (dj_type = 'bp_wwjg') OR")
                .append("     (dj_type = 'bp_xsdd' AND ((djlb_fh = 0) OR (djlb_fh = 1 AND is_fh = 1))) OR")
                .append("     (dj_type = 'kc_bptbjh') OR")
                .append("     (dj_type = 'tmcj') OR")
                .append("     (dj_type = 'bp_xsfhjs_cg') OR")
                .append("     ").append(AFFECTING_KC_CRK)
                .append("   )")
                .append(" GROUP BY ").append(GROUP_BY_COL_ALL);

        appendHaving(sql, p);

        sql.append(") SELECT t1.* FROM tmpKc t1 ORDER BY t1.tm ASC");

        if (p.getLimit() != null && p.getOffset() != null) {
            sql.append(" LIMIT ? OFFSET ?");
            args.add(p.getLimit());
            args.add(p.getOffset());
        }

        return new BuiltQuery(sql.toString(), args.toArray());
    }

    private static void appendFilters(StringBuilder sql, List<Object> args, BpkcMxbQueryParam p) {
        if (Boolean.TRUE.equals(p.getFullMatching())) {
            // full_matching：ck 可选；cp_id / dw_id 必带
            if (p.getCkId() != null) {
                sql.append(" AND ck_id = ?");
                args.add(p.getCkId());
            }
            sql.append(" AND cp_id = ?");
            args.add(p.getCpId());
            if (p.getCpYsId() != null) {
                sql.append(" AND cp_ys_id = ?");
                args.add(p.getCpYsId());
            }
            sql.append(" AND dw_id = ?");
            args.add(p.getDwId());
            if (p.getFk() != null) {
                sql.append(" AND fk = ?");
                args.add(p.getFk());
            }
            if (p.getKz() != null) {
                sql.append(" AND kz = ?");
                args.add(p.getKz());
            }
            if (p.getGg() != null) {
                sql.append(" AND gg = ?");
                args.add(p.getGg());
            }
            if (hasText(p.getGh())) {
                sql.append(" AND gh = ?");
                args.add(p.getGh());
            }
            if (hasText(p.getJh())) {
                sql.append(" AND jh = ?");
                args.add(p.getJh());
            }
            if (hasText(p.getPh())) {
                sql.append(" AND ph = ?");
                args.add(p.getPh());
            }
            return;
        }

        // otherwise：明细表普通筛选
        if (p.getCkId() != null && p.getCkId() > 0) {
            sql.append(" AND ck_id = ?");
            args.add(p.getCkId());
        }
        if (p.getCpIdList() != null && !p.getCpIdList().isEmpty()) {
            sql.append(" AND cp_id IN (");
            for (int i = 0; i < p.getCpIdList().size(); i++) {
                if (i > 0) {
                    sql.append(',');
                }
                sql.append('?');
                args.add(p.getCpIdList().get(i));
            }
            sql.append(')');
        } else if (p.getCpId() != null && p.getCpId() > 0) {
            sql.append(" AND cp_id = ?");
            args.add(p.getCpId());
        }
        if (p.getCpYsId() != null && p.getCpYsId() > 0) {
            sql.append(" AND cp_ys_id = ?");
            args.add(p.getCpYsId());
        }
        if (hasText(p.getGh())) {
            sql.append(" AND gh = ?");
            args.add(p.getGh());
        }
        if (p.getDw2Id() != null && p.getDw2Id() > 0) {
            sql.append(" AND dw2_id = ?");
            args.add(p.getDw2Id());
        }
        if (p.getCkZdy1() != null) {
            sql.append(" AND ck_zdy1 = ?");
            args.add(p.getCkZdy1());
        }
        if (p.getCkZdy2() != null) {
            sql.append(" AND ck_zdy2 = ?");
            args.add(p.getCkZdy2());
        }
        if (p.getCkZdy3() != null) {
            sql.append(" AND ck_zdy3 = ?");
            args.add(p.getCkZdy3());
        }
        if (p.getCkZdy4() != null) {
            sql.append(" AND ck_zdy4 = ?");
            args.add(p.getCkZdy4());
        }
        if (p.getCkZdy5() != null) {
            sql.append(" AND ck_zdy5 = ?");
            args.add(p.getCkZdy5());
        }
        if (p.getCwId() != null && p.getCwId() > 0) {
            sql.append(" AND cw_id = ?");
            args.add(p.getCwId());
        }
        if (hasText(p.getPh())) {
            sql.append(" AND ph = ?");
            args.add(p.getPh());
        }
        if (hasText(p.getFk())) {
            sql.append(" AND fk = ?");
            args.add(p.getFk());
        }
        if (hasText(p.getKz())) {
            sql.append(" AND kz = ?");
            args.add(p.getKz());
        }
        if (hasText(p.getJh())) {
            sql.append(" AND jh = ?");
            args.add(p.getJh());
        }
        if (hasText(p.getGg())) {
            sql.append(" AND gg = ?");
            args.add(p.getGg());
        }
        if (p.getYwyId() != null && p.getYwyId() > 0) {
            sql.append(" AND ywy_id = ?");
            args.add(p.getYwyId());
        }
        if (p.getBpGysId() != null && p.getBpGysId() > 0) {
            sql.append(" AND bp_gys_id = ?");
            args.add(p.getBpGysId());
        }
        if (p.getBpJgsId() != null && p.getBpJgsId() > 0) {
            sql.append(" AND bp_jgs_id = ?");
            args.add(p.getBpJgsId());
        }
        if (p.getBpKhId() != null && p.getBpKhId() > 0) {
            sql.append(" AND bp_kh_id = ?");
            args.add(p.getBpKhId());
        }
    }

    private static void appendHaving(StringBuilder sql, BpkcMxbQueryParam p) {
        boolean needKcSl = p.getXslkc() == null || Boolean.FALSE.equals(p.getXslkc());
        boolean needDck = Boolean.FALSE.equals(p.getXsdck());
        if (!needKcSl && !needDck) {
            return;
        }
        sql.append(" HAVING ");
        boolean first = true;
        if (needKcSl) {
            sql.append("kc_sl != 0");
            first = false;
        }
        if (needDck) {
            if (!first) {
                sql.append(" AND ");
            }
            sql.append("dck_sl <= 0");
        }
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
