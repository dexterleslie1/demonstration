package com.future.demo;

import redis.clients.jedis.ClusterPipeline;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipelineContextHolder {

    /**
     * 存储 ClusterPipeline 实例
     */
    private final static ThreadLocal<ClusterPipeline> contextHolderPipeline = new ThreadLocal<>();
    /**
     * 存储 Response 实例
     */
    private final static ThreadLocal<List<ResponseWithContext>> contextHolderResponse = new ThreadLocal<>();

    /**
     * 设置 ClusterPipeline 实例
     *
     * @param jedisCluster
     * @return
     */
    public static void setupPipeline(JedisCluster jedisCluster) {
        ClusterPipeline pipeline = contextHolderPipeline.get();
        if (pipeline == null) {
            pipeline = jedisCluster.pipelined();
            contextHolderPipeline.set(pipeline);
        }
    }

    /**
     * 关闭 ClusterPipeline 实例并检查管道中命令的执行结果，如果有执行失败则抛出异常
     */
    public static void closePipeline() {
        try {
            ClusterPipeline pipeline = contextHolderPipeline.get();
            if (pipeline != null) {
                pipeline.close();
            }

            List<ResponseWithContext> responseList = contextHolderResponse.get();
            if (responseList != null && !responseList.isEmpty()) {
                for (ResponseWithContext context : responseList) {
                    try {
                        context.getResponse().get();
                    } catch (Exception ex) {
                        // 附加调用栈和命令上下文到异常信息
                        String errorMsg = String.format(
                                "命令执行失败！\n" +
                                        "命令类型: %s\n" +
                                        "命令参数: %s\n" +
                                        "调用栈:\n%s",
                                context.getCommandType(),
                                Arrays.toString(context.getCommandArgs()),
                                context.getFormattedStackTrace()
                        );
                        throw new JedisDataException(errorMsg, ex);  // 包装原始异常并抛出
                    }
                }
            }
        } finally {
            // 释放内存，防止内存泄漏
            contextHolderPipeline.remove();
            contextHolderResponse.remove();
        }
    }

    /**
     * 添加 Response
     *
     * @param response
     * @param commandType
     * @param commandArgs
     * @param stackTrace  通过 Thread.currentThread().getStackTrace() 获取
     */
    public static void addResponse(Response<?> response,
                                   String commandType,
                                   Object[] commandArgs,
                                   StackTraceElement[] stackTrace) {
        List<ResponseWithContext> responseList = contextHolderResponse.get();
        if (responseList == null) {
            responseList = new ArrayList<>();
            contextHolderResponse.set(responseList);
        }
        responseList.add(new ResponseWithContext(
                response,
                commandType,
                commandArgs,
                stackTrace
        ));
    }

    /**
     * 包装 Pipeline 中的 Response，附加调用栈和命令上下文信息
     */
    public static class ResponseWithContext {
        private final Response<?> response;       // 原始 Response 对象
        private final String commandType;         // 命令类型（如 "SET", "HSET"）
        private final Object[] commandArgs;       // 命令参数（如 [key, value]）
        private final StackTraceElement[] stackTrace; // 调用时的栈轨迹

        public ResponseWithContext(Response<?> response, String commandType, Object[] commandArgs, StackTraceElement[] stackTrace) {
            this.response = response;
            this.commandType = commandType;
            this.commandArgs = commandArgs;
            // 捕获当前调用栈（排除当前构造方法本身的栈帧）
            this.stackTrace = stackTrace;
        }

        // 获取原始 Response
        public Response<?> getResponse() {
            return response;
        }

        // 获取命令类型（如 "SET"）
        public String getCommandType() {
            return commandType;
        }

        // 获取命令参数（如 [uuidStr1, uuidStr1]）
        public Object[] getCommandArgs() {
            return commandArgs;
        }

        // 获取格式化的调用栈信息（字符串）
        public String getFormattedStackTrace() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            for (StackTraceElement element : stackTrace) {
                // 跳过构造方法和无关的栈帧（如 Thread.getStackTrace()）
                if (!element.getClassName().equals(Thread.class.getName())
                        && !element.getMethodName().equals("getStackTrace")) {
                    pw.println("\tat " + element);
                }
            }
            return sw.toString();
        }
    }

    public static void set(String key, String value) {
        StackTraceElement[] stackTrace = captureStackTrace();
        ClusterPipeline pipeline = contextHolderPipeline.get();
        Response<?> response = pipeline.set(key, value);
        PipelineContextHolder.addResponse(response, "set", new Object[]{key, value}, stackTrace);
    }

    public static void hset(String key, String field, String value) {
        StackTraceElement[] stackTrace = captureStackTrace();
        ClusterPipeline pipeline = contextHolderPipeline.get();
        Response<?> response = pipeline.hset(key, field, value);
        PipelineContextHolder.addResponse(response, "hset", new Object[]{key, field, value}, stackTrace);
    }


    public static void hincrByFloat(String key, String field, double value) {
        StackTraceElement[] stackTrace = captureStackTrace();
        ClusterPipeline pipeline = contextHolderPipeline.get();
        Response<?> response = pipeline.hincrByFloat(key, field, value);
        PipelineContextHolder.addResponse(response, "hincrByFloat", new Object[]{key, field, value}, stackTrace);
    }

    public static void zincrby(String key, double increment, String member) {
        StackTraceElement[] stackTrace = captureStackTrace();
        ClusterPipeline pipeline = contextHolderPipeline.get();
        Response<?> response = pipeline.zincrby(key, increment, member);
        PipelineContextHolder.addResponse(response, "zincrby", new Object[]{key, increment, member}, stackTrace);
    }

    public static void zadd(String key, double score, String member) {
        StackTraceElement[] stackTrace = captureStackTrace();
        ClusterPipeline pipeline = contextHolderPipeline.get();
        Response<?> response = pipeline.zadd(key, score, member);
        PipelineContextHolder.addResponse(response, "zadd", new Object[]{key, score, member}, stackTrace);
    }

    /**
     * 捕获并过滤调用栈（关键工具方法）
     * 过滤规则：跳过 Thread.getStackTrace() 和当前方法（如 set/hset）的栈帧
     */
    private static StackTraceElement[] captureStackTrace() {
        StackTraceElement[] originalStackTrace = Thread.currentThread().getStackTrace();
        // 通常需要跳过前 3 个栈帧（具体数量根据实际调用层级调整）
        // 栈帧顺序：[0] getStackTrace(), [1] captureStackTrace(), [2] set()/hset() 等方法, [3+] 用户调用位置
        int skipFrames = 3; // 跳过前 3 层（根据实际调试调整）
        if (originalStackTrace.length > skipFrames) {
            return Arrays.copyOfRange(originalStackTrace, skipFrames, originalStackTrace.length);
        } else {
            return originalStackTrace; // 防止数组越界
        }
    }
}
