package com.future.demo.jpa;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.NonUniqueResultException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
@Slf4j
public class TransactionTrackingRepositoryTests {
	@Autowired
	private TransactionTrackingRepository transactionTrackingRepository;

	@Test
	public void testNonUniqueResultException() {
		this.transactionTrackingRepository.deleteAll();

		for(int i=1; i<=3; i++) {
			TransactionTrackingModel model = new TransactionTrackingModel();
			String trackingId = UUID.randomUUID().toString();
			String payload = UUID.randomUUID().toString();
			model.setId(i);
			model.setTrackingId(trackingId);
			model.setCreateTime(new Date());
			model.setPayload(payload);
			model.setStatus(TransactionStatus.PENDING);
			model.setType(TransactionTrackingType.UserBalanceRecharge);
			this.transactionTrackingRepository.save(model);
		}

		try {
			this.transactionTrackingRepository.findByType(TransactionTrackingType.UserBalanceRecharge);
			Assert.fail("预期异常没有抛出");
		} catch(IncorrectResultSizeDataAccessException ex) {
			Assert.assertTrue(ex.getCause() instanceof NonUniqueResultException);
		}

		TransactionTrackingModel model = this.transactionTrackingRepository.findFirstByTypeOrderByIdDesc(TransactionTrackingType.UserBalanceRecharge);
		Assert.assertEquals(3, model.getId());
	}

	/**
	 *
	 */
	@Test
	public void test() {
		// 删除所有数据
		this.transactionTrackingRepository.deleteAll();

		TransactionTrackingModel model = new TransactionTrackingModel();
		String trackingId = UUID.randomUUID().toString();
		String payload = UUID.randomUUID().toString();
		model.setTrackingId(trackingId);
		model.setCreateTime(new Date());
		model.setPayload(payload);
		model.setStatus(TransactionStatus.PENDING);
		model.setType(TransactionTrackingType.UserBalanceRecharge);
		this.transactionTrackingRepository.save(model);

		int totalCharge = 5;
		for(int i=0; i<totalCharge; i++) {
			String trackingIdTemporary = UUID.randomUUID().toString();
			String payloadTemporary = UUID.randomUUID().toString();
			model.setTrackingId(trackingIdTemporary);
			model.setCreateTime(new Date());
			model.setPayload(payloadTemporary);
			model.setStatus(TransactionStatus.PENDING);
			model.setType(TransactionTrackingType.UserBalanceCharge);
			this.transactionTrackingRepository.save(model);
		}

		model = this.transactionTrackingRepository.findByTrackingId(trackingId);
		Assert.assertNotNull(model);
		Assert.assertEquals(payload, model.getPayload());

		List<TransactionTrackingModel> modelList =
				this.transactionTrackingRepository.findByTypeAndStatus(
						TransactionTrackingType.UserBalanceRecharge,
						TransactionStatus.PENDING);
		Assert.assertEquals(1, modelList.size());

		model.setStatus(TransactionStatus.SUCCEED);
		this.transactionTrackingRepository.save(model);
		model = this.transactionTrackingRepository.findByTrackingId(trackingId);
		Assert.assertEquals(TransactionStatus.SUCCEED, model.getStatus());

		this.transactionTrackingRepository.delete(model);
		model = this.transactionTrackingRepository.findByTrackingId(trackingId);
		Assert.assertNull(model);

		int count = this.transactionTrackingRepository.countByTypeAndStatus(
				TransactionTrackingType.UserBalanceCharge,
				TransactionStatus.PENDING);
		Assert.assertEquals(totalCharge, count);
	}

	@Test
	public void testPagination() {
		// 删除所有数据
		this.transactionTrackingRepository.deleteAll();

		int totalRecordPending = 27;

		for(int i=0; i<totalRecordPending; i++) {
			TransactionTrackingModel model = new TransactionTrackingModel();
			String trackingId = UUID.randomUUID().toString();
			String payload = UUID.randomUUID().toString();
			model.setTrackingId(trackingId);
			model.setCreateTime(new Date());
			model.setPayload(payload);
			model.setStatus(TransactionStatus.PENDING);
			model.setType(TransactionTrackingType.UserBalanceRecharge);
			this.transactionTrackingRepository.save(model);
		}

		// 第一、二页
		for(int page=1; page<=2; page++) {
			int pageSize = 10;
			Pageable pageable = PageRequest.of(page - 1, pageSize);
			Page<TransactionTrackingModel> pagePending =
					this.transactionTrackingRepository.findByStatus(TransactionStatus.PENDING, pageable);
			// 当前页码
			Assert.assertEquals(page - 1, pagePending.getNumber());
			// 每页记录数
			Assert.assertEquals(pageSize, pagePending.getSize());
			// 当前页记录数
			Assert.assertEquals(pageSize, pagePending.getNumberOfElements());
			// 总记录数
			Assert.assertEquals(totalRecordPending, pagePending.getTotalElements());
			// 总页数
			int totalPage = totalRecordPending % pageSize == 0 ? (totalRecordPending / pageSize) : ((totalRecordPending + pageSize) / pageSize);
			Assert.assertEquals(totalPage, pagePending.getTotalPages());
		}

		// 第三页
		int pageSize = 10;
		int page = 3;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<TransactionTrackingModel> pagePending =
				this.transactionTrackingRepository.findByStatus(TransactionStatus.PENDING, pageable);
		// 当前页码
		Assert.assertEquals(page - 1, pagePending.getNumber());
		// 每页记录数
		Assert.assertEquals(pageSize, pagePending.getSize());
		// 当前页记录数
		Assert.assertEquals(totalRecordPending-2*pageSize, pagePending.getNumberOfElements());
		// 总记录数
		Assert.assertEquals(totalRecordPending, pagePending.getTotalElements());
		// 总页数
		int totalPage = totalRecordPending % pageSize == 0 ? (totalRecordPending / pageSize) : ((totalRecordPending + pageSize) / pageSize);
		Assert.assertEquals(totalPage, pagePending.getTotalPages());
	}

