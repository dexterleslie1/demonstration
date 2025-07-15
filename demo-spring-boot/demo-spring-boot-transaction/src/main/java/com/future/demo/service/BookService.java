package com.future.demo.service;

import com.future.demo.bean.Book;
import com.future.demo.dao.AccountDao;
import com.future.demo.dao.BookDao;
import com.future.demo.exceptions.CustomCheckedException;
import com.future.demo.exceptions.SubCustomCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BookService {
    @Autowired
    AccountDao accountDao;
    @Autowired
    BookDao bookDao;

    /**
     * 购买图书
     *
     * @param userId
     * @param bookId
     * @param bookNum
     * @param throwException 是否抛出异常
     * @param sleepSeconds   睡眠秒数，模拟事务处理超时
     */
    @Transactional(timeout = 2/* 事务处理超过2秒失败并自动回滚 */,
            rollbackFor = Exception.class/* 检查型异常（Checked Exception）：默认情况下，检查型异常（即继承自`Exception`但不继承自`RuntimeException`的异常）不会导致事务回滚。如果你希望某个检查型异常也能导致事务回滚，你需要在`rollbackFor`属性中显式指定该异常类型 */,
            noRollbackFor = {
                    NullPointerException.class,/* 默认规则会回滚NullPointerException，使用noRollbackException修改此默认行为 */
                    // 默认回滚所有异常，在此指定不回滚SubCustomCheckedException
                    SubCustomCheckedException.class})
    public void buy(Long userId, Long bookId, Integer bookNum, boolean throwException, Integer sleepSeconds,
                    boolean throwNullPointerException) throws CustomCheckedException {
        // 查询图书信息
        Book book = this.bookDao.get(bookId);
        BigDecimal price = book.getPrice();

        // 总价钱
        BigDecimal totalPrice = price.multiply(new BigDecimal(bookNum));
        // 扣除余额
        accountDao.updateBalance(userId, totalPrice);

        if (sleepSeconds != null && sleepSeconds > 0) {
            try {
                Thread.sleep(1000 * sleepSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (throwException)
            throw new CustomCheckedException("模拟业务异常");

        // 扣除库存
        bookDao.updateStock(bookId, bookNum);

        if (throwNullPointerException)
            throw new NullPointerException("模拟空指针异常");
    }

    // 使用事务传播特性实现当业务抛出异常，只回滚余额，不回滚库存
    @Transactional(rollbackFor = CustomCheckedException.class)
    public void buyWithTxPropagation(Long userId, Long bookId, Integer bookNum) throws CustomCheckedException {
        // 查询图书信息
        Book book = this.bookDao.get(bookId);
        BigDecimal price = book.getPrice();

        // 总价钱
        BigDecimal totalPrice = price.multiply(new BigDecimal(bookNum));
        // 扣除余额
        accountDao.updateBalanceWithPropagationRequired(userId, totalPrice);

        // 扣除库存
        bookDao.updateStockWithPropagationRequiresNew(bookId, bookNum);

        throw new CustomCheckedException("模拟业务异常");
    }
}
