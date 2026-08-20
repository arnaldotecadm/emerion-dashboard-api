ALTER TABLE customer
    ADD COLUMN uf VARCHAR(2),
    ADD COLUMN macro_regiao VARCHAR(128),
    ADD COLUMN micro_regiao VARCHAR(128),
    ADD COLUMN setor VARCHAR(128);

ALTER TABLE customer_address_detail
    ADD COLUMN tipo_endereco VARCHAR(32),
    ADD COLUMN ddd_telefone VARCHAR(8),
    ADD COLUMN ddd_fax VARCHAR(8),
    ADD COLUMN ddd_celular VARCHAR(8),
    ADD COLUMN celular VARCHAR(32);
