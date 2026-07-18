import http from 'k6/http';
import { check } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const scenario = __ENV.SCENARIO || 'steady';

const steady = {
  executor: 'constant-arrival-rate', rate: 100, timeUnit: '1s', duration: '5m',
  preAllocatedVUs: 100, maxVUs: 500,
};
const spike = {
  executor: 'ramping-arrival-rate', startRate: 100, timeUnit: '1s', preAllocatedVUs: 200, maxVUs: 2000,
  stages: [{ target: 100, duration: '1m' }, { target: 1000, duration: '10s' }, { target: 1000, duration: '2m' }],
};
export const options = { scenarios: { [scenario]: scenario === 'spike' ? spike : steady }, thresholds: { http_req_failed: ['rate<0.01'], http_req_duration: ['p(95)<500'] } };

export default function () {
  const version = Date.now() * 1000 + __VU * 100 + __ITER;
  const body = JSON.stringify({ listingId: `LOAD-${__VU}-${__ITER}`, eventId: 'EVT-LOAD', section: '101', row: 'A', seats: [1, 2], price: 50.00, currency: 'USD', available: true, version, updatedAt: new Date().toISOString() });
  const response = http.post(`${baseUrl}/api/v1/partners/partner-a/inventory`, body, { headers: { 'Content-Type': 'application/json', 'X-API-Key': __ENV.PARTNER_API_KEY || 'partner-a-demo-key', 'Idempotency-Key': `load-${__VU}-${__ITER}-${Date.now()}` } });
  check(response, { accepted: r => r.status === 202 });
}

