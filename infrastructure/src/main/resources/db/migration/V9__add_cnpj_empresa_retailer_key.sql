-- cnpjEmpresa is the retailer/tenant CNPJ that emerion-load-service stamps
-- onto every record it sends (one deployment per retailer). It is now a
-- required field on all six ingested resources, used to segregate data by
-- retailer. Existing rows predate this field, so we backfill them with an
-- empty-string placeholder before enforcing NOT NULL, mirroring the
-- established seq_re2 backfill pattern from V7/V8.

-- customer: brand-new column
ALTER TABLE customer ADD COLUMN cnpj_empresa VARCHAR(20) NOT NULL DEFAULT '';
ALTER TABLE customer ALTER COLUMN cnpj_empresa DROP DEFAULT;
CREATE INDEX idx_customer_cnpj_empresa ON customer (cnpj_empresa);

-- product: brand-new column
ALTER TABLE product ADD COLUMN cnpj_empresa VARCHAR(20) NOT NULL DEFAULT '';
ALTER TABLE product ALTER COLUMN cnpj_empresa DROP DEFAULT;
CREATE INDEX idx_product_cnpj_empresa ON product (cnpj_empresa);

-- customer_credit: brand-new column
ALTER TABLE customer_credit ADD COLUMN cnpj_empresa VARCHAR(20) NOT NULL DEFAULT '';
ALTER TABLE customer_credit ALTER COLUMN cnpj_empresa DROP DEFAULT;
CREATE INDEX idx_customer_credit_cnpj_empresa ON customer_credit (cnpj_empresa);

-- vendedor: brand-new column
ALTER TABLE vendedor ADD COLUMN cnpj_empresa VARCHAR(20) NOT NULL DEFAULT '';
ALTER TABLE vendedor ALTER COLUMN cnpj_empresa DROP DEFAULT;
CREATE INDEX idx_vendedor_cnpj_empresa ON vendedor (cnpj_empresa);

-- customer_address: column already exists, nullable; backfill and enforce NOT NULL
UPDATE customer_address SET cnpj_empresa = '' WHERE cnpj_empresa IS NULL;
ALTER TABLE customer_address ALTER COLUMN cnpj_empresa SET NOT NULL;
CREATE INDEX idx_customer_address_cnpj_empresa ON customer_address (cnpj_empresa);

-- customer_order: column already exists, nullable; backfill and enforce NOT NULL
UPDATE customer_order SET cnpj_empresa = '' WHERE cnpj_empresa IS NULL;
ALTER TABLE customer_order ALTER COLUMN cnpj_empresa SET NOT NULL;
CREATE INDEX idx_customer_order_cnpj_empresa ON customer_order (cnpj_empresa);
