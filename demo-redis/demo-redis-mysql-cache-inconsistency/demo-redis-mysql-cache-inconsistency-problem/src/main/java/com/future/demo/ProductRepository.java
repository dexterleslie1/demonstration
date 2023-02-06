package com.future.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 * @author dexterleslie@gmail.com
 */
public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    @Transactional
    @Modifying
    @Query("update ProductModel entity set entity.quantity=entity.quantity-:quantity where entity.id=:id")
    void updateDecrease(@Param("id") Long id, @Param("quantity") int quantity);
}
