<template>
  <div class="theme-toggle">
    <button @click="toggleTheme" class="theme-button">
      {{ isDarkMode ? '🌞' : '🌙' }}
    </button>
  </div>
</template>

<script>
export default {
  name: 'ThemeToggle',
  data() {
    return {
      isDarkMode: false
    }
  },
  methods: {
    toggleTheme() {
      this.isDarkMode = !this.isDarkMode
      // document.documentElement 对应网页中的 <html> 根标签。
      document.documentElement.setAttribute('data-theme', this.isDarkMode ? 'dark' : 'light')
    }
  },
  mounted() {
    // 检查本地存储中的主题设置
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      this.isDarkMode = savedTheme === 'dark'
      // document.documentElement 对应网页中的 <html> 根标签。
      document.documentElement.setAttribute('data-theme', savedTheme)
    }
  }
}
</script>

<style scoped>
.theme-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
}

.theme-button {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.theme-button:hover {
  background-color: var(--hover-bg-color);
}
</style> 