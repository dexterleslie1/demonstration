package com.future.demo.pkg2;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.text.MessageFormat;
import java.util.Map;

public class CacheSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 获取 @EnableMyCache 缓存类型注解值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMyCache.class.getName());
        CacheType type = (CacheType) annotationAttributes.get("type");
        // 根据注解返回不同的缓存实现类名称，以实现动态地向Spring IoC容器中导入组件
        switch (type) {
            case Local: {
                return new String[]{LocalCacheService.class.getName()};
            }
            case Redis: {
                return new String[]{RedisCacheService.class.getName()};
            }
            default: {
                throw new RuntimeException(MessageFormat.format("unsupport cache type {0}", type.toString()));
            }
        }
    }
}
