-- dteres was originally modeled as a TIMESTAMPTZ but is a date-only field
-- (matches the Vendedor.dataCadastro precedent); switch the column to DATE,
-- truncating any existing timestamp values down to their date component.
ALTER TABLE customer_order ALTER COLUMN dteres TYPE DATE USING dteres::date;
