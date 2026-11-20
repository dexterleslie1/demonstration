import http from 'k6/http';
import { check } from 'k6';
import { Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const LIMIT = __ENV.LIMIT || '50000';
const PATH = `/api/v1/external/bpkc-mxb/random?limit=${LIMIT}`;

const errorRate = new Rate('errors');

export const options = {
  // 丢弃响应体，避免 50k JSON × 多 VU 把内存顶到数 GB
  discardResponseBodies: true,
  scenarios: {
    random_bpkc_mxb: {
      executor: 'constant-vus',
      vus: Number(__ENV.VUS || 16),
      duration: __ENV.DURATION || '10m',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.1'],
    http_req_duration: ['p(95)<60000'],
    errors: ['rate<0.1'],
  },
  // 大响应体，适当放宽超时
  timeout: '120s',
};

export default function () {
  const res = http.get(`${BASE_URL}${PATH}`, {
    headers: { 'Accept-Encoding': 'gzip' },
    tags: { name: 'bpkc-mxb-random' },
    timeout: '120s',
    responseType: 'none',
  });

  const ok = check(res, {
    'status is 200': (r) => r.status === 200,
  });

  errorRate.add(!ok);
}
