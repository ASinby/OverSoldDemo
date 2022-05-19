create table T_PRODUCT
(
    ID           int auto_increment comment '编号'
        primary key,
    PRODUCT_NAME varchar(60)    not null comment '产品名称',
    STOCK        int            not null comment '库存',
    PRICE        decimal(16, 2) not null comment '单价',
    VERSION      int default 0  not null comment '版本号',
    NOTE         varchar(256)   null comment '备注'
)
    comment '产品信息';

INSERT INTO T_PRODUCT (ID, PRODUCT_NAME, STOCK, PRICE, VERSION, NOTE) VALUES (1, '威化饼干', 0, 5.00, 151, '来自澳洲');