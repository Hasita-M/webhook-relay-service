# Webhook Relay & Retry Service
This project is a webhook relay acting as an intermediary between senders and receivers, reducing the risk of lost webhooks by retrying failed webhook attempts and noting them. Target receivers are small developer teams or startups and senders are their customers or partner integrations (e.g. payment service confirming a transaction). The service accepts incoming requests, verifies them via HMAC, stores them in Postgres and attempts delivery to the destination URL, retrying failed attempts via a Redis queue with exponential backoff while handling idempotency so duplicate sends are not double processed. Developers see a dashboard showing delivery history of successes, failures, and pending retries in a timeline view with filters, letting them debug their own endpoints. 

## Status

Currently implemented:
- Receives incoming webhook POSTs
- Persists events to Postgres
- Attempts delivery, with automatic retries on failure
- Redis-backed retry queue with exponential backoff (2s, 4s...up to 5 attempts)
- Full delivery history logged per attempt
- Marks each event SUCCESS or FAILED
- Deployed live on Render, Postgres on Neon, Redis on Upstash
- HMAC signature verification
- Idempotency handling for duplicate incoming sends

Planned:
- Dashboard for delivery history
- Facilitating multiple receivers
