package com.future.demo.sleuth;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationServiceC.class})
public class BraveApiTests {
    @Autowired
    Tracer tracer;

    @Test
    public void test() {
//        // 新建一个全新的trace
//        Span span = tracer.newTrace();
//        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
//            log.debug("根Trace");
//        } catch (Throwable throwable) {
//            span.error(throwable);
//        } finally {
//            span.finish();
//        }

//        Span span = tracer.newTrace();
        TraceContext traceContext = TraceContext.newBuilder().traceId(6033130421003114317l).spanId(6033130421003114317l).build();
        traceContext.traceIdString();
        Span span = tracer.toSpan(traceContext);
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            log.debug("根Trace");

//            final Span finalSpan = span;
            long traceId = span.context().traceId();
            long spanId = span.context().spanId();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TraceContext traceContext = TraceContext.newBuilder().traceId(traceId).spanId(spanId).build();
                    Span spanNext = tracer.newChild(traceContext);
                    try (Tracer.SpanInScope spanInScope1 = tracer.withSpanInScope(spanNext)) {
                        log.debug(Thread.currentThread().getName() + " 输出日志");
                    } catch (Throwable throwable) {
                        spanNext.error(throwable);
                    } finally {
                        spanNext.finish();
                    }
                }
            });
            thread.start();

            Thread.sleep(1000);
        } catch (Throwable throwable) {
            span.error(throwable);
        } finally {
            span.finish();
        }
    }
}
