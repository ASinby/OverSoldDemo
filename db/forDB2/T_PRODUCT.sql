create table T_PRODUCT
(
    ID           INTEGER
        constraint T_PRODUCT_PK
            primary key,
    PRODUCT_NAME VARCHAR(60)       not null,
    STOCK        INTEGER           not null,
    PRICE        DECIMAL(16, 2)    not null,
    VERSION      INTEGER default 0 not null,
    NOTE         VARCHAR(256)
);

comment on table T_PRODUCT is '产品信息';

comment on column T_PRODUCT.ID is '编号';

comment on column T_PRODUCT.PRODUCT_NAME is '产品名称';

comment on column T_PRODUCT.STOCK is '库存';

comment on column T_PRODUCT.PRICE is '单价';

comment on column T_PRODUCT.VERSION is '版本号';

comment on column T_PRODUCT.NOTE is '备注';

INSERT INTO T_PRODUCT (ID, PRODUCT_NAME, STOCK, PRICE, VERSION, NOTE) VALUES (1, '威化饼干', 80, 5.00, 0, '来自澳洲');