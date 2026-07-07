CREATE TABLE usersession (
                             userid VARCHAR NOT NULL CONSTRAINT usersession_pk PRIMARY KEY,
                             name VARCHAR NOT NULL,
                             email VARCHAR NOT NULL,
                             ipaddress VARCHAR NOT NULL,
                             loginscount INTEGER NOT NULL,
                             timestamp DATE NOT NULL
);