	@Test
	public void testOrderBy() {
		// 删除所有数据
		this.transactionTrackingRepository.deleteAll();

		TransactionTrackingModel model = new TransactionTrackingModel();
		String trackingId = UUID.randomUUID().toString();
		String payload = UUID.randomUUID().toString();
		model.setTrackingId(trackingId);
		model.setCreateTime(new Date());
		model.setPayload(payload);
		model.setStatus(TransactionStatus.PENDING);
		model.setType(TransactionTrackingType.UserBalanceRecharge);
		this.transactionTrackingRepository.save(model);

		int totalCharge = 5;
		for(int i=0; i<totalCharge; i++) {
			String trackingIdTemporary = UUID.randomUUID().toString();
			String payloadTemporary = UUID.randomUUID().toString();
			model.setTrackingId(trackingIdTemporary);
			model.setCreateTime(new Date());
			model.setPayload(payloadTemporary);
			model.setStatus(TransactionStatus.PENDING);
			model.setType(TransactionTrackingType.UserBalanceCharge);
			this.transactionTrackingRepository.save(model);
		}

		List<TransactionTrackingModel> modelAll = this.transactionTrackingRepository.findAll();
		long id = 0;
		for(TransactionTrackingModel modelTemporary : modelAll) {
			if(id<=0) {
				id = modelTemporary.getId();
			}

			if(modelTemporary.getId()>id) {
				id = modelTemporary.getId();
			}
		}

		List<TransactionTrackingModel> modelsOrderById = this.transactionTrackingRepository.findByStatusOrderByIdDesc(TransactionStatus.PENDING);
		Assert.assertEquals(id, modelsOrderById.get(0).getId());
	}

	@Test
	public void updateStatusByTrackingId() {
		TransactionTrackingModel model = new TransactionTrackingModel();
		String trackingId = UUID.randomUUID().toString();
		String payload = UUID.randomUUID().toString();
		model.setTrackingId(trackingId);
		model.setCreateTime(new Date());
		model.setPayload(payload);
		model.setStatus(TransactionStatus.PENDING);
		model.setType(TransactionTrackingType.UserBalanceRecharge);
		this.transactionTrackingRepository.save(model);

		TransactionTrackingModel modelTemporary = this.transactionTrackingRepository.findByTrackingId(trackingId);
		Assert.assertEquals(TransactionStatus.PENDING, modelTemporary.getStatus());

		this.transactionTrackingRepository.updateStatusByTrackingId(trackingId, TransactionStatus.SUCCEED);
		modelTemporary = this.transactionTrackingRepository.findByTrackingId(trackingId);
		Assert.assertEquals(TransactionStatus.SUCCEED, modelTemporary.getStatus());

		this.transactionTrackingRepository.delete(modelTemporary);
	}

	@Test
	public void findByCreateTimeLessThanEqual() {
		Date endTime = new Date();
		List<TransactionTrackingModel> modelList = this.transactionTrackingRepository.findByCreateTimeLessThanEqual(endTime);
		log.debug(modelList.toString());
	}

	@Test
	public void countByCreateTimeLessThanEqual() {
		Date endTime = new Date();
		int count = this.transactionTrackingRepository.countByCreateTimeLessThanEqual(endTime);
		log.debug("count=" + count);
	}

	@Test
	public void findIdByCreateTimeLessThanEqual() {
		Date endTime = new Date();
		List<Long> idList = this.transactionTrackingRepository.findIdByCreateTimeLessThanEqual(endTime);
		log.debug(idList.toString());
	}

	@Test
	public void deleteByCreateTimeLessThanEqualLimitSize() {
		Date endTime = new Date();
		endTime = DateUtils.addDays(endTime, -365);
		this.transactionTrackingRepository.deleteByCreateTimeLessThanEqualLimitSize(endTime, 100);
	}
}