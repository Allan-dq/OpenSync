CREATE TABLE partners (
 id UUID PRIMARY KEY, partner_id VARCHAR(100) NOT NULL UNIQUE, name VARCHAR(200) NOT NULL,
 api_key_hash VARCHAR(255) NOT NULL, hmac_secret_hash VARCHAR(255), active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE partner_schema_mappings (
 id UUID PRIMARY KEY, partner_id VARCHAR(100) NOT NULL, mapping_version INTEGER NOT NULL,
 mapping_json JSONB NOT NULL, status VARCHAR(30) NOT NULL, approved_by VARCHAR(200),
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL,
 UNIQUE(partner_id,mapping_version)
);
CREATE TABLE inventory (
 id UUID PRIMARY KEY, partner_id VARCHAR(100) NOT NULL, partner_listing_id VARCHAR(200) NOT NULL,
 event_id VARCHAR(200) NOT NULL, venue_id VARCHAR(200), section VARCHAR(100) NOT NULL,
 row_number VARCHAR(100) NOT NULL, seat_start INTEGER, seat_end INTEGER, quantity INTEGER NOT NULL,
 unit_price NUMERIC(12,2) NOT NULL, currency CHAR(3) NOT NULL, inventory_status VARCHAR(30) NOT NULL,
 source_version BIGINT NOT NULL, partner_updated_at TIMESTAMPTZ NOT NULL, sync_status VARCHAR(30) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL, entity_version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(partner_id,partner_listing_id), CHECK(quantity >= 0), CHECK(unit_price >= 0)
);
CREATE TABLE inventory_sync_jobs (
 id UUID PRIMARY KEY, request_id VARCHAR(100) NOT NULL UNIQUE, correlation_id VARCHAR(100) NOT NULL,
 partner_id VARCHAR(100) NOT NULL, partner_listing_id VARCHAR(200), status VARCHAR(30) NOT NULL,
 error_code VARCHAR(100), error_message VARCHAR(1000), created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE idempotency_records (
 id UUID PRIMARY KEY, partner_id VARCHAR(100) NOT NULL, idempotency_key VARCHAR(255) NOT NULL,
 request_id VARCHAR(100) NOT NULL, correlation_id VARCHAR(100) NOT NULL, response_json JSONB NOT NULL,
 expires_at TIMESTAMPTZ NOT NULL, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL,
 UNIQUE(partner_id,idempotency_key)
);
CREATE TABLE outbox_events (
 id UUID PRIMARY KEY, topic VARCHAR(200) NOT NULL, event_key VARCHAR(255) NOT NULL, payload JSONB NOT NULL,
 status VARCHAR(30) NOT NULL, attempt_count INTEGER NOT NULL DEFAULT 0, published_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_outbox_pending ON outbox_events(status,created_at);
CREATE TABLE dead_letter_messages (
 message_id UUID PRIMARY KEY, original_topic VARCHAR(200) NOT NULL, payload JSONB NOT NULL,
 error_code VARCHAR(100) NOT NULL, error_message VARCHAR(1000) NOT NULL, stack_trace_summary VARCHAR(2000),
 retry_count INTEGER NOT NULL, first_failed_at TIMESTAMPTZ NOT NULL, last_failed_at TIMESTAMPTZ NOT NULL,
 status VARCHAR(30) NOT NULL, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE publication_attempts (
 id UUID PRIMARY KEY, message_id UUID NOT NULL, attempt_number INTEGER NOT NULL, status VARCHAR(30) NOT NULL,
 http_status INTEGER, error_message VARCHAR(1000), duration_ms BIGINT NOT NULL, attempted_at TIMESTAMPTZ NOT NULL
);

