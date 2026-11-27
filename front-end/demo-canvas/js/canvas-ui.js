/**
 * 基于 Canvas 2D 的极简 UI 框架
 * 职责：组件树、脏标记重绘、命中测试、鼠标/键盘事件分发
 */

(function (global) {
  'use strict';

  // ---------- 基础节点 ----------

  class Widget {
    constructor(options = {}) {
      this.x = options.x || 0;
      this.y = options.y || 0;
      this.width = options.width || 0;
      this.height = options.height || 0;
      this.visible = options.visible !== false;
      this.children = [];
      this.parent = null;
      this.app = null;
      this.id = options.id || null;
    }

    add(child) {
      child.parent = this;
      child.app = this.app;
      this.children.push(child);
      if (this.app) this.app.markDirty();
      return child;
    }

    absoluteX() {
      return this.parent ? this.parent.absoluteX() + this.x : this.x;
    }

    absoluteY() {
      return this.parent ? this.parent.absoluteY() + this.y : this.y;
    }

    containsPoint(px, py) {
      const ax = this.absoluteX();
      const ay = this.absoluteY();
      return px >= ax && px <= ax + this.width && py >= ay && py <= ay + this.height;
    }

    /** 从后往前命中（后添加的在上层） */
    hitTest(px, py) {
      if (!this.visible) return null;
      for (let i = this.children.length - 1; i >= 0; i--) {
        const hit = this.children[i].hitTest(px, py);
        if (hit) return hit;
      }
      return this.containsPoint(px, py) ? this : null;
    }

    draw(ctx) {
      if (!this.visible) return;
      this.paint(ctx);
      for (const child of this.children) {
        child.draw(ctx);
      }
    }

    /** 子类覆盖：绘制自身（不含子节点） */
    paint(_ctx) {}

    onMouseDown(_e) {}
    onMouseUp(_e) {}
    onMouseMove(_e) {}
    onClick(_e) {}
    onFocus() {}
    onBlur() {}
    onKeyDown(_e) {}
  }

  // ---------- 应用壳 ----------

  class CanvasApp {
    constructor(canvas) {
      this.canvas = canvas;
      this.ctx = canvas.getContext('2d');
      this.root = new Widget({ x: 0, y: 0, width: canvas.width, height: canvas.height });
      this.root.app = this;
      this.dirty = true;
      this.hover = null;
      this.focus = null;
      /** 当前拥有文字选区的组件（Label / Button / TextInput 等） */
      this.selectionOwner = null;
      this.mouseDownTarget = null;
      this._raf = null;

      // 透明 DOM input：叠在当前 TextInput 上，承接键盘与 IME
      // （Canvas 像素本身无法获得系统输入焦点）
      this.hiddenInput = document.createElement('input');
      this.hiddenInput.setAttribute('autocomplete', 'off');
      this.hiddenInput.setAttribute('autocapitalize', 'off');
      this.hiddenInput.setAttribute('spellcheck', 'false');
      this.hiddenInput.style.cssText = [
        'position:fixed',
        'left:0',
        'top:0',
        'width:1px',
        'height:1px',
        'margin:0',
        'padding:0',
        'border:none',
        'outline:none',
        'opacity:0',
        'background:transparent',
        'color:transparent',
        'caret-color:transparent',
        'z-index:10',
        'pointer-events:none',
      ].join(';');
      document.body.appendChild(this.hiddenInput);

      this._bindEvents();
      this._loop();
    }

    add(widget) {
      return this.root.add(widget);
    }

    markDirty() {
      this.dirty = true;
    }

    setFocus(widget) {
      const same = this.focus === widget;
      if (!same) {
        if (this.focus) this.focus.onBlur();
        this.focus = widget;
        if (widget) widget.onFocus();
      }
      if (widget) {
        this.hiddenInput.readOnly = false;
        if (typeof widget.syncHiddenInput === 'function') {
          widget.syncHiddenInput(this.hiddenInput);
        }
        this._placeHiddenInput(widget);
        // mousedown 同期 focus 可能被浏览器立刻抢走，下一宏任务再抢一次
        this.hiddenInput.focus({ preventScroll: true });
        setTimeout(() => {
          if (this.focus === widget) {
            this.hiddenInput.focus({ preventScroll: true });
          }
        }, 0);
      } else if (!this.selectionOwner) {
        this._hideHiddenInput();
        this.hiddenInput.blur();
      }
      this.markDirty();
    }

    /** 切换文字选区归属，并同步到隐藏 input 以便 Ctrl+C */
    setSelectionOwner(widget) {
      if (this.selectionOwner && this.selectionOwner !== widget) {
        this.selectionOwner.clearSelection();
      }
      this.selectionOwner = widget || null;
      if (widget) this.mirrorSelection(widget);
      this.markDirty();
    }

    mirrorSelection(widget) {
      if (!widget || typeof widget.getCopyText !== 'function') return;
      this._placeHiddenInput(widget);
      if (widget.focusable && typeof widget.syncHiddenInput === 'function') {
        // 可编辑输入框：保持 password 等类型，只同步值与选区
        widget.syncHiddenInput(this.hiddenInput);
        } else {
        const text = widget.getCopyText();
        this.hiddenInput.readOnly = true;
        this.hiddenInput.type = 'text';
        if (this.hiddenInput.value !== text) this.hiddenInput.value = text;
        if (typeof widget.syncSelectionToHidden === 'function') {
          widget.syncSelectionToHidden(this.hiddenInput);
        } else {
          const { start, end } = widget.selRange();
          const dir = widget.selectionStart > widget.selectionEnd ? 'backward' : 'forward';
          try {
            this.hiddenInput.setSelectionRange(start, end, dir);
          } catch (_) {}
        }
      }
      this.hiddenInput.focus({ preventScroll: true });
    }

    /** 把透明 input 对齐到组件在页面上的矩形，便于保持焦点与 IME */
    _placeHiddenInput(widget) {
      const rect = this.canvas.getBoundingClientRect();
      const scaleX = rect.width / this.canvas.width;
      const scaleY = rect.height / this.canvas.height;
      const left = rect.left + widget.absoluteX() * scaleX;
      const top = rect.top + widget.absoluteY() * scaleY;
      const width = Math.max(widget.width * scaleX, 4);
      const height = Math.max(widget.height * scaleY, 4);
      const s = this.hiddenInput.style;
      s.left = `${left}px`;
      s.top = `${top}px`;
      s.width = `${width}px`;
      s.height = `${height}px`;
      s.pointerEvents = 'none';
      s.opacity = '0';
    }

    _hideHiddenInput() {
      const s = this.hiddenInput.style;
      s.left = '0';
      s.top = '0';
      s.width = '1px';
      s.height = '1px';
    }

    _pointerPos(e) {
      const rect = this.canvas.getBoundingClientRect();
      const scaleX = this.canvas.width / rect.width;
      const scaleY = this.canvas.height / rect.height;
      return {
        x: (e.clientX - rect.left) * scaleX,
        y: (e.clientY - rect.top) * scaleY,
      };
    }

    _bindEvents() {
      this.canvas.addEventListener('mousedown', (e) => {
        const { x, y } = this._pointerPos(e);
        const target = this.root.hitTest(x, y);
        this.mouseDownTarget = target;
        if (target && target.focusable) {
          // 阻止 canvas 抢走焦点，否则隐藏 input 立刻失焦，键盘无响应
          e.preventDefault();
          this.setFocus(target);
          this.setSelectionOwner(target);
        } else if (target && target.textSelectable) {
          e.preventDefault();
          this.setFocus(null);
          this.setSelectionOwner(target);
        } else {
          this.setFocus(null);
          this.setSelectionOwner(null);
        }
        if (target) target.onMouseDown({ x, y, originalEvent: e });
        this.markDirty();
      });

      window.addEventListener('mouseup', (e) => {
        const { x, y } = this._pointerPos(e);
        const target = this.root.hitTest(x, y);
        // 拖拽选区可能在控件外松开，事件仍交给按下时的目标
        if (this.mouseDownTarget) {
          this.mouseDownTarget.onMouseUp({ x, y, originalEvent: e });
        } else if (target) {
          target.onMouseUp({ x, y, originalEvent: e });
        }
        if (this.mouseDownTarget && this.mouseDownTarget === target) {
          this.mouseDownTarget.onClick({ x, y, originalEvent: e });
        }
        this.mouseDownTarget = null;
        this.markDirty();
      });

      // 用 window 监听，便于拖出 canvas / 输入框后仍更新选区
      window.addEventListener('mousemove', (e) => {
        const { x, y } = this._pointerPos(e);
        const target = this.root.hitTest(x, y);
        if (!this.mouseDownTarget && this.hover !== target) {
          this.hover = target;
          this.canvas.style.cursor = (target && target.cursor) || 'default';
          this.markDirty();
        }
        const receiver = this.mouseDownTarget || target;
        if (receiver) receiver.onMouseMove({ x, y, originalEvent: e });
      });

      const syncFromHidden = () => {
        if (this.focus && typeof this.focus.onHiddenInput === 'function') {
          this.focus.onHiddenInput(this.hiddenInput.value);
          if (typeof this.focus.syncSelectionFromHidden === 'function') {
            this.focus.syncSelectionFromHidden(this.hiddenInput);
          }
          this.markDirty();
        }
      };
      this.hiddenInput.addEventListener('input', syncFromHidden);
      this.hiddenInput.addEventListener('compositionend', syncFromHidden);
      this.hiddenInput.addEventListener('select', () => {
        // 拖拽选区过程中以组件内锚点/焦点为准，避免 DOM 规范化选区写回把反向拖拽折叠掉
        if (this.focus && this.focus._selectDragging) return;
        if (this.focus && typeof this.focus.syncSelectionFromHidden === 'function') {
          this.focus.syncSelectionFromHidden(this.hiddenInput);
          this.markDirty();
        }
      });
      this.hiddenInput.addEventListener('keyup', () => {
        if (this.focus && this.focus._selectDragging) return;
        if (this.focus && typeof this.focus.syncSelectionFromHidden === 'function') {
          this.focus.syncSelectionFromHidden(this.hiddenInput);
          this.markDirty();
        }
      });

      this.hiddenInput.addEventListener('keydown', (e) => {
        const receiver = this.focus || this.selectionOwner;
        if (receiver && typeof receiver.onKeyDown === 'function') {
          receiver.onKeyDown(e);
          this.markDirty();
        }
      });

      window.addEventListener('resize', () => {
        if (this.focus) this._placeHiddenInput(this.focus);
      });
      window.addEventListener('scroll', () => {
        if (this.focus) this._placeHiddenInput(this.focus);
      }, true);
    }

    _loop() {
      const tick = () => {
        if (this.dirty) {
          this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
          this.root.draw(this.ctx);
          this.dirty = false;
        }
        this._raf = requestAnimationFrame(tick);
      };
      this._raf = requestAnimationFrame(tick);
    }
  }

  // ---------- 基础组件 ----------

  function roundRect(ctx, x, y, w, h, r) {
    const radius = Math.min(r, w / 2, h / 2);
    ctx.beginPath();
    ctx.moveTo(x + radius, y);
    ctx.arcTo(x + w, y, x + w, y + h, radius);
    ctx.arcTo(x + w, y + h, x, y + h, radius);
    ctx.arcTo(x, y + h, x, y, radius);
    ctx.arcTo(x, y, x + w, y, radius);
    ctx.closePath();
  }

  /**
   * 可拖拽选字的文字组件基类
   * Label / Button / TextInput 共用选区状态、坐标↔下标、高亮绘制与复制镜像
   */
  class SelectableTextWidget extends Widget {
    constructor(options = {}) {
      super(options);
      this.text = options.text || '';
      this.font = options.font || '14px system-ui, sans-serif';
      this.align = options.align || 'left';
      this.baseline = options.baseline || 'middle';
      this.paddingX = options.paddingX || 0;
      this.selectionColor = options.selectionColor || '#bfdbfe';
      this.textSelectable = options.textSelectable !== false;
      this.selectionStart = 0;
      this.selectionEnd = 0;
      this._selectDragging = false;
      if (this.textSelectable && !this.cursor) this.cursor = 'text';
    }

    /** 用于度量与绘制的字符串 */
    getSelectableText() {
      return this.text || '';
    }

    /** 复制到系统剪贴板镜像时的字符串（密码框可返回明文） */
    getCopyText() {
      return this.getSelectableText();
    }

    getFont() {
      return this.font;
    }

    selRange() {
      const a = this.selectionStart;
      const b = this.selectionEnd;
      return { start: Math.min(a, b), end: Math.max(a, b) };
    }

    hasSelection() {
      return this.selectionStart !== this.selectionEnd;
    }

    clearSelection() {
      this.selectionStart = this.selectionEnd;
      this._selectDragging = false;
    }

    /** 同步选区到 DOM；反向拖拽时用 direction=backward，且始终传 start<=end */
    syncSelectionToHidden(input) {
      const el = input || (this.app && this.app.hiddenInput);
      if (!el) return;
      try {
        const len = el.value.length;
        const a = Math.max(0, Math.min(this.selectionStart, len));
        const b = Math.max(0, Math.min(this.selectionEnd, len));
        const start = Math.min(a, b);
        const end = Math.max(a, b);
        const dir = a > b ? 'backward' : 'forward';
        el.setSelectionRange(start, end, dir);
      } catch (_) {}
    }

    /** 显示文本左边缘的画布 x（考虑 align） */
    textLeftX() {
      const ctx = this.app && this.app.ctx;
      const ax = this.absoluteX();
      if (!ctx) return ax + this.paddingX;
      ctx.font = this.getFont();
      const text = this.getSelectableText();
      const w = ctx.measureText(text).width;
      if (this.align === 'center') return ax + this.width / 2 - w / 2;
      if (this.align === 'right') return ax + this.width - this.paddingX - w;
      return ax + this.paddingX;
    }

    indexAt(canvasX) {
      const ctx = this.app && this.app.ctx;
      if (!ctx) return 0;
      ctx.font = this.getFont();
      const text = this.getSelectableText();
      const localX = canvasX - this.textLeftX();
      if (localX <= 0) return 0;
      let prevW = 0;
      for (let i = 1; i <= text.length; i++) {
        const w = ctx.measureText(text.slice(0, i)).width;
        if (w >= localX) {
          return localX - prevW < w - localX ? i - 1 : i;
        }
        prevW = w;
      }
      return text.length;
    }

    textWidthUntil(index) {
      const ctx = this.app && this.app.ctx;
      if (!ctx) return 0;
      ctx.font = this.getFont();
      const text = this.getSelectableText().slice(0, Math.max(0, index));
      return ctx.measureText(text).width;
    }

    beginSelect(e) {
      if (!this.textSelectable) return;
      const i = this.indexAt(e.x);
      this.selectionStart = i;
      this.selectionEnd = i;
      this._selectDragging = true;
      if (this.app) this.app.mirrorSelection(this);
    }

    updateSelect(e) {
      if (!this._selectDragging) return;
      this.selectionEnd = this.indexAt(e.x);
      if (this.app) {
        this.app.mirrorSelection(this);
        this.app.markDirty();
      }
    }

    endSelect() {
      this._selectDragging = false;
      if (this.app && this.hasSelection()) this.app.mirrorSelection(this);
    }

    onMouseDown(e) {
      this.beginSelect(e);
    }

    onMouseMove(e) {
      this.updateSelect(e);
    }

    onMouseUp() {
      this.endSelect();
    }

    onKeyDown(e) {
      if (!this.textSelectable) return;
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'a') {
        e.preventDefault();
        this.selectionStart = 0;
        this.selectionEnd = this.getCopyText().length;
        if (this.app) this.app.mirrorSelection(this);
      }
    }

    /** 在已设置好 font 的上下文中绘制选区高亮（文字左缘为 textLeft） */
    paintSelectionHighlight(ctx, textLeft, top, height) {
      if (!this.hasSelection() || !this.getSelectableText()) return;
      const { start, end } = this.selRange();
      const x1 = textLeft + this.textWidthUntil(start);
      const x2 = textLeft + this.textWidthUntil(end);
      ctx.fillStyle = this.selectionColor;
      ctx.fillRect(x1, top, Math.max(x2 - x1, 1), height);
    }
  }

  class Panel extends SelectableTextWidget {
    constructor(options = {}) {
      super(
        Object.assign({}, options, {
          // Panel 默认无正文；若传入 text/title 则可拖拽选字
          text: options.text || options.title || '',
          textSelectable: options.textSelectable != null ? options.textSelectable : !!(options.text || options.title),
          align: options.align || 'left',
          font: options.font || '600 14px system-ui, sans-serif',
          paddingX: options.paddingX != null ? options.paddingX : 16,
        })
      );
      this.background = options.background || '#ffffff';
      this.borderColor = options.borderColor || '#e5e7eb';
      this.radius = options.radius != null ? options.radius : 12;
      this.shadow = options.shadow !== false;
      this.titleOffsetY = options.titleOffsetY != null ? options.titleOffsetY : 14;
      if (!this.textSelectable) this.cursor = undefined;
    }

    paint(ctx) {
      const ax = this.absoluteX();
      const ay = this.absoluteY();
      if (this.shadow) {
        ctx.save();
        ctx.shadowColor = 'rgba(15, 23, 42, 0.12)';
        ctx.shadowBlur = 24;
        ctx.shadowOffsetY = 8;
        ctx.fillStyle = this.background;
        roundRect(ctx, ax, ay, this.width, this.height, this.radius);
        ctx.fill();
        ctx.restore();
      } else {
        ctx.fillStyle = this.background;
        roundRect(ctx, ax, ay, this.width, this.height, this.radius);
        ctx.fill();
      }
      ctx.strokeStyle = this.borderColor;
      ctx.lineWidth = 1;
      roundRect(ctx, ax, ay, this.width, this.height, this.radius);
      ctx.stroke();

      if (this.text) {
        ctx.save();
        ctx.font = this.getFont();
        ctx.textAlign = 'left';
        ctx.textBaseline = 'top';
        const textLeft = this.textLeftX();
        const textTop = ay + this.titleOffsetY;
        this.paintSelectionHighlight(ctx, textLeft, textTop - 2, 18);
        ctx.fillStyle = '#0f172a';
        ctx.fillText(this.text, textLeft, textTop);
        ctx.restore();
      }
    }
  }

  class Label extends SelectableTextWidget {
    constructor(options = {}) {
      super(options);
      this.color = options.color || '#0f172a';
      this.cursor = 'text';
    }

    paint(ctx) {
      const ax = this.absoluteX();
      const ay = this.absoluteY();
      const text = this.getSelectableText();
      ctx.font = this.getFont();
      ctx.textBaseline = this.baseline;
      ctx.textAlign = 'left';

      const textLeft = this.textLeftX();
      const ty = this.baseline === 'middle' ? ay + this.height / 2 : ay;
      const highlightTop = this.baseline === 'middle' ? ay + 2 : ay;
      const highlightH = Math.max(this.height - 4, 14);

      this.paintSelectionHighlight(ctx, textLeft, highlightTop, highlightH);

      ctx.fillStyle = this.color;
      ctx.fillText(text, textLeft, ty);
    }
  }

  class TextInput extends SelectableTextWidget {
    constructor(options = {}) {
      super(
        Object.assign({}, options, {
          text: options.value || '',
          align: 'left',
          paddingX: options.paddingX != null ? options.paddingX : 12,
          font: options.font || '14px system-ui, sans-serif',
        })
      );
      this.value = options.value || '';
      this.placeholder = options.placeholder || '';
      this.password = !!options.password;
      this.focusable = true;
      this.cursor = 'text';
      this.radius = 8;
      this._blinkOn = true;
      this._blinkTimer = null;
    }

    getSelectableText() {
      return this.password ? '•'.repeat(this.value.length) : this.value;
    }

    getCopyText() {
      return this.value;
    }

    displayText() {
      return this.getSelectableText();
    }

    syncHiddenInput(input) {
      input.readOnly = false;
      input.type = this.password ? 'password' : 'text';
      // 重复赋值 value 会清空 DOM 选区；拖拽过程中只更新选区
      if (input.value !== this.value) input.value = this.value;
      input.placeholder = this.placeholder;
      this.syncSelectionToHidden(input);
    }

    syncSelectionFromHidden(input) {
      if (!input || this._selectDragging) return;
      const start = input.selectionStart;
      const end = input.selectionEnd;
      // 保留反向选区：DOM 里 start<=end，用 selectionDirection 还原锚点
      if (input.selectionDirection === 'backward') {
        this.selectionStart = end;
        this.selectionEnd = start;
      } else {
        this.selectionStart = start;
        this.selectionEnd = end;
      }
    }

    onHiddenInput(value) {
      this.value = value;
      this.text = value;
    }

    onMouseDown(e) {
      this._blinkOn = true;
      this.beginSelect(e);
    }

    onFocus() {
      this._blinkOn = true;
      clearInterval(this._blinkTimer);
      this._blinkTimer = setInterval(() => {
        this._blinkOn = !this._blinkOn;
        if (this.app) this.app.markDirty();
      }, 500);
    }

    onBlur() {
      clearInterval(this._blinkTimer);
      this._blinkTimer = null;
      this._blinkOn = false;
      this._selectDragging = false;
    }

    onKeyDown(e) {
      if (e.key === 'Tab') e.preventDefault();
      SelectableTextWidget.prototype.onKeyDown.call(this, e);
    }

    paint(ctx) {
      const ax = this.absoluteX();
      const ay = this.absoluteY();
      const focused = this.app && this.app.focus === this;

      ctx.fillStyle = '#ffffff';
      roundRect(ctx, ax, ay, this.width, this.height, this.radius);
      ctx.fill();

      ctx.strokeStyle = focused ? '#2563eb' : '#cbd5e1';
      ctx.lineWidth = focused ? 2 : 1;
      roundRect(ctx, ax, ay, this.width, this.height, this.radius);
      ctx.stroke();

      ctx.save();
      ctx.beginPath();
      ctx.rect(ax + 2, ay + 2, this.width - 4, this.height - 4);
      ctx.clip();

      ctx.font = this.getFont();
      ctx.textBaseline = 'middle';
      ctx.textAlign = 'left';

      const text = this.displayText();
      const textX = this.textLeftX();
      const textY = ay + this.height / 2;

      if (!text) {
        ctx.fillStyle = '#94a3b8';
        ctx.fillText(this.placeholder, textX, textY);
      } else {
        this.paintSelectionHighlight(ctx, textX, ay + 8, this.height - 16);
        ctx.fillStyle = '#0f172a';
        ctx.fillText(text, textX, textY);
      }

      if (focused && this._blinkOn && !this.hasSelection()) {
        const cx = textX + this.textWidthUntil(this.selectionEnd) + 1;
        ctx.strokeStyle = '#0f172a';
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.moveTo(cx, ay + 10);
        ctx.lineTo(cx, ay + this.height - 10);
        ctx.stroke();
      }
      ctx.restore();
    }
  }

  class Button extends SelectableTextWidget {
    constructor(options = {}) {
      super(
        Object.assign({}, options, {
          text: options.text || 'Button',
          align: 'center',
          font: options.font || '600 14px system-ui, sans-serif',
          selectionColor: options.selectionColor || 'rgba(255,255,255,0.35)',
        })
      );
      this.onPress = options.onPress || null;
      this.radius = 8;
      this._pressed = false;
      this.disabled = !!options.disabled;
      // 默认可选字；仍保持 pointer，拖拽时也能选
      this.cursor = options.cursor || 'pointer';
    }

    onMouseDown(e) {
      if (this.disabled) return;
      this._pressed = true;
      this.beginSelect(e);
    }

    onMouseUp(e) {
      this._pressed = false;
      this.endSelect();
    }

    onClick() {
      if (this.disabled) return;
      this._pressed = false;
      // 拖出选区则视为选字，不触发点击
      if (this.hasSelection()) return;
      if (typeof this.onPress === 'function') this.onPress();
    }

    paint(ctx) {
      const ax = this.absoluteX();
      const ay = this.absoluteY();
      const hovered = this.app && this.app.hover === this;
      let bg = '#2563eb';
      if (this.disabled) bg = '#94a3b8';
      else if (this._pressed) bg = '#1d4ed8';
      else if (hovered) bg = '#3b82f6';

      ctx.fillStyle = bg;
      roundRect(ctx, ax, ay, this.width, this.height, this.radius);
      ctx.fill();

      ctx.font = this.getFont();
      ctx.textAlign = 'left';
      ctx.textBaseline = 'middle';
      const textLeft = this.textLeftX();
      this.paintSelectionHighlight(ctx, textLeft, ay + 10, this.height - 20);
      ctx.fillStyle = '#ffffff';
      ctx.fillText(this.getSelectableText(), textLeft, ay + this.height / 2);
    }
  }

  global.CanvasUI = {
    Widget,
    SelectableTextWidget,
    CanvasApp,
    Panel,
    Label,
    TextInput,
    Button,
    roundRect,
  };
})(typeof window !== 'undefined' ? window : globalThis);
