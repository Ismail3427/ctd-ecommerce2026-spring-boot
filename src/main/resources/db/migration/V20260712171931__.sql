CREATE TABLE categories
(
    id                 UUID NOT NULL,
    name               VARCHAR(255),
    amount_of_products INTEGER,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE products_categories
(
    categories_id UUID NOT NULL,
    products_id   UUID NOT NULL
);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_categories_model FOREIGN KEY (categories_id) REFERENCES categories (id);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_product_model FOREIGN KEY (products_id) REFERENCES products (id);

ALTER TABLE products
DROP
COLUMN category;