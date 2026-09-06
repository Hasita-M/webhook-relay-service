# Webhook Relay & Retry Service
A webhook relay that sits between senders and receivers, reducing the risk of lost
deliveries by automatically retrying failed webhook attempts with exponential backoff.

**Live: https://webhook-relay-service.onrender.com**
*(Note: Due to free-tier hosting on Render the first request may take up to a minute if the service was idle.)*

## What it does
Target receivers are small developer teams or startups and senders are their customers or partner integrations (e.g. payment service confirming a transaction). The service accepts incoming requests, stores them durably in Postgres and attempts delivery to a configured destination, retrying failed attempts via exponential backoff (2s, 4s, 8s,
16s...), up to a configurable retry limit) via a Redis-backed queue.

- **HMAC-SHA256 signature verification** on every delivery, so receivers can confirm
  requests genuinely came from the relay and weren't tampered with in transit
- **Idempotency handling** for duplicate incoming sends
- **Secrets encrypted at rest** (AES-256/GCM) to securely store in database
- **Multi-receiver support** - each integration gets its own webhook URL, secure
    secret, and a private management token (no login required)
- **Per-receiver customizability** - destination URL, max retry count (1-10), and a
  **chaos mode** toggle that lets an integrator deliberately trigger simulated failures
  against their own endpoint
- **Full delivery timeline** - every attempt (success or failure) is logged and viewable in the UI

## Try it yourself

Set up your own integration on the live site, or test the relay directly:
```bash
curl -X POST [your Render URL]/webhook/{receiverId} \
  -H "Content-Type: application/json" \
  -d '{"event":"test.event","data":"hello"}'
```

## Architecture

- **Spring Boot** (Java)
- **PostgreSQL** (hosted on Neon)
- **Redis** (hosted on Upstash)
- Deployed via Docker on Render




