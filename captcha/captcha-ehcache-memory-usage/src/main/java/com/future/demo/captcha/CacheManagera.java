package com.future.demo.captcha;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import java.io.InputStream;

public class CacheManagera {
    private CacheManager cacheManager=null;

    public CacheManagera(String name) throws Exception{
        InputStream is=null;
        try{
            is=Thread.currentThread().getContextClassLoader().getResourceAsStream("/ehcache.xml");
            if(is==null)
                is= com.future.demo.captcha.CacheManagera.class.getResourceAsStream("/ehcache.xml");
            cacheManager=CacheManager.newInstance(is);
        }catch(Exception ex){
            throw ex;
        }finally{
            if(is!=null)
                is.close();
        }
    }

    public CacheManager getCacheManager(){
        return this.cacheManager;
    }

    public void close(){
        if(this.cacheManager!=null)
            this.cacheManager.shutdown();
    }

    public Cache getCache(String name){
        return this.cacheManager.getCache(name);
    }
}
