<template>
  <div id="app">
    <div>默认插槽 - 父组件替换子组件默认插槽</div>
    <DefaultSlotComponent>
      <!-- 父组件传递的内容会替换子组件的默认插槽 -->
      <p>这是父组件传递的自定义内容！</p>
    </DefaultSlotComponent>
    <hr />

    <div>默认插槽 - 使用子组件的默认插槽</div>
    <DefaultSlotComponent>
      <!-- 父组件不传递内容会使用子组件的默认插槽 -->
    </DefaultSlotComponent>
    <hr />

    <div>具名插槽</div>
    <NamedSlotComponent>
      <!-- 头部内容：指定 name="header" -->
      <template #header>
        <h3>卡片标题</h3>
      </template>

      <!-- 主体内容：未指定 name 的默认插槽 -->
      <p>卡片主体内容...</p>

      <!-- 底部内容：指定 name="footer" -->
      <template v-slot:footer>
        <button>关闭</button>
      </template>
    </NamedSlotComponent>
    <hr />
    
  </div>
</template>

<script>
import DefaultSlotComponent from "@/components/DefaultSlotComponent.vue";
import NamedSlotComponent from "@/components/NamedSlotComponent.vue"

export default {
  name: 'App',
  components: {
    // 注意：必须注册组件，否则使用子组件默认插槽不生效
    DefaultSlotComponent,
    // 注意：必须注册组件，否则使用子组件默认插槽不生效
    NamedSlotComponent
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
