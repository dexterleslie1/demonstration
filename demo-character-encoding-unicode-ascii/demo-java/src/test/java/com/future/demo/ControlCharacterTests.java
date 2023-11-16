package com.future.demo;

import org.junit.Test;

public class ControlCharacterTests {

    // NOTE: 使用mvn命令运行测试，使用idea运行测试无法看到效果
    // 什么是control character
    // https://en.wikipedia.org/wiki/Control_character
    @Test
    public void test() {

        //region carriage return

        // https://en.wikipedia.org/wiki/Carriage_return
        // CR 表示光标移动到行首

        System.out.println("Hello world!\r888");

        //endregion
    }
}
