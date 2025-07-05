import { message } from '@/utils/message';

// 定义插件
const MessagePlugin = {
  install(Vue, options = {}) {
    // 将消息函数挂载到 Vue 原型
    Vue.prototype.$message = message;

    // 可选：全局配置（如默认持续时间）
    if (options.duration) {
      message.defaultDuration = options.duration;
    }
  }
};

export default MessagePlugin;
