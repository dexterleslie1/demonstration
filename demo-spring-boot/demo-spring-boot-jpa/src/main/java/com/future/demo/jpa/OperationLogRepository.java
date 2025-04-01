package com.future.demo.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperationLogRepository extends JpaRepository<OperationLogModel, Long> {

    @Query(
            value = "select model " +
                    "from OperationLogModel model where model.authId=:authId and model.operationType in(:operationTypeList) " +
                    "order by model.id desc",
            countQuery = "select count(model.id) from OperationLogModel model where model.authId=:authId and model.operationType in(:operationTypeList)")
    Page<OperationLogModel> find(
            @Param("authId") Long authId,
            @Param("operationTypeList") List<OperationType> operationTypeList,
            Pageable pageable);

    @Query(
            value = "select model " +
                    "from OperationLogModel model where model.authId=:authId " +
                    "order by model.id desc",
            countQuery = "select count(model.id) from OperationLogModel model where model.authId=:authId")
    Page<OperationLogModel> find(
            @Param("authId") Long authId,
            Pageable pageable);

}
