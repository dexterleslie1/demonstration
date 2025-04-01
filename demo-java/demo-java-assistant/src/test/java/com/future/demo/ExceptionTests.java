package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

@Slf4j
public class ExceptionTests {

    @Test
    @Ignore
    public void test() {
        Exception ex = this.customizeExceptionWithCauseException();
        ex.printStackTrace();

        log.error(ex.getMessage(), ex);
    }

    Exception customizeExceptionWithCauseException() {
        Exception cause = this.customizeExceptionReturnCauseException();
        if (cause != null) {
            return new Exception("My customize exception with cause exception", cause);
        }

        return null;
    }

    Exception customizeExceptionReturnCauseException() {
        boolean b = true;
        if (b) {
            return new Exception("My customize exception inner cause exception");
        }
        return null;
    }
}
