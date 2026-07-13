ALTER TABLE products
    ADD is_available BOOLEAN;

ALTER TABLE products
    ADD is_showing BOOLEAN;

ALTER TABLE products
    ADD location_id UUID;

ALTER TABLE products
    ADD stock INTEGER;

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_LOCATION FOREIGN KEY (location_id) REFERENCES locations (id);