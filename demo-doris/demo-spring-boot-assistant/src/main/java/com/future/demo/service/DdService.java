package com.future.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.demo.benchmark.BpkcMxbQueryParam;
import com.future.demo.benchmark.BpkcMxbSql;
import com.future.demo.entity.Dd;
import com.future.demo.mapper.DdMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * demot.dd CRUD。Unique Key 为复合键，按全量 UK 定位行。
 * <p>
 * Doris Unique 表禁止 UPDATE key 列（Only value columns of unique table could be updated），
 * 因此 {@link #updateByUk} 会先清空实体上的 UK 字段再写入。
 * </p>
 */
@Service
public class DdService extends ServiceImpl<DdMapper, Dd> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public QueryWrapper<Dd> ukWrapper(Dd key) {
        return new QueryWrapper<Dd>()
                .eq("company_id", key.getCompany_id())
                .eq("dj_type", key.getDj_type())
                .eq("dj_type_sub", key.getDj_type_sub())
                .eq("dj_id", key.getDj_id())
                .eq("jl_id", key.getJl_id())
                .eq("mt_id", key.getMt_id())
                .eq("mx_id", key.getMx_id());
    }

    public Dd getByUk(Dd key) {
        return getOne(ukWrapper(key), false);
    }

    public boolean insertRow(Dd row) {
        return save(row);
    }

    public boolean updateByUk(Dd row) {
        Dd patch = new Dd();
        BeanUtils.copyProperties(row, patch);
        clearUk(patch);
        return update(patch, ukWrapper(row));
    }

    public boolean deleteByUk(Dd key) {
        return remove(ukWrapper(key));
    }

    /**
     * 布匹库存明细表查询（对齐 InventoryMapper.bpkcMxb 动态参数路径）。
     */
    public List<Map<String, Object>> queryBpkcMxb(BpkcMxbQueryParam param) {
        BpkcMxbSql.BuiltQuery q = BpkcMxbSql.build(param);
        return jdbcTemplate.queryForList(q.sql, q.args);
    }

    private static void clearUk(Dd d) {
        d.setCompany_id(null);
        d.setDj_type(null);
        d.setDj_type_sub(null);
        d.setDj_id(null);
        d.setJl_id(null);
        d.setMt_id(null);
        d.setMx_id(null);
    }
}
