// Utility functions
(function() {
  'use strict';
  
  // Export utility functions to global scope
  window.utils = {
    // Format date function
    formatDate: function(date) {
      if (!(date instanceof Date)) {
        date = new Date(date);
      }
      return date.toLocaleDateString('zh-CN');
    },
    
    // Generate random number
    random: function(min, max) {
      return Math.floor(Math.random() * (max - min + 1)) + min;
    },
    
    // Show notification
    showNotification: function(message, type) {
      const notification = document.createElement('div');
      notification.className = `notification ${type || 'info'}`;
      notification.textContent = message;
      notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 24px;
        background-color: #3498db;
        color: white;
        border-radius: 4px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.2);
        z-index: 1000;
        animation: slideIn 0.3s ease-out;
      `;
      
      document.body.appendChild(notification);
      
      setTimeout(() => {
        notification.remove();
      }, 3000);
    }
  };
  
  // Add animation styles
  const style = document.createElement('style');
  style.textContent = `
    @keyframes slideIn {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }
  `;
  document.head.appendChild(style);
})();
