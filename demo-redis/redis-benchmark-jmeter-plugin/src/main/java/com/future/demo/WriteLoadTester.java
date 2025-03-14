//package com.future.demo;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//import java.util.UUID;
//
///**
// * @author dexterleslie@gmail.com
// */
//public class WriteLoadTester {
//    private JedisConnectionFactory connectionFactory;
//    private StringRedisTemplate redisTemplate = null;
////    private ValueOperations<String,String> valueOperations = null;
////    private RedisAtomicInteger atomicInteger = null;
////    private Random random = new Random();
////    private final static List<Integer> sex = new ArrayList<Integer>();
////    static {
////        sex.add(0);
////        sex.add(1);
////        sex.add(2);
////    }
//
//    public void setup() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
//        configuration.setHostName("127.0.0.1");
//        configuration.setPort(6379);
//        configuration.setPassword(RedisPassword.of("aa112233"));
////        JedisConnectionFactory connectionFactory=new JedisConnectionFactory(configuration);
////        RedisClusterConfiguration configuration = new RedisClusterConfiguration(
////                Arrays.asList("192.168.1.130:6379",
////                        "192.168.1.131:6379",
////                        "192.168.1.132:6379",
////                        "192.168.1.133:6379",
////                        "192.168.1.134:6379",
////                        "192.168.1.135:6379")
////        );
////        configuration.setPassword(RedisPassword.of("aa112233"));
//        connectionFactory = new JedisConnectionFactory(configuration);
//        connectionFactory.afterPropertiesSet();
//        redisTemplate = new StringRedisTemplate();
//        redisTemplate.setConnectionFactory(connectionFactory);
//        redisTemplate.afterPropertiesSet();
////        valueOperations = redisTemplate.opsForValue();
////        atomicInteger = new RedisAtomicInteger("id#user", this.redisTemplate.getConnectionFactory());
//    }
//
//    final static String Key = UUID.randomUUID().toString();
//
//    public void doInsert() throws JsonProcessingException {
////        int id = atomicInteger.incrementAndGet();
////        java.sql.Date date = new java.sql.Date(new Date().getTime());
////        String randomString = RandomUtils.getCharacterAndNumber(20);
////        String loginname = "ln" + randomString;
////        String nickname = "nn" + randomString;
////        String phone = "135" + randomString;
////        String email = "gm" + randomString + "@gmail.com";
////        User user = new User();
////        user.setId(id);
////        user.setCreateTime(date);
////        user.setLoginname(loginname);
////        user.setNickname(nickname);
////        user.setPassword(randomString);
////        user.setPhone(phone);
////        user.setEmail(email);
////        int size = sex.size();
////        int index = random.nextInt(size);
////        user.setSex(sex.get(index));
////
////        ObjectMapper objectMapper = new ObjectMapper();
////        String json = objectMapper.writeValueAsString(user);
////        this.valueOperations.set(String.valueOf(id), json);
//        this.redisTemplate.opsForValue().set(Key, Key);
//    }
//
//    public void teardown() {
//        if (this.connectionFactory != null) {
//            this.connectionFactory.destroy();
//        }
//    }
//}
