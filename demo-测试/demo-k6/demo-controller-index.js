import http from 'k6/http';
import { check, sleep } from 'k6';

// Base URL 用于指定被测服务地址。
// - 默认：`http://localhost:8080`
// - 建议在运行时通过环境变量覆盖：`--env BASE_URL=http://ip:port`
// - 去掉末尾的 `/`，避免拼接出 `//?param1=...` 这种多余路径
const BASE_URL = (__ENV.BASE_URL || 'http://localhost:8080').replace(/\/$/, '');

// k6 的执行参数（场景/并发/阈值等）。
// 这里我们只定义一个简单场景：持续固定 VU 并发压测该接口。
export const options = {
  scenarios: {
    demo_controller_index: {
      // executor: constant-vus 表示固定并发数运行
      executor: 'constant-vus',

      // VUS（并发用户数）
      // - 运行时可通过 `--env VUS=10` 覆盖
      // - 否则默认 10
      vus: (__ENV.VUS ? parseInt(__ENV.VUS, 10) : 10),

      // duration（持续时长）
      // - 运行时可通过 `--env DURATION=30s` 覆盖
      // - 否则默认 30s
      duration: (__ENV.DURATION || '30s'),
    },
  },
  thresholds: {
    // 失败率阈值：如果请求失败（DNS/连接/超时等导致的失败）过高，则让测试整体失败
    // rate<0.01 表示失败率需要 < 1%
    http_req_failed: ['rate<0.01'],
  },
};

// 构造接口 URL
// 被测接口来自：DemoController
// - 类上：`@RequestMapping("/")`
// - 方法上：`@GetMapping("/")`
// 因此最终路径为：`GET /?param1=...`
function buildUrl(param1) {
  return `${BASE_URL}/?param1=${encodeURIComponent(param1)}`;
}

export default function () {
  // param1 做成“每次请求不同”，用于：
  // 1) 避免缓存/复用导致的非预期结果
  // 2) 断言时验证响应里的 `data.param1` 确实回显了本次请求入参
  //
  // k6 内置变量：
  // - __VU：当前虚拟用户编号
  // - __ITER：当前虚拟用户的迭代次数（从 0 开始）
  // Use unique param per request to validate request/response consistency.
  const param1 = `param1-${__VU}-${__ITER}`;
  const url = buildUrl(param1);

  // 发起 GET 请求
  // Accept: application/json 提示服务返回 JSON（Spring 默认通常就会返回 JSON，但这里显式声明更稳）
  const res = http.get(url, {
    headers: { Accept: 'application/json' },
  });

  // 关键 HTTP 状态码断言
  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  // 解析 JSON 响应体。
  // 为了让脚本在服务端异常（比如非 JSON、返回 HTML 错误页）时也能继续执行 check，
  // 这里用 try/catch，解析失败时 json 保持 null。
  let json = null;
  try {
    json = res.json();
  } catch (e) {
    json = null;
  }

  // 根据接口返回结构抽取 data：
  // DemoController 返回的是：ObjectResponse<MyBean>
  // ObjectResponse 继承 BaseResponse，包含：
  // - errorCode: 0（默认值）
  // - data: MyBean
  const data = json && json.data ? json.data : null;

  // dataList 是 MyBean 中的字段（List<MyBeanInner>）
  const dataList = data && Array.isArray(data.dataList) ? data.dataList : [];

  // 业务字段断言：
  // - errorCode：应为数字（通常默认 0）
  // - data.field1：接口拼接了 `UUID:` + uuid，因此只要断言前缀即可（uuid 本身每次不同）
  // - data.field2：在服务端固定为 "field2 value"
  // - data.param1：应等于本次请求的入参
  // - data.dataList：应该是 2 条数据，并且每条的 field1 是固定值
  check(json, {
    // 返回结构存在性/类型检查
    'has errorCode': (j) => j && typeof j.errorCode === 'number',
    'has data': (j) => j && j.data,

    // field1 以 UUID: 开头（uuid 本身随机，无法精确比对）
    'data.field1 startsWith UUID:': () => !!data && typeof data.field1 === 'string' && data.field1.startsWith('UUID:'),

    // field2 为固定值
    'data.field2 is field2 value': () => !!data && data.field2 === 'field2 value',

    // param1 回显校验
    'data.param1 equals request param1': () => !!data && data.param1 === param1,

    // dataList 固定为 2 条（服务端写死了两条 MyBeanInner）
    'dataList length is 2': () => dataList.length === 2,

    // dataList[0]/[1] 的 field1 固定（field2 也固定但这里只断言 field1，减少脆弱性）
    'dataList[0].field1 is b1-f1': () => dataList[0] && dataList[0].field1 === 'b1-f1',
    'dataList[1].field1 is b2-f1': () => dataList[1] && dataList[1].field1 === 'b2-f1',
  });

  // Think time：模拟用户请求之间的“思考/间隔”
  // 这里给一个很小的 sleep（0.1s），避免完全打满导致更像“无限冲击”而不是压测+用户行为。
  // 若你希望更激进或更真实，可自行调大/调小。
  // sleep(0.1);
}

