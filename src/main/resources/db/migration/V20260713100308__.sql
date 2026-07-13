CREATE TABLE discounts
(
    id         UUID NOT NULL,
    product_id UUID,
    offer      DOUBLE PRECISION,
    name       VARCHAR(255),
    CONSTRAINT pk_discounts PRIMARY KEY (id)
);

ALTER TABLE discounts
    ADD CONSTRAINT FK_DISCOUNTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);