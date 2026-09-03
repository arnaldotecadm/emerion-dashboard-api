ALTER TABLE customer_credit
DROP CONSTRAINT uk_customer_credit_customer_sequencia;

ALTER TABLE customer_credit
    ADD CONSTRAINT uk_customer_credit_customer_sequencia
        UNIQUE (customer_external_id, sequencia, data);