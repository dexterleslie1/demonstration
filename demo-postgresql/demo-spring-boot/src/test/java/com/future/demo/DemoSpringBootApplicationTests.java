package com.future.demo;

import com.future.demo.entity.User;
import com.future.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DemoSpringBootApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testCreateUser() {
        // 创建用户
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setName("Test User");
        
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void testReadUser() {
        // 创建用户
        User user = new User();
        user.setUsername("readuser");
        user.setEmail("read@example.com");
        user.setName("Read User");
        User savedUser = userRepository.save(user);
        
        // 根据ID查询
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("readuser", foundUser.get().getUsername());
        
        // 根据用户名查询
        Optional<User> foundByUsername = userRepository.findByUsername("readuser");
        assertTrue(foundByUsername.isPresent());
    }

    @Test
    void testUpdateUser() {
        // 创建用户
        User user = new User();
        user.setUsername("updateuser");
        user.setEmail("update@example.com");
        user.setName("Update User");
        User savedUser = userRepository.save(user);
        
        // 更新用户
        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@example.com");
        User updatedUser = userRepository.save(savedUser);
        
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        // 创建用户
        User user = new User();
        user.setUsername("deleteuser");
        user.setEmail("delete@example.com");
        user.setName("Delete User");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        
        // 删除用户
        userRepository.deleteById(userId);
        
        // 验证已删除
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testFindAllUsers() {
        // 创建多个用户
        userRepository.save(new User(null, "user1", "user1@example.com", "User 1", null));
        userRepository.save(new User(null, "user2", "user2@example.com", "User 2", null));
        
        // 查询所有用户
        List<User> users = userRepository.findAll();
        
        assertTrue(users.size() >= 2);
    }
}
