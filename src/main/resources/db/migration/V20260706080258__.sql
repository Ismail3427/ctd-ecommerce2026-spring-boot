CREATE TABLE cart
(
    id         UUID NOT NULL,
    product_id VARCHAR(255),
    user_id    VARCHAR(255),
    CONSTRAINT pk_cart PRIMARY KEY (id)
);

ALTER TABLE cart
    ADD CONSTRAINT uc_cart_userid UNIQUE (user_id);