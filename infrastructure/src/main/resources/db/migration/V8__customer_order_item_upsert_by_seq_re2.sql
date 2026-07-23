-- The legacy PEDRE2 sequence (seq_re2) is the true per-line identity for a
-- customer order item, not produto: the same produto can legitimately
-- appear more than once within a single order (e.g. one invoice line per
-- lot/batch of the same product). The original uk_customer_order_item_produto
-- constraint rejected that as a duplicate, which made ingestion fail instead
-- of upserting whenever emerion-load-service sent (or re-sent) such a batch.
ALTER TABLE customer_order_item DROP CONSTRAINT uk_customer_order_item_produto;

ALTER TABLE customer_order_item
    ADD CONSTRAINT uk_customer_order_item_seq_re2 UNIQUE (customer_order_id, seq_re2);
