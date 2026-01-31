package com.future.demo.controller;

import com.future.demo.ObjectResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于协助JMeter、Gatling等工具测试
 */
@RequestMapping("/api/v1")
@RestController
public class UserController {

    // 存储用户信息：userId -> UserInfo
    private final Map<Long, UserInfo> userMap = new ConcurrentHashMap<>();
    
    // 存储用户好友列表：userId -> List<FriendInfo>
    private final Map<Long, List<FriendInfo>> friendsMap = new ConcurrentHashMap<>();
    
    // 存储 token：token -> userId
    private final Map<String, Long> tokenMap = new ConcurrentHashMap<>();
    
    // 存储用户登录信息：username -> password
    private final Map<String, String> userCredentials = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化测试用户数据
        initTestData();
    }

    private void initTestData() {
        // 创建测试用户
        for (long i = 1; i <= 10; i++) {
            String username = "user" + i;
            String password = "password" + i;
            userCredentials.put(username, password);
            
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(i);
            userInfo.setUsername(username);
            userInfo.setNickname("用户" + i);
            userInfo.setEmail(username + "@example.com");
            userInfo.setAvatar("https://example.com/avatar/" + i + ".jpg");
            userInfo.setPhone("1380000" + String.format("%04d", i));
            userMap.put(i, userInfo);
            
            // 创建好友列表
            List<FriendInfo> friends = new ArrayList<>();
            for (long j = 1; j <= 5; j++) {
                long friendId = (i + j - 1) % 10 + 1;
                if (friendId != i) {
                    FriendInfo friend = new FriendInfo();
                    friend.setFriendId(friendId);
                    friend.setFriendName("用户" + friendId);
                    friend.setFriendAvatar("https://example.com/avatar/" + friendId + ".jpg");
                    friend.setRemark("好友" + j);
                    friends.add(friend);
                }
            }
            friendsMap.put(i, friends);
        }
    }

    /**
     * 用户登录接口
     * POST /api/v1/login
     */
    @PostMapping("/login")
    public ObjectResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        ObjectResponse<LoginResponse> response = new ObjectResponse<>();
        
        String password = userCredentials.get(request.getUsername());
        if (password == null || !password.equals(request.getPassword())) {
            response.setErrorCode(401);
            response.setErrorMessage("用户名或密码错误");
            return response;
        }
        
        // 查找用户ID
        Long userId = userMap.values().stream()
                .filter(user -> user.getUsername().equals(request.getUsername()))
                .map(UserInfo::getUserId)
                .findFirst()
                .orElse(null);
        
        if (userId == null) {
            response.setErrorCode(401);
            response.setErrorMessage("用户不存在");
            return response;
        }
        
        // 生成 token
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, userId);
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUserId(userId);
        loginResponse.setUsername(request.getUsername());
        
        response.setData(loginResponse);
        return response;
    }

    /**
     * 获取好友列表接口
     * GET /api/v1/friends
     */
    @GetMapping("/friends")
    public ObjectResponse<List<FriendInfo>> getFriends(@RequestHeader(value = "Authorization", required = false) String token) {
        ObjectResponse<List<FriendInfo>> response = new ObjectResponse<>();
        
        Long userId = validateToken(token);
        if (userId == null) {
            response.setErrorCode(401);
            response.setErrorMessage("未授权，请先登录");
            return response;
        }
        
        List<FriendInfo> friends = friendsMap.getOrDefault(userId, new ArrayList<>());
        response.setData(friends);
        return response;
    }

    /**
     * 获取个人信息接口
     * GET /api/v1/profile
     */
    @GetMapping("/profile")
    public ObjectResponse<UserInfo> getProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        ObjectResponse<UserInfo> response = new ObjectResponse<>();
        
        Long userId = validateToken(token);
        if (userId == null) {
            response.setErrorCode(401);
            response.setErrorMessage("未授权，请先登录");
            return response;
        }
        
        UserInfo userInfo = userMap.get(userId);
        if (userInfo == null) {
            response.setErrorCode(404);
            response.setErrorMessage("用户不存在");
            return response;
        }
        
        response.setData(userInfo);
        return response;
    }

    /**
     * Form 参数提交演示：application/x-www-form-urlencoded
     * POST /api/v1/form-demo，参数：name、message
     */
    @PostMapping(value = "/form-demo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ObjectResponse<Map<String, String>> formDemo(@RequestParam String name, @RequestParam String message) {
        ObjectResponse<Map<String, String>> response = new ObjectResponse<>();
        response.setData(Map.of("name", name, "message", message));
        return response;
    }

    /**
     * 验证 token
     */
    private Long validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        // 支持 Bearer token 格式
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return tokenMap.get(token);
    }

    // 内部类：登录请求
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // 内部类：登录响应
    @Data
    public static class LoginResponse {
        private String token;
        private Long userId;
        private String username;
    }

    // 内部类：用户信息
    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String nickname;
        private String email;
        private String avatar;
        private String phone;
    }

    // 内部类：好友信息
    @Data
    public static class FriendInfo {
        private Long friendId;
        private String friendName;
        private String friendAvatar;
        private String remark;
    }
}
