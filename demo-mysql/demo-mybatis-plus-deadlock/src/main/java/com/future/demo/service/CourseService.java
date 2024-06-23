package com.future.demo.service;

import com.future.demo.entity.Course;
import com.future.demo.mapper.CourseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class CourseService {
    @Autowired
    CourseMapper courseMapper;

    @Transactional
    public void tx1(Long id) throws RuntimeException {
        Course course = courseMapper.selectLockInShareMode(id);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        course.setName(course.getName() + UUID.randomUUID());
        courseMapper.updateById(course);

        throw new RuntimeException("防止修改数据，事务回滚");
    }

    @Transactional
    public void tx2(Long id) throws RuntimeException {
        try {
            Course course = courseMapper.selectLockInShareMode(id);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            course.setName(course.getName() + UUID.randomUUID());
            courseMapper.updateById(course);
        } catch (DeadlockLoserDataAccessException ex) {
            // todo 研究是否可以打印对应mysql的事务id
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
}
