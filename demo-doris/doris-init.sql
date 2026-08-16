-- Doris 目标库/表（配合 SeaTunnel `seatunnel-config/mysql_to_doris.conf`）
--
-- 说明：
-- - Doris Stream Load 在目标库不存在时会报 unknown database；`schema_save_mode = CREATE_SCHEMA_WHEN_NOT_EXIST`
--   不能替代「先建库」这一步。
-- - 表采用 UNIQUE KEY（按主键 upsert），与 CDC + sink.enable-delete 一致。
-- - replication_num=1 适合单机演示；多 BE 请按需调大。
--

-- 算子落盘会话变量（GLOBAL：对新连接生效；BE 落盘路径见 doris-config/be.conf）
-- 演示环境 BE mem_limit=4g，exec_mem_limit 设为 2g 便于在复杂查询时触发落盘而非直接 OOM。
-- SET GLOBAL enable_spill = true;
-- SET GLOBAL exec_mem_limit = 2g;
-- SET GLOBAL query_timeout = 3600;

CREATE DATABASE IF NOT EXISTS demot;

DROP TABLE IF EXISTS demot.auth;

CREATE TABLE IF NOT EXISTS demot.auth (
  id BIGINT NOT NULL,
  /*account VARCHAR(64) NOT NULL,*/
  `password` VARCHAR(64) NOT NULL,
  create_time DATETIME NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);

CREATE TABLE IF NOT EXISTS demot.student (
  id BIGINT NOT NULL,
  `name` VARCHAR(64) NULL,
  create_time DATETIME(3) NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);

CREATE TABLE IF NOT EXISTS demot.course (
  id BIGINT NOT NULL,
  student_id BIGINT NULL,
  `name` VARCHAR(64) NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);

CREATE TABLE IF NOT EXISTS demot.dd (
    company_id      BIGINT,
    dj_type         VARCHAR(50),
    /*单据子表类型：jl（记录类型）、mt（material原材料）、mx（明细匹）、rk（调拨单入库）、ck（调拨单出库）*/
    dj_type_sub     VARCHAR(10),
    /*单据表id*/
    dj_id           BIGINT,
    /*记录表id*/
    jl_id           BIGINT,
    /*原材料表id*/
    mt_id           BIGINT,
    /*明细表id*/
    mx_id           BIGINT,
    /*引用单据id*/
    dd_dj_id        BIGINT,
    /*引用记录id*/
    dd_jl_id        BIGINT,
    ck_id           BIGINT,
    /*仓位id*/
    cw_id           BIGINT,
    cp_id           BIGINT,
    cp_ys_id        BIGINT,
    fk              VARCHAR(50),
    kz              VARCHAR(50),
    dw_id           BIGINT,
    jh              STRING,
    gh              STRING,
    gg              STRING,
    tm              VARCHAR(100),
    ph              STRING,
    /*染厂色号*/
    ck_zdy1         STRING,
    /*工艺批次号*/
    ck_zdy2         STRING,
    /*下单客户*/
    ck_zdy3         STRING,
    /*供应商成本价*/
    ck_zdy4         STRING,
    /*染色建议*/
    dyeing_advice   STRING,
    /*仓库字段5*/
    ck_zdy5         STRING,
    /*数量2*/
    zsl2            DECIMAL(18, 4),
    /*单位2*/
    dw2_id          BIGINT,
    /*业务员id*/
    ywy_id          BIGINT,
    /*往来单位id，其他出入库单往来单位id为0*/
    wldw_id         BIGINT,
    /*布/供*/
    bp_gys_id       BIGINT,
    /*布/加*/
    bp_jgs_id       BIGINT,
    /*布/客*/
    bp_kh_id        BIGINT,
    /*
     * 1、采购来货结算调用采购来货单时重复计算，使用引用单据类型解决这个问题
     */
    yy_djlx         VARCHAR(50),
    has_count       INT,
    is_jd           INT,
    is_sh           INT,
    is_delete       INT,
    is_zf           INT,
    is_qx           INT,
    /*djlb_fh和is_fh用于协助计算布匹库存库存情况表中的订单未发数据，启用复核的客户订单复核后才算入订单未发，未启用复核的客户订单直接算入订单未发*/
    /*单据类别是否需要复核*/
    djlb_fh         INT,
    /*是否复核*/
    is_fh           INT,
    /*开单时间*/
    kdsj            VARCHAR(30),
    /*审核时间*/
    sh_sj           VARCHAR(30),
    /*作废时间*/
    zf_sj           VARCHAR(30),
    dh              VARCHAR(50),
    /*采集app条码查询待出库明细回显功能中，采集记录的单号使用dd_dh回显（客户订单编号）*/
    dd_dh           VARCHAR(50),
    /*制单人id*/
    zdr_id          BIGINT,
    /*经手人id*/
    jsr_id          BIGINT,
    /*审核人id*/
    shr_id          BIGINT,
    /*作废人id*/
    zfr_id          BIGINT,
    /*记录的备注*/
    jl_bz           STRING,
    /*单据备注*/
    dj_bz           STRING,
    /*品牌*/
    brand           STRING,
    /*款号*/
    style_number    STRING,
    /*季度*/
    quarter         STRING,
    /*业务跟单*/
    ywgd_staff_id   BIGINT,
    /*单据流水号*/
    dj_lsh          INT,
    ps              DECIMAL(18, 4),
    sl              DECIMAL(18, 4)
)
UNIQUE KEY(company_id, dj_type, dj_type_sub, dj_id, jl_id, mt_id, mx_id)
AUTO PARTITION BY LIST(company_id)()
DISTRIBUTED BY HASH(company_id, dj_id) BUCKETS 8
PROPERTIES (
   "replication_num" = "1",
   "enable_unique_key_merge_on_write" = "true"
);
