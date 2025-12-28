<template>
  <div class="scss-demo">
    <h2>SCSS 样式演示</h2>
    <p>这是一个使用 SCSS 样式的简单演示，展示了 SCSS 的特性。</p>
    
    <div class="scss-features">
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
        <h3>混合宏</h3>
        <button class="btn-scss primary">主要按钮</button>
        <button class="btn-scss secondary">次要按钮</button>
      </div>
      
      <div class="feature">
        <h3>继承</h3>
        <div class="alert">普通提示</div>
        <div class="alert success">成功提示</div>
        <div class="alert error">错误提示</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScssDemo'
}
</script>

<style lang="scss" scoped>
// 定义变量
$scss-primary-color: #3498db;
$scss-secondary-color: #e74c3c;
$scss-bg-color: #f8f9fa;
$scss-border-radius: 10px;
$scss-spacing: 20px;
$scss-font-family: 'Arial', sans-serif;

// 定义混合宏
@mixin flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

@mixin box-shadow($offset: 0 2px 8px rgba(0, 0, 0, 0.1)) {
  box-shadow: $offset;
  transition: box-shadow 0.3s ease;
}

@mixin btn-style($color: $scss-primary-color) {
  background-color: $color;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: $scss-border-radius;
  cursor: pointer;
  font-size: 1em;
  transition: all 0.3s ease;
  
  &:hover {
    background-color: darken($color, 10%);
    transform: translateY(-2px);
  }
  
  &:active {
    transform: translateY(0);
  }
}

// 主要样式
.scss-demo {
  background-color: $scss-bg-color;
  border-radius: $scss-border-radius;
  padding: $scss-spacing * 2;
  margin: $scss-spacing auto;
  max-width: 800px;
  font-family: $scss-font-family;
  @include box-shadow;
  
  h2 {
    color: $scss-primary-color;
    text-align: center;
    margin-bottom: $scss-spacing;
    font-size: 2em;
  }
  
  p {
    text-align: center;
    margin-bottom: $scss-spacing * 2;
    color: #666;
    font-size: 1.1em;
  }
}

// 特性展示区域
.scss-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: $scss-spacing;
  margin-top: $scss-spacing * 2;
  
  .feature {
    background-color: white;
    padding: $scss-spacing;
    border-radius: $scss-border-radius;
    @include box-shadow;
    
    h3 {
      color: $scss-primary-color;
      margin-bottom: $scss-spacing;
      text-align: center;
    }
  }
}

// 颜色盒子（变量演示）
.color-box {
  width: 80px;
  height: 80px;
  border-radius: $scss-border-radius;
  margin: 0 auto $scss-spacing;
  @include box-shadow;
  
  &.primary {
    background-color: $scss-primary-color;
  }
  
  &.secondary {
    background-color: $scss-secondary-color;
  }
}

// 嵌套演示
.nested-example {
  .level-1 {
    background-color: lighten($scss-primary-color, 30%);
    padding: $scss-spacing;
    border-radius: $scss-border-radius;
    
    .level-2 {
      background-color: lighten($scss-primary-color, 20%);
      padding: $scss-spacing;
      border-radius: $scss-border-radius;
      
      .level-3 {
        background-color: $scss-primary-color;
        color: white;
        padding: $scss-spacing;
        border-radius: $scss-border-radius;
        text-align: center;
      }
    }
  }
}

// 按钮样式（混合宏演示）
.btn-scss {
  @include btn-style;
  margin: 0 5px;
  
  &.secondary {
    @include btn-style($scss-secondary-color);
  }
}

// 继承演示
.alert {
  padding: 10px 15px;
  border-radius: $scss-border-radius;
  margin: 5px 0;
  text-align: center;
  font-weight: bold;
  
  &.success {
    @extend .alert;
    background-color: #2ecc71;
    color: white;
  }
  
  &.error {
    @extend .alert;
    background-color: $scss-secondary-color;
    color: white;
  }
}
</style>