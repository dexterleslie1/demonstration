# `spring-boot`集成`redisson`

示例的详细使用和配置请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/spring-boot-redisson-integration)

集成的关键配置解析：

- 项目`maven`的`pom.xml`配置

  ```xml
  <parent>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-parent</artifactId>
      <version>2.2.7.RELEASE</version>
  </parent>
  
  <dependency>
      <groupId>org.redisson</groupId>
      <artifactId>redisson</artifactId>
      <version>3.19.1</version>
  </dependency>
  ```

- `spring-boot`项目的`application.properties`配置

  ```properties
  spring.redis.host=localhost
  spring.redis.port=6379
  spring.redis.password=123456
  ```

- 创建`RedissonClient`

  ```java
  @Configuration
  public class ConfigRedis {
      @Value("${spring.redis.host}")
      private String redisHost = null;
      @Value("${spring.redis.port}")
      private int redisPort = 0;
      @Value("${spring.redis.password}")
      private String redisPassword = null;
  
      @Bean
      RedissonClient redissonClient(){
          Config config = new Config();
          SingleServerConfig singleServerConfig = config
                  .useSingleServer()
                  .setAddress("redis://"+redisHost+":"+redisPort);
          if(!StringUtils.isEmpty(redisPassword)) {
              singleServerConfig.setPassword(redisPassword);
          }
          return Redisson.create(config);
      }
  }
  ```

- 测试`RedissonClient`

  ```java
  @RunWith(SpringRunner.class)
  @SpringBootTest(classes={Application.class})
  @Slf4j
  public class Tests {
  	@Autowired
  	RedissonClient redissonClient;
  
  	@Test
  	public void test() {
  		String key = UUID.randomUUID().toString();
  
  		RLock rLock = this.redissonClient.getLock(key);
  		boolean acquired = false;
  		try {
  			acquired = rLock.tryLock();
  		} finally {
  			if(acquired) {
  				try {
  					rLock.unlock();
  				} catch (Exception ignored) {
  
  				}
  			}
  		}
  
  		Assert.assertTrue(acquired);
  	}
  }
  ```

  

