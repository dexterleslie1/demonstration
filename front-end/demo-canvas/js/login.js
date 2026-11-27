/**
 * 用 CanvasUI 基础组件拼一个登录界面
 */
(function () {
  'use strict';

  const { CanvasApp, Panel, Label, TextInput, Button } = window.CanvasUI;

  const canvas = document.getElementById('ui-canvas');
  const app = new CanvasApp(canvas);

  // 画布背景色（框架根节点不画背景，这里用一层全屏 Panel）
  const backdrop = new Panel({
    x: 0,
    y: 0,
    width: canvas.width,
    height: canvas.height,
    background: '#f1f5f9',
    borderColor: 'transparent',
    radius: 0,
    shadow: false,
  });
  app.add(backdrop);

  const cardW = 360;
  const cardH = 380;
  const cardX = Math.round((canvas.width - cardW) / 2);
  const cardY = Math.round((canvas.height - cardH) / 2);

  const card = new Panel({
    x: cardX,
    y: cardY,
    width: cardW,
    height: cardH,
    background: '#ffffff',
    borderColor: '#e2e8f0',
    radius: 16,
  });
  app.add(card);

  card.add(
    new Label({
      x: 0,
      y: 28,
      width: cardW,
      height: 32,
      text: '欢迎登录',
      align: 'center',
      font: '600 22px system-ui, sans-serif',
      color: '#0f172a',
    })
  );

  card.add(
    new Label({
      x: 0,
      y: 60,
      width: cardW,
      height: 20,
      text: 'Canvas 2D UI 框架演示',
      align: 'center',
      font: '13px system-ui, sans-serif',
      color: '#64748b',
    })
  );

  card.add(
    new Label({
      x: 32,
      y: 108,
      width: 120,
      height: 20,
      text: '用户名',
      font: '13px system-ui, sans-serif',
      color: '#334155',
    })
  );

  const username = new TextInput({
    x: 32,
    y: 132,
    width: cardW - 64,
    height: 40,
    placeholder: '请输入用户名',
  });
  card.add(username);

  card.add(
    new Label({
      x: 32,
      y: 188,
      width: 120,
      height: 20,
      text: '密码',
      font: '13px system-ui, sans-serif',
      color: '#334155',
    })
  );

  const password = new TextInput({
    x: 32,
    y: 212,
    width: cardW - 64,
    height: 40,
    placeholder: '请输入密码',
    password: true,
  });
  card.add(password);

  const status = new Label({
    x: 32,
    y: 262,
    width: cardW - 64,
    height: 20,
    text: '',
    align: 'center',
    font: '13px system-ui, sans-serif',
    color: '#64748b',
  });
  card.add(status);

  function submit() {
    const u = username.value.trim();
    const p = password.value;
    if (!u) {
      status.color = '#dc2626';
      status.text = '请填写用户名';
      app.setFocus(username);
      app.markDirty();
      return;
    }
    if (!p) {
      status.color = '#dc2626';
      status.text = '请填写密码';
      app.setFocus(password);
      app.markDirty();
      return;
    }
    // 演示用假校验：任意非空即可「登录成功」
    status.color = '#16a34a';
    status.text = '登录成功：' + u;
    app.markDirty();
  }

  const loginBtn = new Button({
    x: 32,
    y: 300,
    width: cardW - 64,
    height: 44,
    text: '登 录',
    onPress: submit,
  });
  card.add(loginBtn);

  // Enter 提交：挂在两个输入框的 keydown 上
  function wireEnter(input) {
    const prev = input.onKeyDown.bind(input);
    input.onKeyDown = function (e) {
      prev(e);
      if (e.key === 'Enter') {
        e.preventDefault();
        submit();
      }
    };
  }
  wireEnter(username);
  wireEnter(password);

  // 初始焦点
  app.setFocus(username);
})();
