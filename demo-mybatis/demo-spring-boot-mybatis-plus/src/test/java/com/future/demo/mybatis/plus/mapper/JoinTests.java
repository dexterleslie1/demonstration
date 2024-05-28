package com.future.demo.mybatis.plus.mapper;

import com.future.demo.mybatis.plus.Application;
import com.future.demo.mybatis.plus.entity.Developer;
import com.future.demo.mybatis.plus.entity.DeveloperAndIpsetRelation;
import com.future.demo.mybatis.plus.entity.Ipset;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class JoinTests {
    @Resource
    DeveloperAndIpsetRelationMapper developerAndIpsetRelationMapper;
    @Resource
    DeveloperMapper developerMapper;
    @Resource
    IpsetMapper ipsetMapper;

    @Test
    public void test() {
        this.developerAndIpsetRelationMapper.delete(null);
        this.developerMapper.delete(null);
        this.ipsetMapper.delete(null);

        Developer developer = new Developer();
        developer.setName("dev1");
        developer.setCreateTime(new Date());
        this.developerMapper.insert(developer);
        long developerId1 = developer.getId();

        developer = new Developer();
        developer.setName("dev2");
        developer.setCreateTime(new Date());
        this.developerMapper.insert(developer);
        long developerId2 = developer.getId();

        Ipset ipset = new Ipset();
        ipset.setName("ipset1-1");
        ipset.setCreateTime(new Date());
        this.ipsetMapper.insert(ipset);
        long ipsetId11 = ipset.getId();
        ipset = new Ipset();
        ipset.setName("ipset1-2");
        ipset.setCreateTime(new Date());
        this.ipsetMapper.insert(ipset);
        long ipsetId12 = ipset.getId();

        ipset = new Ipset();
        ipset.setName("ipset2");
        ipset.setCreateTime(new Date());
        this.ipsetMapper.insert(ipset);
        long ipsetId2 = ipset.getId();

        DeveloperAndIpsetRelation relation = new DeveloperAndIpsetRelation();
        relation.setDeveloperId(developerId1);
        relation.setIpsetId(ipsetId11);
        relation.setCreateTime(new Date());
        this.developerAndIpsetRelationMapper.insert(relation);
        relation = new DeveloperAndIpsetRelation();
        relation.setDeveloperId(developerId1);
        relation.setIpsetId(ipsetId12);
        relation.setCreateTime(new Date());
        this.developerAndIpsetRelationMapper.insert(relation);

        relation = new DeveloperAndIpsetRelation();
        relation.setDeveloperId(developerId2);
        relation.setIpsetId(ipsetId2);
        relation.setCreateTime(new Date());
        this.developerAndIpsetRelationMapper.insert(relation);

        MPJLambdaWrapper<Developer> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper.
                // 查询所有Developer列
                selectAll(Developer.class)
                // 只查询DeveloperAndIpsetRelation ipsetId列
                .select(DeveloperAndIpsetRelation::getIpsetId)
                // Developer左连接DeveloperAndIpsetRelation使用DeveloperAndIpsetRelation.developerId=Developer.id
                .leftJoin(DeveloperAndIpsetRelation.class, DeveloperAndIpsetRelation::getDeveloperId, Developer::getId)
                .eq(DeveloperAndIpsetRelation::getIpsetId, ipsetId11);
        // selectOne返回一个领域对象
        Developer developerObject = this.developerMapper.selectJoinOne(Developer.class, mpjLambdaWrapper);
        Assert.assertEquals(developerId1, developerObject.getId().longValue());

        // selectOne返回一个Map<String, Object>包含指定select列
        Map<String, Object> mapObject = this.developerMapper.selectJoinMap(mpjLambdaWrapper);
        Assert.assertEquals(developerId1, mapObject.get("id"));
        Assert.assertEquals(ipsetId11, mapObject.get("ipsetId"));
    }
}
