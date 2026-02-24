(function () {
  const params = new URLSearchParams(location.search);
  const enabledByQuery = params.get("vconsole") === "1";
  const enabledByStorage = localStorage.getItem("demo_vconsole_enabled") === "1";
  const enabled = enabledByQuery || enabledByStorage;

  const statusEl = document.getElementById("vconsoleStatus");
  const toggleBtn = document.getElementById("toggleVConsole");

  function setStatus(isEnabled) {
    statusEl.textContent = `状态：${isEnabled ? "已启用" : "未启用"}`;
    toggleBtn.textContent = isEnabled ? "关闭 vConsole（刷新）" : "启用 vConsole（刷新）";
  }

  setStatus(enabled);

  toggleBtn.addEventListener("click", () => {
    const next = !enabledByStorage;
    localStorage.setItem("demo_vconsole_enabled", next ? "1" : "0");
    const nextUrl = new URL(location.href);
    nextUrl.searchParams.delete("vconsole");
    location.href = nextUrl.toString();
  });

  if (enabled) {
    if (typeof window.VConsole === "function") {
      window.__demo_vconsole__ = new window.VConsole();
      console.log("[demo] vConsole 已初始化", {
        enabledByQuery,
        enabledByStorage,
        ua: navigator.userAgent,
      });
    } else {
      console.warn("[demo] vConsole 脚本未加载成功（CDN 不可用？）");
    }
  } else {
    console.log("[demo] vConsole 未启用。可访问 ?vconsole=1 或点击“启用 vConsole”按钮。");
  }

  function safeJsonParse(text) {
    try {
      return JSON.parse(text);
    } catch {
      return { raw: text };
    }
  }

  async function doFetch(url) {
    const startedAt = Date.now();
    const res = await fetch(url, {
      method: "GET",
      headers: { "x-demo": "vconsole" },
      cache: "no-store",
    });
    const text = await res.text();
    const ms = Date.now() - startedAt;
    console.info("[demo] fetch 完成", { url, status: res.status, ms });
    return safeJsonParse(text);
  }

  function doXhr(url) {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.open("GET", url, true);
      xhr.setRequestHeader("x-demo", "vconsole");
      xhr.timeout = 8000;
      xhr.onreadystatechange = () => {
        if (xhr.readyState !== 4) return;
        if (xhr.status >= 200 && xhr.status < 300) {
          console.info("[demo] XHR 完成", { url, status: xhr.status });
          resolve(safeJsonParse(xhr.responseText));
        } else {
          const err = new Error(`XHR failed: status=${xhr.status}`);
          console.warn("[demo] XHR 失败", { url, status: xhr.status });
          reject(err);
        }
      };
      xhr.ontimeout = () => reject(new Error("XHR timeout"));
      xhr.onerror = () => reject(new Error("XHR network error"));
      xhr.send();
    });
  }

  function setDemoStorage() {
    const now = new Date();
    localStorage.setItem("demo_local_key", `local:${now.toISOString()}`);
    sessionStorage.setItem("demo_session_key", `session:${now.toISOString()}`);
    console.log("[demo] 已写入 localStorage/sessionStorage");
  }

  function readDemoStorage() {
    console.table([
      { key: "demo_local_key", value: localStorage.getItem("demo_local_key") },
      { key: "demo_session_key", value: sessionStorage.getItem("demo_session_key") },
      { key: "cookie", value: document.cookie || "(empty)" },
    ]);
  }

  function setDemoCookie() {
    const expires = new Date(Date.now() + 7 * 24 * 3600 * 1000).toUTCString();
    document.cookie = `demo_cookie=hello; Expires=${expires}; Path=/; SameSite=Lax`;
    console.log("[demo] 已写入 cookie：demo_cookie=hello");
  }

  function attachHandlers() {
    document.addEventListener("click", async (e) => {
      const btn = e.target && e.target.closest ? e.target.closest("[data-action]") : null;
      if (!btn) return;

      const action = btn.getAttribute("data-action");
      try {
        switch (action) {
          case "log":
            console.log("[demo] console.log", { at: new Date().toISOString(), random: Math.random() });
            break;
          case "info":
            console.info("[demo] console.info", { memory: performance && performance.memory });
            break;
          case "warn":
            console.warn("[demo] console.warn", { hint: "这是一条 warning" });
            break;
          case "error":
            console.error("[demo] console.error", new Error("这是一个示例错误对象"));
            break;
          case "table":
            console.table([
              { id: 1, name: "Alice", role: "admin" },
              { id: 2, name: "Bob", role: "user" },
              { id: 3, name: "Carol", role: "guest" },
            ]);
            break;
          case "time":
            console.time("[demo] timer");
            await new Promise((r) => setTimeout(r, 220));
            console.timeEnd("[demo] timer");
            break;
          case "fetch": {
            const data = await doFetch(`https://httpbin.org/get?ts=${Date.now()}`);
            console.log("[demo] fetch 响应", data);
            break;
          }
          case "xhr": {
            const data = await doXhr(`https://httpbin.org/uuid?ts=${Date.now()}`);
            console.log("[demo] xhr 响应", data);
            break;
          }
          case "fail": {
            await doFetch(`https://httpbin.org/status/503?ts=${Date.now()}`);
            break;
          }
          case "setStorage":
            setDemoStorage();
            break;
          case "readStorage":
            readDemoStorage();
            break;
          case "setCookie":
            setDemoCookie();
            break;
          case "throwSync":
            throw new Error("同步抛错：用于观察 vConsole 的 error 展示");
          case "throwAsync":
            setTimeout(() => {
              throw new Error("异步抛错：用于观察 vConsole 的 error 展示");
            }, 0);
            break;
          default:
            console.warn("[demo] 未知 action", action);
        }
      } catch (err) {
        console.error("[demo] 操作失败", { action, err });
      }
    });
  }

  attachHandlers();
})();

