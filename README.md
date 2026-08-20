# Webhook Relay & Retry Service
This project is a webhook relay acting as an intermediary between senders and receivers, reducing the risk of lost webhooks by retrying failed webhook attempts and noting them. Target receivers are small developer teams or startups and senders are their customers or partner integrations (e.g. payment service confirming a transaction). The service accepts incoming requests, verifies them via HMAC, stores them in Postgres and attempts delivery to the destination URL, retrying failed attempts via a Redis queue with exponential backoff while handling idempotency so duplicate sends are not double processed. Developers see a dashboard showing delivery history of successes, failures, and pending retries in a timeline view with filters, letting them debug their own endpoints. 

## Status

Currently implemented:
- Receives incoming webhook POSTs
- Persists events to Postgres
- Attempts delivery once, synchronously
- Marks each event SUCCESS or FAILED

Planned:
- HMAC signature verification
- Redis-backed retry queue with exponential backoff
- Idempotency handling for duplicate sends
- Dashboard for delivery history