package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.TestAnnotationAndXmlMapperModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestAnnotationAndXmlMapper {
    @Delete("truncate table tb_test_annotation_and_xml_mapper")
    void truncate();

    @Insert("insert into tb_test_annotation_and_xml_mapper(id,flag) values(NULL,#{model.flag})")
    void add(@Param("model") TestAnnotationAndXmlMapperModel model);

    // 接口的名字和映射文件的名字相同，接口中方法的名字和要调用的映射文件中的标签的id相同。
    void add1(@Param("model") TestAnnotationAndXmlMapperModel model);

    @Select("select * from tb_test_annotation_and_xml_mapper order by id asc")
    List<TestAnnotationAndXmlMapperModel> findAll();
}
