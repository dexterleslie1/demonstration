<template>
  <div>
    <hr>
    <div>
      演示动态切换img图片<br>
      https://graffino.com/til/jN9xnHChCC-how-to-add-dynamic-images-in-nuxtjs-vuejs</div>
    <div>
      <!-- <img src="~/assets/images/chevron-down.svg" style="width:30px;height:30px;"/> -->
      <img :src="require(`~/assets/images/${imgName}.svg`)" style="width:30px;height:30px;" />
      <img :src="imgSrc" style="width:30px;height:30px;" />
    </div>
    <div>
      <button @click="handleClick">点击我切换图片</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      imgName: 'chevron-down'
    }
  },
  computed: {
    imgSrc() {
      return require(`~/assets/images/${this.imgName}.svg`)
    }
  },
  name: 'IndexPage',
  methods: {
    handleClick() {
      if (this.imgName === 'chevron-down')
        this.imgName = 'chevron-up'
      else
        this.imgName = 'chevron-down'
    }
  }
}
</script>
