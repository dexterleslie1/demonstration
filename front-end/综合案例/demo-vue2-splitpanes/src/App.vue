<template>
  <!-- <div id="app">
    <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/>
  </div> -->

  <splitpanes style="height: 400px" class="default-theme">
    <pane min-size="20">1</pane>
    <pane>
      <splitpanes horizontal>
        <pane>2</pane>
        <pane>3</pane>
        <pane>4</pane>
      </splitpanes>
    </pane>
    <pane>5</pane>
  </splitpanes>
</template>

<script>
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'

export default {
  components: { Splitpanes, Pane },
}

</script>

<style>
.splitpanes__pane {
  display: flex;
  justify-content: center;
  align-items: center;
  /* font-family: Helvetica, Arial, sans-serif; */
  /* color: rgba(255, 255, 255, 0.6); */
  /* font-size: 5em; */
}

/* #app {
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
} */
</style>
