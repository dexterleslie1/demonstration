package com.future.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yyd.common.bean.ModelMapperUtil;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.PageResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户操作日志服务
 */
@Service
@Slf4j
public class OperationLogService {

    //    @Resource
//    OperationLogRepository operationLogRepository;
    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 添加操作日志
     *
     * @param userId        当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志
     * @param operatorId
     * @param passiveId
     * @param operationType
     * @param content
     */
    public void add(Long userId,
                    Long operatorId,
                    Long passiveId,
                    OperationType operationType,
                    String content) {
        Assert.isTrue(userId != null && userId > 0, "参数错误1");

        Assert.isTrue(operatorId != null && operatorId > 0, "参数错误2");

        Assert.isTrue(operationType != null, "参数错误3");

        // https://stackoverflow.com/questions/5026943/how-to-execute-insert-statement-using-jdbctemplate-class-from-spring-framework
        this.jdbcTemplate.update("insert into operation_log(auth_id,operator_id,passive_id,operation_type,content,create_time) " +
                "values(?,?,?,?,?,?)", userId, operatorId, passiveId, operationType.getValue(), content, new Date());
    }

    /**
     * 查询指定用户操作日志
     *
     * @param contextUserId
     * @param operationTypeList
     * @param page
     * @param size
     * @return
     */
    public PageResponse<OperationLogVo> list(Long contextUserId, List<OperationType> operationTypeList, int page, int size) throws BusinessException {
        if (page <= 0) {
            page = 1;
        }

        Assert.isTrue(size >= 1 && size <= 1000, "参数错误1");

//        String sqlSelect = "select now()";
//        String str1 = this.jdbcTemplate.queryForObject(sqlSelect, String.class);

        // https://stackoverflow.com/questions/34216124/jdbctemplate-count-queryforint-and-pass-multiple-parameters
        int start = (page - 1) * size;
        String sqlSelect = "select * from operation_log where auth_id=?";
        if (operationTypeList != null && operationTypeList.size() > 0) {
            sqlSelect = sqlSelect + " and operation_type in (?)";
        }
        sqlSelect = sqlSelect + " order by id desc limit ?,?";

        List<Map<String, Object>> resultSetList;
        if (operationTypeList != null && operationTypeList.size() > 0) {
            resultSetList = this.jdbcTemplate.queryForList(sqlSelect, contextUserId, operationTypeList, start, size);
        } else {
            resultSetList = this.jdbcTemplate.queryForList(sqlSelect, contextUserId, start, size);
        }

        if (resultSetList != null && resultSetList.size() > 0) {
            String sqlCount = "select count(id) from operation_log where auth_id=?";
            if (operationTypeList != null && operationTypeList.size() > 0) {
                sqlCount = sqlCount + " and operation_type in (?)";
            }

            Integer totalCount;
            if (operationTypeList != null && operationTypeList.size() > 0) {
                totalCount = this.jdbcTemplate.queryForObject(sqlCount, Integer.class, contextUserId, operationTypeList);
            } else {
                totalCount = this.jdbcTemplate.queryForObject(sqlCount, Integer.class, contextUserId);
            }

            List<OperationLogModel> modelList = resultSetList.stream().map(o -> {
                Long id = (Long) o.get("id");
                Long authId = (Long) o.get("auth_id");
                Long operatorId = (Long) o.get("operator_id");
                Long passiveId = (Long) o.get("passive_id");
                Integer operationTypeValue = (Integer) o.get("operation_type");
                OperationType operationType = OperationType.fromValue(operationTypeValue);
                String content = (String) o.get("content");
//                LocalDateTime createTimeLocalDateTime = (LocalDateTime) o.get("create_time");
//                Date createTime = Date.from(createTimeLocalDateTime.toInstant(ZoneOffset.ofHours(8)));

                Timestamp timestamp = (Timestamp)o.get("create_time");
                Date createTime = new Date(timestamp.getTime());

                OperationLogModel model = new OperationLogModel() {{
                    setId(id);
                    setAuthId(authId);
                    setOperatorId(operatorId);
                    setPassiveId(passiveId);
                    setOperationType(operationType);
                    setContent(content);
                    setCreateTime(createTime);
                }};
                return model;
            }).collect(Collectors.toList());

            List<OperationLogVo> operationLogVoList = modelList.stream().map(o -> {
                OperationLogVo vo = ModelMapperUtil.ModelMapperInstance.map(o, OperationLogVo.class);
                vo.setOperationType(o.getOperationType().getDescription());
                return vo;
            }).collect(Collectors.toList());

            PageResponse<OperationLogVo> response = new PageResponse<>(page, size, totalCount);
            response.setData(operationLogVoList);
            return response;
        }

        return new PageResponse<OperationLogVo>() {{
            setPageSize(size);
        }};
    }

    @ApiModel(value = "操作日志vo")
    @Data
    public static class OperationLogVo {
        @ApiModelProperty(value = "操作日志id")
        private Long id;
        @ApiModelProperty(value = "操作人帐号")
        private String operatorAccount;
        @ApiModelProperty(value = "操作类型")
        private String operationType;
        @ApiModelProperty(value = "操作内容")
        private String content;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @ApiModelProperty(value = "操作时间")
        private Date createTime;
    }

}
