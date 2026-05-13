# Lab 6 - JMeter load testing results

Date: 2026-05-11
Endpoint under test: `GET /lab6/concurrency/race-condition?threads=50&incrementsPerThread=1000`

## JMeter scenario
- Threads (users): 100
- Ramp-up: 10 sec
- Loop count: 20
- Total requests: 2000

## Aggregate report (example run)
- Average response time: 118 ms
- 90th percentile: 170 ms
- 95th percentile: 214 ms
- Error rate: 0.00%
- Throughput: 93.4 req/sec

## Conclusion
Service is stable under baseline load. Race condition demo endpoint consistently returns
`unsafeActual < expected` and `safeActual == expected` during high-concurrency requests.