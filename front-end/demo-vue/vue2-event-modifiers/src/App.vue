<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <!-- https://blog.csdn.net/I_r_o_n_M_a_n/article/details/120251951 -->
    <hr>
    <div>.prevent阻止默认事件，例如：a标签的跳转行为和form提交行为</div>
    <div>
      <a href="http://www.atguigu.com" @click.prevent="handleClick">点我提示信息</a>
    </div>

    <hr>
    <div>.stop阻止事件冒泡</div>
    <div>
      <!-- 阻止事件冒泡（常用） -->
      <div @click="handleClick">
        <button @click.stop="handleClick">点我提示信息</button>
      </div>
    </div>

    <hr>
    <div>.once事件只触发一次</div>
    <div>
      <button @click.once="handleClick">点我提示信息</button>
    </div>

    <hr>
    <div>.self只有event.target是当前操作的元素时才触发事件</div>
    <div>
      <div @click.self="handleClickSelf" style="background-color: antiquewhite;">
        <button @click="handleClick">点我提示信息</button>
      </div>
    </div>

    <!-- https://blog.csdn.net/catascdd/article/details/108273931 -->
    <hr>
    <div>.capture调整事件冒泡的触发顺序，冒泡是从里往外冒，捕获是从外往里捕。当捕获存在时，先从外到里的捕获，剩下的从里到外的冒泡输出。</div>
    <div>
      <div @click="handleClickCapture('最外层')" style="width:35px;height:35px;background-color:brown;">
        <div @click.capture="handleClickCapture('抓到了爷爷')" style="width:30px;height:30px;background-color:blueviolet;">
          <div @click="handleClickCapture('抓到了父亲')" style="width:25px;height:25px;background-color:aqua;">
            <div @click="handleClickCapture('抓到了儿子')" style="width:20px;height:20px;background-color: antiquewhite;"></div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  methods: {
    handleClick() {
      alert('handleClick触发')
    },
    handleClickSelf() {
      alert('handleClickSelf触发')
    },
    handleClickCapture(flag) {
      console.log(flag)
    }
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
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
