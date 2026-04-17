package com.future.demo.sleuth.gateway.filter;

import brave.Span;
import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilterDemo implements GlobalFilter, Ordered {
    public static final String RESPONSE_HEADER_TRACE_ID = "x-trace-id";

    /**
     * 缓存在本次 {@link ServerWebExchange} 上的 traceId，供 {@code beforeCommit} 使用。
     * <p>
     * Spring Boot 2.7 + Spring Cloud 2021（Sleuth 3）下，Gateway 基于 Reactor，Brave 的
     * {@link Tracer#currentSpan()} 依赖当前订阅链上的上下文；而 {@code beforeCommit} 往往在
     * 响应提交阶段执行，可能已换线程或脱离 Reactor Context，导致 {@code currentSpan()} 为 null。
     * 因此在仍处在 Gateway 主 filter 链路上的本方法入口解析一次并写入 attribute，可避免该问题。
     */
    private static final String EXCHANGE_ATTR_TRACE_ID = GlobalFilterDemo.class.getName() + ".traceId";

    private final Tracer tracer;

    public GlobalFilterDemo(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String uri = exchange.getRequest().getURI().getPath();
        log.debug("请求uri=" + uri);

        /*
         * 在 filter 主链路上尽早解析 traceId 并写入 exchange attributes。
         * 此时 Sleuth 的全局过滤器通常已执行（本类 getOrder 为最低优先级，靠后执行），
         * tracer.currentSpan() 更可能处于有效作用域内。
         */
        String traceIdResolvedEarly = resolveTraceIdWhileInFilterChain(exchange);
        if (traceIdResolvedEarly != null && !traceIdResolvedEarly.isEmpty()) {
            exchange.getAttributes().put(EXCHANGE_ATTR_TRACE_ID, traceIdResolvedEarly);
        }

        /*
         * 目的：让网关返回的每个 HTTP 响应都带上 traceId，方便：
         * - 在浏览器 / Postman 侧快速定位一次请求对应的链路
         * - 线上排障时通过 traceId 直接跳转 Zipkin/日志查询
         *
         * 为什么仍用 beforeCommit：
         * - WebFlux 的响应头一旦 commit 就无法再修改
         * - 在这里设置 header，尽量贴近“最终提交响应”的时刻
         *
         * 为什么不再在 beforeCommit 里调用 tracer.currentSpan()：
         * - 该回调里常常拿不到 Brave/Reactor 上下文，currentSpan() 易为 null（尤其 Sleuth 3 + Gateway）
         * - traceId 已在上面解析并缓存到 exchange attributes，此处只读缓存或请求头兜底即可
         */
        exchange.getResponse().beforeCommit(() -> {
            // getAttribute 返回 Object，显式转为 String，避免不同 Spring 版本下泛型推断差异。
            String traceId = (String) exchange.getAttribute(EXCHANGE_ATTR_TRACE_ID);
            if (traceId == null || traceId.isEmpty()) {
                traceId = resolveTraceIdFromInboundHeadersOnly(exchange.getRequest().getHeaders());
            }
            if (traceId != null && !traceId.isEmpty()) {
                exchange.getResponse().getHeaders().set(RESPONSE_HEADER_TRACE_ID, traceId);
            }
            return Mono.empty();
        });

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        /*
         * 选择最低优先级：尽量让 Sleuth/Brave 的 tracing filter 先执行并建立当前 span。
         * 这样在 resolveTraceIdWhileInFilterChain() 中走到 tracer.currentSpan() 时更可能拿到有效 traceId。
         */
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 在 GlobalFilter 主执行路径上解析 traceId（可安全使用 tracer / MDC）。
     * 优先级：入站 X-B3-TraceId / b3 → Brave 当前 span → MDC。
     */
    private String resolveTraceIdWhileInFilterChain(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String fromHeaders = resolveTraceIdFromInboundHeadersOnly(headers);
        if (fromHeaders != null && !fromHeaders.isEmpty()) {
            return fromHeaders;
        }
        Span span = tracer.currentSpan();
        if (span != null) {
            return span.context().traceIdString();
        }
        return MDC.get("traceId");
    }

    /**
     * 仅从入站 HTTP 头解析 traceId，不依赖 Brave/Reactor 上下文。
     * 用于 beforeCommit 兜底：上游已传 B3 时仍可写出 x-trace-id。
     */
    private String resolveTraceIdFromInboundHeadersOnly(HttpHeaders headers) {
        String xB3 = headers.getFirst("X-B3-TraceId");
        if (xB3 != null && !xB3.isEmpty()) {
            return xB3;
        }
        // b3: {TraceId}-{SpanId}-{SamplingState}-{ParentSpanId}，此处只取第一段 TraceId。
        String b3 = headers.getFirst("b3");
        if (b3 != null && !b3.isEmpty()) {
            int dash = b3.indexOf('-');
            if (dash > 0) {
                return b3.substring(0, dash);
            }
            if (!b3.equals("0") && !b3.equals("1")) {
                return b3;
            }
        }
        return null;
    }
}
