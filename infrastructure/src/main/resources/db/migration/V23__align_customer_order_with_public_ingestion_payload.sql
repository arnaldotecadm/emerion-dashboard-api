ALTER TABLE customer_order
    RENAME COLUMN cod_cli TO codigo_cliente;
ALTER TABLE customer_order
    ALTER COLUMN codigo_cliente TYPE INTEGER USING codigo_cliente::INTEGER;
ALTER TABLE customer_order RENAME COLUMN nronfe TO numero_pedido;
ALTER TABLE customer_order RENAME COLUMN dteres TO data_pedido;
ALTER TABLE customer_order RENAME COLUMN sitres TO status_pedido;
ALTER TABLE customer_order RENAME COLUMN totger TO total_pedido_com_impostos;
ALTER TABLE customer_order RENAME COLUMN totres TO total_pedido_sem_impostos;
ALTER TABLE customer_order RENAME COLUMN totipi TO total_ipi;
ALTER TABLE customer_order RENAME COLUMN totsub TO total_substituicao_tributaria;
ALTER TABLE customer_order RENAME COLUMN totdescinc TO total_desconto_incondicional;
ALTER TABLE customer_order RENAME COLUMN totfrt TO total_frete;
ALTER TABLE customer_order RENAME COLUMN totseg TO total_seguro;
ALTER TABLE customer_order RENAME COLUMN totoutdesp TO total_outras_despesas;
ALTER TABLE customer_order
    DROP COLUMN cnpj_empresa,
    ADD COLUMN codigo_empresa INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN total_icms NUMERIC(19, 4) NOT NULL DEFAULT 0,
    ADD COLUMN total_pis NUMERIC(19, 4) NOT NULL DEFAULT 0,
    ADD COLUMN total_cofins NUMERIC(19, 4) NOT NULL DEFAULT 0,
    ADD COLUMN codigo_padrao_faturamento VARCHAR(32);

ALTER TABLE customer_order_item
    ADD COLUMN cod_emp INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN dteres DATE NOT NULL DEFAULT CURRENT_DATE,
    ADD COLUMN numres VARCHAR(64) NOT NULL DEFAULT '',
    ADD COLUMN custo_total NUMERIC(19, 4),
    ADD COLUMN lucro_valor NUMERIC(19, 4),
    ADD COLUMN lucro_porcentagem NUMERIC(19, 4);

CREATE INDEX idx_customer_order_codigo_cliente ON customer_order (codigo_cliente);
CREATE INDEX idx_customer_order_status_pedido ON customer_order (status_pedido);
CREATE INDEX idx_customer_order_codigo_empresa ON customer_order (codigo_empresa);
