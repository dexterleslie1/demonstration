package com.future.demo;

import com.future.demo.bean.Account;
import com.future.demo.bean.Book;
import com.future.demo.dao.AccountDao;
import com.future.demo.dao.BookDao;
import com.future.demo.exceptions.CustomCheckedException;
import com.future.demo.service.BookService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionTimedOutException;

import java.math.BigDecimal;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {

    @Autowired
    BookService bookService;
    @Autowired
    BookDao bookDao;
    @Autowired
    AccountDao accountDao;

    @Test
    public void contextLoads() throws CustomCheckedException, InterruptedException, ExecutionException {
        Long userId = 1L;
        Long bookId = 1L;
        Integer bookNum = 3;

        // 恢复数据
        this.accountDao.updateSetBalance(userId, new BigDecimal(10000));
        this.bookDao.updateSetStock(userId, 50);

        Book book = this.bookDao.get(bookId);
        Account account = this.accountDao.get(userId);
        this.bookService.buy(userId, bookId, bookNum, false, 0, false);
        // 验证书本库存
        Assertions.assertEquals(book.getStock() - bookNum, this.bookDao.get(bookId).getStock());
        // 验证账户余额
        Assertions.assertEquals(account.getBalance().subtract(book.getPrice().multiply(new BigDecimal(bookNum))), this.accountDao.get(userId).getBalance());

        // region 测试异常
        Account account1 = this.accountDao.get(userId);
        Book book1 = this.bookDao.get(bookId);

        // 测试抛出异常
        try {
            this.bookService.buy(userId, bookId, bookNum, true, 0, false);
            Assertions.fail();
        } catch (CustomCheckedException e) {
            Assertions.assertEquals(e.getMessage(), "模拟业务异常");
            Assertions.assertEquals(account1.getBalance(), this.accountDao.get(userId).getBalance());
            Assertions.assertEquals(book1.getStock(), this.bookDao.get(bookId).getStock());
        }

        // 测试超时
        try {
            this.bookService.buy(userId, bookId, bookNum, false, 3, false);
            Assertions.fail();
        } catch (TransactionTimedOutException e) {
            Assertions.assertEquals(account1.getBalance(), this.accountDao.get(userId).getBalance());
            Assertions.assertEquals(book1.getStock(), this.bookDao.get(bookId).getStock());
        }

        // 测试NullPointerException
        try {
            this.bookService.buy(userId, bookId, bookNum, false, 0, true);
            Assertions.fail();
        } catch (NullPointerException e) {
            // 验证书本库存
            Assertions.assertEquals(book1.getStock() - bookNum, this.bookDao.get(bookId).getStock());
            // 验证账户余额
            Assertions.assertEquals(account1.getBalance().subtract(book.getPrice().multiply(new BigDecimal(bookNum))), this.accountDao.get(userId).getBalance());
        }

        // endregion

        // region 测试事务隔离级别

        Account account2 = this.accountDao.get(userId);
        Book book2 = this.bookDao.get(bookId);

        // 测试读未提交隔离级别的存在脏读问题
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future future = executorService.submit(() -> {
            try {
                this.bookService.buy(userId, bookId, bookNum, true, 1, false);
            } catch (CustomCheckedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread.sleep(100);
        Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum)))
                , this.accountDao.getWithIsolationReadUncommitted(userId).getBalance());

        try {
            future.get();
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause().getCause() instanceof CustomCheckedException);
            Assertions.assertEquals(account2.getBalance(), this.accountDao.get(userId).getBalance());
            Assertions.assertEquals(book2.getStock(), this.bookDao.get(bookId).getStock());
        }

        // 测试读已提交隔离级别解决脏读问题
        future = executorService.submit(() -> {
            try {
                this.bookService.buy(userId, bookId, bookNum, false, 1, false);
            } catch (CustomCheckedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread.sleep(100);
        Assertions.assertEquals(account2.getBalance()
                , this.accountDao.getWithIsolationReadCommited(userId)[0].getBalance());

        future.get();
        Assertions.assertEquals(book2.getStock() - bookNum, this.bookDao.get(bookId).getStock());
        Assertions.assertEquals(account2.getBalance().subtract(book.getPrice().multiply(new BigDecimal(bookNum))), this.accountDao.get(userId).getBalance());

        // 测试读已提交隔离级别存在不可重复读问题
        account2 = this.accountDao.get(userId);
        book2 = this.bookDao.get(bookId);
        future = executorService.submit(() -> {
            try {
                this.bookService.buy(userId, bookId, bookNum, false, 0, false);
                Thread.sleep(200);
                this.bookService.buy(userId, bookId, bookNum, false, 0, false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread.sleep(100);
        Account[] accountWithIsolationReadCommitedArr = this.accountDao.getWithIsolationReadCommited(bookId);

        future.get();
        // 读取account余额同一个事物读取到两个不同的余额
        Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum))), accountWithIsolationReadCommitedArr[0].getBalance());
        Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(2 * bookNum))), accountWithIsolationReadCommitedArr[1].getBalance());

        // 测试可重复读隔离级别解决不可重复读问题
        account2 = this.accountDao.get(userId);
        book2 = this.bookDao.get(bookId);
        future = executorService.submit(() -> {
            try {
                this.bookService.buy(userId, bookId, bookNum, false, 0, false);
                Thread.sleep(200);
                this.bookService.buy(userId, bookId, bookNum, false, 0, false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread.sleep(100);
        Account[] accountArr = this.accountDao.getWithIsolationRepeatableRead(bookId);

        future.get();
        // 读取account余额同一个事物读取到两个不同的余额
        Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum))), accountArr[0].getBalance());
        Assertions.assertEquals(account2.getBalance().subtract(book2.getPrice().multiply(new BigDecimal(bookNum))), accountArr[1].getBalance());

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
        }

        // endregion

        // region 测试事务传播特性

        Account account3 = this.accountDao.get(bookId);
        Book book3 = this.bookDao.get(bookId);

        try {
            this.bookService.buyWithTxPropagation(userId, bookId, bookNum);
            Assertions.fail();
        } catch (CustomCheckedException ex) {
            Assertions.assertEquals(account3.getBalance(), this.accountDao.get(userId).getBalance());
            Assertions.assertEquals(book3.getStock() - bookNum, this.bookDao.get(bookId).getStock());
        }

        // endregion
    }
}
