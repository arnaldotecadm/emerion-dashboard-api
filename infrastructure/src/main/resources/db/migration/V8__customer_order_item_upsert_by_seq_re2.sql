-- The legacy PEDRE2 sequence (seq_re2) is the true per-line identity for a
-- customer order item, not produto: the same produto can legitimately
-- appear more than once within a single order (e.g. one invoice line per
-- lot/batch of the same product). The original uk_customer_order_item_produto
-- constraint rejected that as a duplicate, which made ingestion fail instead
-- of upserting whenever emerion-load-service sent (or re-sent) such a batch.

-- Rows persisted before this column existed were all backfilled to
-- seq_re2 = 0 by V7's ADD COLUMN ... DEFAULT 0, so an order with more than
-- one pre-existing item would violate the new unique constraint below
-- unless renumbered first. Give every row within a duplicated (customer_order_id,
-- seq_re2) group a distinct value, offset above that order's current max
-- seq_re2 so it can never collide with an already-unique/real value coming
-- from emerion-load-service. Non-duplicated rows are left untouched.
WITH order_max_seq AS (
    SELECT customer_order_id, MAX(seq_re2) AS max_seq_re2
    FROM customer_order_item
    GROUP BY customer_order_id
),
duplicated AS (
    SELECT customer_order_id, seq_re2
    FROM customer_order_item
    GROUP BY customer_order_id, seq_re2
    HAVING COUNT(*) > 1
),
to_renumber AS (
    SELECT
        coi.id,
        oms.max_seq_re2 + ROW_NUMBER() OVER (PARTITION BY coi.customer_order_id ORDER BY coi.id) AS new_seq_re2
    FROM customer_order_item coi
    JOIN duplicated d ON d.customer_order_id = coi.customer_order_id AND d.seq_re2 = coi.seq_re2
    JOIN order_max_seq oms ON oms.customer_order_id = coi.customer_order_id
)
UPDATE customer_order_item coi
SET seq_re2 = to_renumber.new_seq_re2
FROM to_renumber
WHERE coi.id = to_renumber.id;

ALTER TABLE customer_order_item DROP CONSTRAINT uk_customer_order_item_produto;

ALTER TABLE customer_order_item
    ADD CONSTRAINT uk_customer_order_item_seq_re2 UNIQUE (customer_order_id, seq_re2);
