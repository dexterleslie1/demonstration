
// 测试 ok
QUnit.test( "hello test"/* 单元测试显示的标题 */, function( assert ) {
    // 断言两个字符串相等，否则报告错误
    assert.ok( "hello world" == "hello world", "Test hello world" );
});

// 测试 equal
QUnit.test( "hello test", function( assert ) {
    assert.equal( 0, 0, "Zero, Zero; equal succeeds" );
    assert.equal( "", 0, "Empty, Zero; equal succeeds" );
    assert.equal( "", "", "Empty, Empty; equal succeeds" );
    assert.equal( 0, false, "Zero, false; equal succeeds" );

    assert.equal( "three", 3, "Three, 3; equal fails" );
    assert.equal( null, false, "null, false; equal fails" );
});

// 测试 deepEqual
QUnit.test( "deepEqual test", function( assert ) {
    var obj = { foo: "bar", k1: "v1" };
    assert.deepEqual( obj, { k1: "v1", foo: "bar" }, "Two objects can be the same in value" );
});

QUnit.test( "a test", function( assert ) {
    // 期望只执行一个断言
    assert.expect( 1 );

    var $body = $( "body" );

    $body.on( "click", function() {
        assert.ok( true, "body was clicked!" );
    });

    $body.trigger( "click" );
});

// 异步测试
// 使用assert.async()返回一个”done”函数，当操作结束的时候，调用这个函数。另外我在”done”函数调用结束之后，把body的click事件给移除了，这个是为了方便我在点击结果网页的时候，不要出发多次done函数。
QUnit.test( "async test", function( assert ) {
    var done = assert.async(); 
    var $body = $( "body" );

    $body.on( "click", function() {
        assert.ok( true, "body was clicked!" );
        done();
        $body.unbind('click');
    });

    setTimeout(function(){
        console.log("To click body")
        $body.trigger( "click" );
    }, 3000)
});

// 使用 start 和 stop 控制测试
QUnit.test( "A start and stop test", function( assert ) {
    QUnit.stop()
    var $body = $( "body" );

    $body.on( "click", function() {
        assert.ok( true, "body was clicked!" );
        QUnit.start();
        $body.unbind('click');
    });

    setTimeout(function(){
        console.log("To click body")
        $body.trigger( "click" );
    }, 3000)

});

// 在QUnit中可以对测试进行分组，并且可以指定只跑哪组测试。
// 分组需要使用QUnit.module()方法。我们可以将刚才我们测试的代码进行一个简单的分组。
QUnit.module("Group Async Test");

// 使用 QUnit.asyncTest 和 QUnit.start 控制测试
QUnit.asyncTest( "QUnit.asyncTest and QUnit.start test", function( assert ) {
    var $body = $( "body" );

    $body.on( "click", function() {
        assert.ok( true, "body was clicked!" );
        QUnit.start();
        $body.unbind('click');
    });

    setTimeout(function(){
        console.log("To click body")
        $body.trigger( "click" );
    }, 3000)

});

QUnit.module("Group DOM Test", {
    setup: function(){
        // 每个用例测试之前作准备
        console.log("Test setup");
    },
    teardown: function(){
        // 每个用例测试之后释放资源
        console.log("Test teardown");
    }
})

// 保持测试用例之间互不干扰很重要，如果要测试DOM修改，我们可以使用#qunit-fixture这个元素。#qunit-fixture就好比是拿来练级的小怪，每次打死，下次来又会满血复活。
// 这个元素中你可以写任何初始的HTML，也可以置空，每个test()结束，都会恢复初始值。
QUnit.test( "Appends a span", function( assert ) {
    var fixture = $( "#qunit-fixture" );

    fixture.append( "<span>hello!</span>" );
    assert.equal( $( "span", fixture ).length, 1, "div added successfully!" );
});
QUnit.test( "Appends a span again", function( assert ) {
    var fixture = $( "#qunit-fixture" );

    fixture.append("<span>hello!</span>" );
    assert.equal( $( "span", fixture ).length, 1, "span added successfully!" );
});

// 自定义断言
QUnit.assert.mod2 = function( value, expected, message ) {
    var actual = value % 2;
    this.push( actual === expected, actual, expected, message );
};
QUnit.test( "mod2", function( assert ) {
    assert.expect( 2 );
 
    assert.mod2( 2, 0, "2 % 2 == 0" );
    assert.mod2( 3, 1, "3 % 2 == 1" );
});