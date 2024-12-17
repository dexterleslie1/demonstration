package com.future.demo.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @author dexterleslie@gmail.com
 */
public interface TransactionTrackingRepository extends JpaRepository<TransactionTrackingModel, Long> {
    /**
     * 预期返回多于一条记录并且报错
     * @param type
     * @return
     */
    TransactionTrackingModel findByType(TransactionTrackingType type);
    /**
     * 只返回第一条记录
     * @return
     */
    TransactionTrackingModel findFirstByTypeOrderByIdDesc(TransactionTrackingType type);

    /**
     *
     * @param trackingId
     * @return
     */
    TransactionTrackingModel findByTrackingId(String trackingId);

    /**
     *
     * @param type
     * @return
     */
    List<TransactionTrackingModel> findByTypeAndStatus(
            TransactionTrackingType type,
            TransactionStatus status);

    /**
     *
     * @param type
     * @param status
     * @return
     */
    int countByTypeAndStatus(
            TransactionTrackingType type,
            TransactionStatus status);

    /**
     * 演示简单分页查询
     *
     * @param status
     * @param pageable
     * @return
     */
    @Query(
            value = "select model from TransactionTrackingModel model where model.status=:status order by model.id asc",
            countQuery = "select count(model.id) from TransactionTrackingModel model where model.status=:status")
    Page<TransactionTrackingModel> findByStatus(
            @Param("status") TransactionStatus status,
            Pageable pageable);

    List<TransactionTrackingModel> findByStatusOrderByIdDesc(TransactionStatus status);

    @Transactional
    @Modifying
    @Query("update TransactionTrackingModel entity set entity.status=:status where entity.trackingId=:trackingId")
    void updateStatusByTrackingId(@Param("trackingId") String trackingId,
                                  @Param("status") TransactionStatus status);

    List<TransactionTrackingModel> findByCreateTimeLessThanEqual(@Param("endTime") Date endTime);

    int countByCreateTimeLessThanEqual(@Param("endTime") Date endTime);

    /**
     * 查询单列
     * @param endTime
     * @return
     */
    @Query("select entity.id from TransactionTrackingModel entity where entity.createTime<=:endTime")
    List<Long> findIdByCreateTimeLessThanEqual(@Param("endTime") Date endTime);

    @Transactional
    @Modifying
    @Query(value = "delete from transaction_tracking where createTime<=:endTime limit :size", nativeQuery = true)
    void deleteByCreateTimeLessThanEqualLimitSize(@Param("endTime") Date endTime,
                                                  @Param("size") Integer size);
}
