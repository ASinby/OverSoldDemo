create table T_PURCHASE_RECORD
(
    ID            INTEGER
        constraint T_PURCHASE_RECORD_PK
            primary key,
    USER_ID       INTEGER                                not null,
    PRODUCT_ID    INTEGER                                not null,
    PRICE         DECIMAL(16, 2)                         not null,
    QUANTITY      INTEGER                                not null,
    SUM           DECIMAL(16, 2)                         not null,
    PURCHASE_DATE TIMESTAMP(6) default CURRENT TIMESTAMP not null,
    NOTE          VARCHAR(256)
);

comment on table T_PURCHASE_RECORD is '购买记录';

comment on column T_PURCHASE_RECORD.ID is '编号';

comment on column T_PURCHASE_RECORD.USER_ID is '用户编号';

comment on column T_PURCHASE_RECORD.PRODUCT_ID is '产品编号';

comment on column T_PURCHASE_RECORD.PRICE is '单价';

comment on column T_PURCHASE_RECORD.QUANTITY is '数量';

comment on column T_PURCHASE_RECORD.SUM is '总价';

comment on column T_PURCHASE_RECORD.PURCHASE_DATE is '购买时间';

comment on column T_PURCHASE_RECORD.NOTE is '备注';

