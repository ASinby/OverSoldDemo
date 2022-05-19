create table T_PURCHASE_RECORD
(
    ID            int auto_increment comment '编号'
        primary key,
    USER_ID       int                                 not null comment '用户编号',
    PRODUCT_ID    int                                 not null comment '产品编号',
    PRICE         decimal(16, 2)                      not null comment '单价',
    QUANTITY      int                                 not null comment '数量',
    SUM           decimal(16, 2)                      not null comment '总价',
    PURCHASE_DATE timestamp default CURRENT_TIMESTAMP not null comment '购买时间',
    NOTE          varchar(512)                        null comment '备注'
)
    comment '购买信息';

