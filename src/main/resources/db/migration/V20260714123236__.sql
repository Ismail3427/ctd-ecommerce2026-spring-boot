ALTER TABLE discounts
    ADD retailer_id UUID;

ALTER TABLE discounts
    ADD CONSTRAINT FK_DISCOUNTS_ON_RETAILER FOREIGN KEY (retailer_id) REFERENCES retailers (id);