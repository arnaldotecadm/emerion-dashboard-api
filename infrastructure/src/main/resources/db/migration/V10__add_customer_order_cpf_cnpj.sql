-- Adds the order's customer's own cpf_cnpj (distinct from cnpj_empresa, the
-- retailer/tenant CNPJ). Nullable since emerion-load-service's
-- CustomerOrderIngestionDto.cpfCnpj is itself nullable.
ALTER TABLE customer_order ADD COLUMN cpf_cnpj VARCHAR(20);
