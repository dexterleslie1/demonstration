package com.future.demo.robot.detection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ApplicationLifecycle {

    /**
     *
     */
    protected abstract void doStartup() throws Exception;

    /**
     *
     */
    protected void startup(){
        try{
            this.doStartup();
        }catch(Exception ex){
            log.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    /**
     *
     */
    protected abstract void doShutdown() throws Exception;

    /**
     *
     */
    protected void shutdown(){
        try{
            this.doShutdown();
        }catch(Exception ex){
            log.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }
}
