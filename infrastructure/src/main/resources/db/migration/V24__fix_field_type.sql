ALTER TABLE customer_credit ALTER COLUMN data TYPE DATE USING data::date;
ALTER TABLE customer_credit ALTER COLUMN data_pedido TYPE DATE USING data_pedido::date;