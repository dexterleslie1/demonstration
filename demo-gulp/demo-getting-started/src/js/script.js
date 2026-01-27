// 示例JavaScript代码
console.log('Gulp示例项目初始化');

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    const demoBtn = document.getElementById('demo-btn');
    const demoResult = document.getElementById('demo-result');
    
    // 按钮点击事件
    demoBtn.addEventListener('click', function() {
        console.log('按钮被点击');
        
        // 显示当前时间
        const currentTime = new Date().toLocaleString();
        demoResult.innerHTML = `
            <h3>JavaScript执行结果</h3>
            <p>当前时间：${currentTime}</p>
            <p>Gulp构建成功！</p>
        `;
        
        // 添加动画效果
        demoResult.style.opacity = '0';
        setTimeout(() => {
            demoResult.style.transition = 'opacity 0.5s ease';
            demoResult.style.opacity = '1';
        }, 100);
    });
    
    // 初始化页面
    function initPage() {
        console.log('页面初始化完成');
        demoResult.innerHTML = '<p>点击上方按钮查看效果</p>';
    }
    
    // 调用初始化函数
    initPage();
});

// 工具函数
function formatDate(date) {
    return date.toISOString().split('T')[0];
}

function calculateSum(a, b) {
    return a + b;
}