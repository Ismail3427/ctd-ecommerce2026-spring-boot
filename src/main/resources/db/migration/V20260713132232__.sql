CREATE TABLE coupons
(
    id    UUID NOT NULL,
    code  VARCHAR(255),
    offer DOUBLE PRECISION,
    CONSTRAINT pk_coupons PRIMARY KEY (id)
);