import Vue from 'vue';
import CustomMessage from '@/components/Message.vue'; // 引入消息组件

// 创建消息组件的构造函数
const MessageConstructor = Vue.extend(CustomMessage);

// 保存当前弹出的 message 对话框 DOM 列表
const messageDOMList = []

/**
 * 显示消息提示
 * @param {String} message 消息内容
 * @param {Object} options 配置项（类型、持续时间等）
 */
function showMessage(message, options = {}) {
  // 创建组件实例
  const instance = new MessageConstructor({
    propsData: {
      message,
      ...options
    }
  });

  // 显式挂载实例，生成真实 DOM 节点
  // 注意：$mount() 不指定挂载点时，会返回一个独立的 DOM 元素
  const el = instance.$mount();

  messageDOMList.push(el)

  // 挂载到 DOM（挂载到 body 下，避免父组件样式影响）
  document.body.appendChild(el.$el);

  adjustMessagePositions();

  // 监听关闭事件，从队列中移除并调整后续消息位置
  instance.$on('closed', () => {
    // 从队列中移除当前消息
    const index = messageDOMList.findIndex(item => item === el);
    if (index !== -1) {
      messageDOMList.splice(index, 1);
      // 调整后续消息的 top 值（重新计算所有消息的位置）
      // adjustMessagePositions();
    }
  });

  // 返回实例（可选，用于手动关闭）
  return instance;
}

/**
 * 调整所有消息的位置（当有消息关闭时调用）
 */
function adjustMessagePositions() {
  var spacing = 5;
  // 遍历队列，重新计算每个消息的 top 值
  messageDOMList.forEach((item, index) => {
    var totalHeight = 0
    var totalSpacing = 5
    for (var i = 0; i < messageDOMList.length; i++) {
      if (i >= index) {
        break;
      }

      totalHeight = totalHeight + messageDOMList[i].$el.getBoundingClientRect().height
      totalSpacing = totalSpacing + spacing
    }

    item._self.top = totalHeight + totalSpacing
  });
}

// 导出不同类型的消息方法（可选）
export const message = {
  info: (msg, options) => showMessage(msg, { type: 'info', ...options }),
  success: (msg, options) => showMessage(msg, { type: 'success', ...options }),
  warning: (msg, options) => showMessage(msg, { type: 'warning', ...options }),
  error: (msg, options) => showMessage(msg, { type: 'error', ...options })
};
