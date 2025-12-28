<template>
  <div class="less-demo">
    <h2>Less 样式演示</h2>
    <p>这是一个使用 Less 样式的简单演示，展示了 Less 的特性。</p>
    
    <div class="less-features">
      <div class="feature">
        <h3>变量</h3>
        <div class="color-box primary"></div>
        <div class="color-box secondary"></div>
      </div>
      
      <div class="feature">
        <h3>嵌套</h3>
        <div class="nested-example">
          <div class="level-1">
            <div class="level-2">
              <div class="level-3">嵌套内容</div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="feature">
        <h3>混合</h3>
        <button class="btn-less primary">主要按钮</button>
        <button class="btn-less secondary">次要按钮</button>
      </div>
      
      <div class="feature">
        <h3>函数</h3>
        <div class="alert">普通提示</div>
        <div class="alert success">成功提示</div>
        <div class="alert error">错误提示</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LessDemo'
}
</script>

<style lang="less" scoped>
// 定义变量
@less-primary-color: #42b983;
@less-secondary-color: #e74c3c;
@less-bg-color: #f8f9fa;
@less-border-radius: 10px;
@less-spacing: 20px;
@less-font-family: 'Arial', sans-serif;

// 定义混合
.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.box-shadow(@offset: 0 2px 8px rgba(0, 0, 0, 0.1)) {
  box-shadow: @offset;
  transition: box-shadow 0.3s ease;
}

.btn-style(@color: @less-primary-color) {
  background-color: @color;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: @less-border-radius;
  cursor: pointer;
  font-size: 1em;
  transition: all 0.3s ease;
  
  &:hover {
    background-color: darken(@color, 10%);
    transform: translateY(-2px);
  }
  
  &:active {
    transform: translateY(0);
  }
}

// 主要样式
.less-demo {
  background-color: @less-bg-color;
  border-radius: @less-border-radius;
  padding: @less-spacing * 2;
  margin: @less-spacing auto;
  max-width: 800px;
  font-family: @less-font-family;
  .box-shadow();
  
  h2 {
    color: @less-primary-color;
    text-align: center;
    margin-bottom: @less-spacing;
    font-size: 2em;
  }
  
  p {
    text-align: center;
    margin-bottom: @less-spacing * 2;
    color: #666;
    font-size: 1.1em;
  }
}

// 特性展示区域
.less-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: @less-spacing;
  margin-top: @less-spacing * 2;
  
  .feature {
    background-color: white;
    padding: @less-spacing;
    border-radius: @less-border-radius;
    .box-shadow();
    
    h3 {
      color: @less-primary-color;
      margin-bottom: @less-spacing;
      text-align: center;
    }
  }
}

// 颜色盒子（变量演示）
.color-box {
  width: 80px;
  height: 80px;
  border-radius: @less-border-radius;
  margin: 0 auto @less-spacing;
  .box-shadow();
  
  &.primary {
    background-color: @less-primary-color;
  }
  
  &.secondary {
    background-color: @less-secondary-color;
  }
}

// 嵌套演示
.nested-example {
  .level-1 {
    background-color: lighten(@less-primary-color, 30%);
    padding: @less-spacing;
    border-radius: @less-border-radius;
    
    .level-2 {
      background-color: lighten(@less-primary-color, 20%);
      padding: @less-spacing;
      border-radius: @less-border-radius;
      
      .level-3 {
        background-color: @less-primary-color;
        color: white;
        padding: @less-spacing;
        border-radius: @less-border-radius;
        text-align: center;
      }
    }
  }
}

// 按钮样式（混合演示）
.btn-less {
  .btn-style();
  margin: 0 5px;
  
  &.secondary {
    .btn-style(@less-secondary-color);
  }
}

// 函数演示
.alert {
  padding: 10px 15px;
  border-radius: @less-border-radius;
  margin: 5px 0;
  text-align: center;
  font-weight: bold;
  
  &.success {
    background-color: lighten(@less-primary-color, 10%);
    color: darken(@less-primary-color, 30%);
  }
  
  &.error {
    background-color: @less-secondary-color;
    color: white;
  }
}
</style>