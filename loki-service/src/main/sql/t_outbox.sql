create table if not exists t_outbox(
    id bigint auto_increment comment 'id' primary key,
    topic varchar(32) comment '主题',
    tags varchar(128) comment '标签组',
    payload varchar(2048) comment '载荷',
    retry_count smallint default 0 comment '重试次数',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间',
    awaken_time datetime default current_timestamp on update current_timestampcomment '唤醒时间',
    deleted tinyint default 0 comment '删除标志'
) engine=InnoDB default charset=utf8mb4 comment 'outbox table';
