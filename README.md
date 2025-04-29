# Tracking Number API

## Overview

Generates unique tracking numbers via RESTful API.

### Endpoint

**POST** `/next-tracking-number`

Query parameters:

- `origin_country_id`: ISO 3166-1 alpha-2
- `destination_country_id`: ISO 3166-1 alpha-2
- `weight`: decimal (kg)
- `created_at`: RFC 3339 timestamp
- `customer_id`: UUID
- `customer_name`: String
- `customer_slug`: slug-case string

### Sample Response

```json
{
  "tracking_number": "MYID2504150001",
  "created_at": "2025-04-15T10:05:01+00:00"
}
```

## Run locally

```bash
docker-compose up --build
```

Access: http://localhost:8080/next-tracking-number?...

## Deploy

- Fly.io
- Render.com
- Railway.app