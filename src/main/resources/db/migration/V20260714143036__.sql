ALTER TABLE orders
DROP
COLUMN final_price_in_cents;

ALTER TABLE orders
    ADD final_price_in_cents DOUBLE PRECISION;