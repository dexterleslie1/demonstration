package com.future.demo.mybatis.plus.mapper;

import com.future.demo.mybatis.plus.Application;
import com.future.demo.mybatis.plus.entity.*;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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
    @Resource
    DeveloperInfoMapper developerInfoMapper;
    @Resource
    DeveloperLangMapper developerLangMapper;

    @Test
    public void test() {
        this.developerAndIpsetRelationMapper.delete(null);
        this.developerMapper.delete(null);
        this.ipsetMapper.delete(null);
        this.developerInfoMapper.delete(null);

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

        // region 测试多对多关系

        MPJLambdaWrapper<Developer> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                // 查询所有developer列
                .selectAll(Developer.class)
                // 查询developer关联的ipset集合并填充ipsetList集合
                .selectCollection(Ipset.class, Developer::getIpsetList)
                .leftJoin(DeveloperAndIpsetRelation.class, DeveloperAndIpsetRelation::getDeveloperId, Developer::getId)
                .leftJoin(Ipset.class, Ipset::getId, DeveloperAndIpsetRelation::getIpsetId)
                .eq(Ipset::getId, ipsetId11);

        List<Developer> developerList = developerMapper.selectJoinList(Developer.class, mpjLambdaWrapper);
        Assert.assertEquals(1, developerList.size());
        Assert.assertEquals(developerId1, developerList.get(0).getId().longValue());
        Assert.assertEquals(1, developerList.get(0).getIpsetList().size());
        Assert.assertEquals(ipsetId11, developerList.get(0).getIpsetList().get(0).getId().longValue());

        // endregion

        DeveloperInfo developerInfo = new DeveloperInfo();
        developerInfo.setDeveloperId(developerId1);
        developerInfo.setAge(21);
        developerInfoMapper.insert(developerInfo);

        developerInfo = new DeveloperInfo();
        developerInfo.setDeveloperId(developerId2);
        developerInfo.setAge(31);
        developerInfoMapper.insert(developerInfo);

        // region 测试一对一关系

        mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                // 查询developer所有字段
                .selectAll(Developer.class)
                // 查询developer_info表所有字段并填充到Developer类info成员中
                .selectAssociation(DeveloperInfo.class, Developer::getInfo)
                // 关联查询：left join on developer_info.developerId=developer.id
                .leftJoin(DeveloperInfo.class, DeveloperInfo::getDeveloperId, Developer::getId)
                .eq(Developer::getId, developerId1);
        developer = developerMapper.selectJoinOne(Developer.class, mpjLambdaWrapper);
        Assert.assertEquals(developerId1, developer.getId().longValue());
        Assert.assertEquals(developerId1, developer.getInfo().getDeveloperId().longValue());
        Assert.assertEquals(21, developer.getInfo().getAge().intValue());

        // endregion

        DeveloperLang lang = new DeveloperLang();
        lang.setDeveloperId(developerId1);
        lang.setLang("EN");
        developerLangMapper.insert(lang);
        lang = new DeveloperLang();
        lang.setDeveloperId(developerId1);
        lang.setLang("CN");
        developerLangMapper.insert(lang);

        lang = new DeveloperLang();
        lang.setDeveloperId(developerId2);
        lang.setLang("EN");
        developerLangMapper.insert(lang);
        lang = new DeveloperLang();
        lang.setDeveloperId(developerId2);
        lang.setLang("CN");
        developerLangMapper.insert(lang);

        // region 测试一对多查询

        mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                .selectAll(Developer.class)
                .selectCollection(DeveloperLang.class, Developer::getLangList)
                .leftJoin(DeveloperLang.class, DeveloperLang::getDeveloperId, Developer::getId)
                .eq(Developer::getId, developerId1);
        developer = developerMapper.selectJoinOne(Developer.class, mpjLambdaWrapper);
        Assert.assertEquals(developerId1, developer.getId().longValue());
        Assert.assertEquals(2, developer.getLangList().size());
        Assert.assertEquals("EN", developer.getLangList().get(0).getLang());
        Assert.assertEquals("CN", developer.getLangList().get(1).getLang());

        // endregion
    }
}
