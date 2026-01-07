// Main application script
(function() {
  'use strict';
  
  // Initialize the application
  function initApp() {
    console.log('Application initialized');
    setupEventListeners();
  }
  
  // Setup event listeners
  function setupEventListeners() {
    const button = document.querySelector('.button');
    if (button) {
      button.addEventListener('click', handleButtonClick);
    }
  }
  
  // Handle button click event
  function handleButtonClick(event) {
    event.preventDefault();
    alert('Button clicked! Grunt is working!');
  }
  
  // DOM ready event
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
  } else {
    initApp();
  }
})();
