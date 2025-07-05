<template>
  <transition name="message-fade">
    <div v-show="visible" class="custom-message" :class="`message-${type}`" :style="{ top: `${top}px` }">
      <span class="message-content">{{ message }}</span>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'CustomMessage',
  props: {
    message: {
      type: String,
      required: true
    },
    type: {
      type: String,
      default: 'info' // 默认类型：info/success/warning/error
    },
    duration: {
      type: Number,
      default: 3000 // 自动关闭时间（毫秒）
    },
    // 新增：动态计算的 top 值（由外部传入）
    top: {
      type: Number,
      default: 20
    }
  },
  data() {
    return {
      visible: false,
      timer: null
    };
  },
  mounted() {
    this.visible = true;
    // 自动关闭逻辑
    if (this.duration > 0) {
      this.timer = setTimeout(() => {
        this.close();
      }, this.duration);
    }
  },
  methods: {
    close() {
      this.visible = false;
      clearTimeout(this.timer);
      // 触发自定义事件（可选，用于通知父组件销毁实例）
      this.$emit('closed');
    }
  }
};
</script>

<style scoped>
.custom-message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 10px 20px;
  border-radius: 4px;
  color: #fff;
  z-index: 9999;
  min-width: 200px;
  text-align: center;
}

/* 不同类型的样式 */
.message-info {
  background-color: #909399;
}

.message-success {
  background-color: #67C23A;
}

.message-warning {
  background-color: #E6A23C;
}

.message-error {
  background-color: #F56C6C;
}

/* 过渡动画 */
.message-fade-enter-active,
.message-fade-leave-active {
  transition: opacity 0.3s;
}

.message-fade-enter,
.message-fade-leave-to {
  opacity: 0;
}
</style>
