-- 表名称 = config_info
CREATE TABLE config_info
(
    id                 SERIAL PRIMARY KEY,
    data_id            VARCHAR(255) NOT NULL,
    group_id           VARCHAR(128),
    content            TEXT         NOT NULL,
    md5                VARCHAR(32),
    gmt_create         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    gmt_modified       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    src_user           TEXT,
    src_ip             VARCHAR(50),
    app_name           VARCHAR(128),
    tenant_id          VARCHAR(128)                DEFAULT '',
    c_desc             VARCHAR(256),
    c_use              VARCHAR(64),
    effect             VARCHAR(64),
    type               VARCHAR(64),
    c_schema           TEXT,
    encrypted_data_key VARCHAR(1024)               DEFAULT ''
);
COMMENT ON TABLE config_info IS 'config_info';
COMMENT ON COLUMN config_info.id IS 'id';
COMMENT ON COLUMN config_info.data_id IS 'data_id';
COMMENT ON COLUMN config_info.group_id IS 'group_id';
COMMENT ON COLUMN config_info.content IS 'content';
COMMENT ON COLUMN config_info.md5 IS 'md5';
COMMENT ON COLUMN config_info.gmt_create IS '创建时间';
COMMENT ON COLUMN config_info.gmt_modified IS '修改时间';
COMMENT ON COLUMN config_info.src_user IS 'source user';
COMMENT ON COLUMN config_info.src_ip IS 'source ip';
COMMENT ON COLUMN config_info.app_name IS 'app_name';
COMMENT ON COLUMN config_info.tenant_id IS '租户字段';
COMMENT ON COLUMN config_info.c_desc IS 'configuration description';
COMMENT ON COLUMN config_info.c_use IS 'configuration usage';
COMMENT ON COLUMN config_info.effect IS '配置生效的描述';
COMMENT ON COLUMN config_info.type IS '配置的类型';
COMMENT ON COLUMN config_info.c_schema IS '配置的模式';
COMMENT ON COLUMN config_info.encrypted_data_key IS '密钥';
ALTER TABLE config_info
    ADD CONSTRAINT uk_configinfo_datagrouptenant UNIQUE (data_id, group_id, tenant_id);

-- 表名称 = config_info_gray
CREATE TABLE config_info_gray
(
    id                 SERIAL PRIMARY KEY,
    data_id            VARCHAR(255) NOT NULL,
    group_id           VARCHAR(128) NOT NULL,
    content            TEXT         NOT NULL,
    md5                VARCHAR(32),
    src_user           TEXT,
    src_ip             VARCHAR(100),
    gmt_create         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    gmt_modified       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    app_name           VARCHAR(128),
    tenant_id          VARCHAR(128)                DEFAULT '',
    gray_name          VARCHAR(128) NOT NULL,
    gray_rule          TEXT         NOT NULL,
    encrypted_data_key VARCHAR(256)                DEFAULT ''
);
COMMENT ON TABLE config_info_gray IS 'config_info_gray';
COMMENT ON COLUMN config_info_gray.id IS 'id';
COMMENT ON COLUMN config_info_gray.data_id IS 'data_id';
COMMENT ON COLUMN config_info_gray.group_id IS 'group_id';
COMMENT ON COLUMN config_info_gray.content IS 'content';
COMMENT ON COLUMN config_info_gray.md5 IS 'md5';
COMMENT ON COLUMN config_info_gray.src_user IS 'src_user';
COMMENT ON COLUMN config_info_gray.src_ip IS 'src_ip';
COMMENT ON COLUMN config_info_gray.gmt_create IS 'gmt_create';
COMMENT ON COLUMN config_info_gray.gmt_modified IS 'gmt_modified';
COMMENT ON COLUMN config_info_gray.app_name IS 'app_name';
COMMENT ON COLUMN config_info_gray.tenant_id IS 'tenant_id';
COMMENT ON COLUMN config_info_gray.gray_name IS 'gray_name';
COMMENT ON COLUMN config_info_gray.gray_rule IS 'gray_rule';
COMMENT ON COLUMN config_info_gray.encrypted_data_key IS 'encrypted_data_key';

ALTER TABLE config_info_gray
    ADD CONSTRAINT uk_configinfogray_datagrouptenantgray UNIQUE (data_id, group_id, tenant_id, gray_name);

CREATE INDEX idx_dataid_gmt_modified ON config_info_gray (data_id, gmt_modified);

/**
  idx_dataid_gmt_modified 包含了 gmt_modified 列一个复合索引
  PostgreSQL 在查询时可以使用 idx_dataid_gmt_modified 来优化基于 gmt_modified 的查询
CREATE INDEX idx_gmt_modified ON config_info_gray (gmt_modified);**/

-- 表名称 = config_tags_relation
CREATE TABLE config_tags_relation
(
    id        SERIAL PRIMARY KEY,
    tag_name  VARCHAR(128) NOT NULL,
    tag_type  VARCHAR(64),
    data_id   VARCHAR(255) NOT NULL,
    group_id  VARCHAR(128) NOT NULL,
    tenant_id VARCHAR(128) DEFAULT '',
    nid       SERIAL
);
COMMENT ON TABLE config_tags_relation IS 'config_tag_relation';
COMMENT ON COLUMN config_tags_relation.id IS 'id';
COMMENT ON COLUMN config_tags_relation.tag_name IS 'tag_name';
COMMENT ON COLUMN config_tags_relation.tag_type IS 'tag_type';
COMMENT ON COLUMN config_tags_relation.data_id IS 'data_id';
COMMENT ON COLUMN config_tags_relation.group_id IS 'group_id';
COMMENT ON COLUMN config_tags_relation.tenant_id IS 'tenant_id';
COMMENT ON COLUMN config_tags_relation.nid IS 'nid, 自增长标识';

ALTER TABLE config_tags_relation
    ADD CONSTRAINT uk_configtagrelation_configidtag UNIQUE (id, tag_name, tag_type);
CREATE INDEX idx_tenant_id ON config_tags_relation (tenant_id);

-- 表名称 = group_capacity
CREATE TABLE group_capacity
(
    id                SERIAL PRIMARY KEY,
    group_id          VARCHAR(128) NOT NULL       DEFAULT '',
    quota             INTEGER      NOT NULL       DEFAULT 0,
    usage             INTEGER      NOT NULL       DEFAULT 0,
    max_size          INTEGER      NOT NULL       DEFAULT 0,
    max_aggr_count    INTEGER      NOT NULL       DEFAULT 0,
    max_aggr_size     INTEGER      NOT NULL       DEFAULT 0,
    max_history_count INTEGER      NOT NULL       DEFAULT 0,
    gmt_create        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    gmt_modified      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE group_capacity IS '集群、各Group容量信息表';
COMMENT ON COLUMN group_capacity.id IS '主键ID';
COMMENT ON COLUMN group_capacity.group_id IS 'Group ID，空字符表示整个集群';
COMMENT ON COLUMN group_capacity.quota IS '配额，0表示使用默认值';
COMMENT ON COLUMN group_capacity.usage IS '使用量';
COMMENT ON COLUMN group_capacity.max_size IS '单个配置大小上限，单位为字节，0表示使用默认值';
COMMENT ON COLUMN group_capacity.max_aggr_count IS '聚合子配置最大个数，，0表示使用默认值';
COMMENT ON COLUMN group_capacity.max_aggr_size IS '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值';
COMMENT ON COLUMN group_capacity.max_history_count IS '最大变更历史数量';
COMMENT ON COLUMN group_capacity.gmt_create IS '创建时间';
COMMENT ON COLUMN group_capacity.gmt_modified IS '修改时间';

ALTER TABLE group_capacity
    ADD CONSTRAINT uk_group_id UNIQUE (group_id);

-- 表名称 = his_config_info
CREATE TABLE his_config_info
(
    id                 SERIAL PRIMARY KEY,
    nid                SERIAL,
    data_id            VARCHAR(255) NOT NULL,
    group_id           VARCHAR(128) NOT NULL,
    app_name           VARCHAR(128),
    content            TEXT         NOT NULL,
    md5                VARCHAR(32),
    gmt_create         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    gmt_modified       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    src_user           TEXT,
    src_ip             VARCHAR(50),
    op_type            VARCHAR(10),
    tenant_id          VARCHAR(128)                DEFAULT '',
    encrypted_data_key VARCHAR(1024)               DEFAULT '',
    publish_type       VARCHAR(50)                 DEFAULT 'formal',
    gray_name          VARCHAR(50),
    ext_info           TEXT
);

COMMENT ON TABLE his_config_info IS '多租户改造';
COMMENT ON COLUMN his_config_info.id IS 'id';
COMMENT ON COLUMN his_config_info.nid IS 'nid, 自增标识';
COMMENT ON COLUMN his_config_info.data_id IS 'data_id';
COMMENT ON COLUMN his_config_info.group_id IS 'group_id';
COMMENT ON COLUMN his_config_info.app_name IS 'app_name';
COMMENT ON COLUMN his_config_info.content IS 'content';
COMMENT ON COLUMN his_config_info.md5 IS 'md5';
COMMENT ON COLUMN his_config_info.gmt_create IS '创建时间';
COMMENT ON COLUMN his_config_info.gmt_modified IS '修改时间';
COMMENT ON COLUMN his_config_info.src_user IS 'source user';
COMMENT ON COLUMN his_config_info.src_ip IS 'source ip';
COMMENT ON COLUMN his_config_info.op_type IS 'operation type';
COMMENT ON COLUMN his_config_info.tenant_id IS '租户字段';
COMMENT ON COLUMN his_config_info.encrypted_data_key IS '密钥';
COMMENT ON COLUMN his_config_info.publish_type IS 'publish type gray or formal';
COMMENT ON COLUMN his_config_info.gray_name IS 'gray name';
COMMENT ON COLUMN his_config_info.ext_info IS 'ext info';
/**
  -- CREATE INDEX idx_gmt_create ON his_config_info (gmt_create);
  -- CREATE INDEX idx_did ON his_config_info (data_id);
 */
CREATE INDEX idx_gmt_create_data_id ON his_config_info (gmt_create, data_id);


-- 表名称 = tenant_capacity
CREATE TABLE tenant_capacity
(
    id                SERIAL PRIMARY KEY,
    tenant_id         VARCHAR(128) NOT NULL       DEFAULT '',
    quota             INTEGER      NOT NULL       DEFAULT 0,
    usage             INTEGER      NOT NULL       DEFAULT 0,
    max_size          INTEGER      NOT NULL       DEFAULT 0,
    max_aggr_count    INTEGER      NOT NULL       DEFAULT 0,
    max_aggr_size     INTEGER      NOT NULL       DEFAULT 0,
    max_history_count INTEGER      NOT NULL       DEFAULT 0,
    gmt_create        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    gmt_modified      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tenant_capacity IS '租户容量信息表';
COMMENT ON COLUMN tenant_capacity.id IS '主键ID';
COMMENT ON COLUMN tenant_capacity.tenant_id IS 'Tenant ID';
COMMENT ON COLUMN tenant_capacity.quota IS '配额，0表示使用默认值';
COMMENT ON COLUMN tenant_capacity.usage IS '使用量';
COMMENT ON COLUMN tenant_capacity.max_size IS '单个配置大小上限，单位为字节，0表示使用默认值';
COMMENT ON COLUMN tenant_capacity.max_aggr_count IS '聚合子配置最大个数';
COMMENT ON COLUMN tenant_capacity.max_aggr_size IS '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值';
COMMENT ON COLUMN tenant_capacity.max_history_count IS '最大变更历史数量';
COMMENT ON COLUMN tenant_capacity.gmt_create IS '创建时间';
COMMENT ON COLUMN tenant_capacity.gmt_modified IS '修改时间';

ALTER TABLE tenant_capacity
    ADD CONSTRAINT uk_tenant_id UNIQUE (tenant_id);

-- 表名称 = tenant_info
CREATE TABLE tenant_info
(
    id            SERIAL PRIMARY KEY,
    kp            VARCHAR(128) NOT NULL,
    tenant_id     VARCHAR(128) DEFAULT '',
    tenant_name   VARCHAR(128) DEFAULT '',
    tenant_desc   TEXT,
    create_source VARCHAR(32),
    gmt_create    BIGINT       NOT NULL,
    gmt_modified  BIGINT       NOT NULL
);

COMMENT ON TABLE tenant_info IS 'tenant_info';
COMMENT ON COLUMN tenant_info.id IS 'id';
COMMENT ON COLUMN tenant_info.kp IS 'kp';
COMMENT ON COLUMN tenant_info.tenant_id IS 'tenant_id';
COMMENT ON COLUMN tenant_info.tenant_name IS 'tenant_name';
COMMENT ON COLUMN tenant_info.tenant_desc IS 'tenant_desc';
COMMENT ON COLUMN tenant_info.create_source IS 'create_source';
COMMENT ON COLUMN tenant_info.gmt_create IS '创建时间';
COMMENT ON COLUMN tenant_info.gmt_modified IS '修改时间';

ALTER TABLE tenant_info
    ADD CONSTRAINT uk_tenant_info_kptenantid UNIQUE (kp, tenant_id);


-- 表名称 = users
CREATE TABLE users
(
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN      NOT NULL
);

COMMENT ON TABLE users IS 'users';
COMMENT ON COLUMN users.username IS 'username';
COMMENT ON COLUMN users.password IS 'password';
COMMENT ON COLUMN users.enabled IS 'enabled';

-- 表名称 = roles
CREATE TABLE roles
(
    username VARCHAR(50) NOT NULL,
    role     VARCHAR(50) NOT NULL,
    CONSTRAINT idx_user_role UNIQUE (username, role)
);

COMMENT ON TABLE roles IS 'roles';
COMMENT ON COLUMN roles.username IS 'username';
COMMENT ON COLUMN roles.role IS 'role';

-- 表名称 = permissions
CREATE TABLE permissions
(
    role     VARCHAR(50)  NOT NULL,
    resource VARCHAR(128) NOT NULL,
    action   VARCHAR(8)   NOT NULL,
    CONSTRAINT uk_role_permission UNIQUE (role, resource, action)
);

COMMENT ON TABLE permissions IS 'permissions';
COMMENT ON COLUMN permissions.role IS 'role';
COMMENT ON COLUMN permissions.resource IS 'resource';
COMMENT ON COLUMN permissions.action IS 'action';

-- 创建 config_info_tag 表
CREATE TABLE config_info_tag
(
    id           SERIAL PRIMARY KEY,
    data_id      VARCHAR(255)                                          NOT NULL,
    group_id     VARCHAR(128)                                          NOT NULL,
    tenant_id    VARCHAR(128)                                          NOT NULL,
    tag_id       VARCHAR(128)                                          NOT NULL,
    app_name     VARCHAR(128),
    content      TEXT                                                  NOT NULL,
    md5          VARCHAR(32),
    gmt_create   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    gmt_modified TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    src_user     TEXT,
    src_ip       VARCHAR(50)
);

-- 添加唯一约束
ALTER TABLE config_info_tag
    ADD CONSTRAINT uk_configinfotag_datagrouptenanttag
        UNIQUE (data_id, group_id, tenant_id, tag_id);

-- 添加注释
COMMENT ON TABLE config_info_tag IS 'config_info_tag';
COMMENT ON COLUMN config_info_tag.id IS 'id';
COMMENT ON COLUMN config_info_tag.data_id IS 'data_id';
COMMENT ON COLUMN config_info_tag.group_id IS 'group_id';
COMMENT ON COLUMN config_info_tag.tenant_id IS 'tenant_id';
COMMENT ON COLUMN config_info_tag.tag_id IS 'tag_id';
COMMENT ON COLUMN config_info_tag.app_name IS 'app_name';
COMMENT ON COLUMN config_info_tag.content IS 'content';
COMMENT ON COLUMN config_info_tag.md5 IS 'md5';
COMMENT ON COLUMN config_info_tag.gmt_create IS '创建时间';
COMMENT ON COLUMN config_info_tag.gmt_modified IS '修改时间';
COMMENT ON COLUMN config_info_tag.src_user IS 'source user';
COMMENT ON COLUMN config_info_tag.src_ip IS 'source ip';

-- 创建 config_info_beta 表
CREATE TABLE config_info_beta
(
    id                 SERIAL PRIMARY KEY,
    data_id            VARCHAR(255)                                          NOT NULL,
    group_id           VARCHAR(128)                                          NOT NULL,
    app_name           VARCHAR(128),
    content            TEXT                                                  NOT NULL,
    beta_ips           VARCHAR(1024),
    md5                VARCHAR(32),
    gmt_create         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    gmt_modified       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    src_user           TEXT,
    src_ip             VARCHAR(50),
    tenant_id          VARCHAR(128)                                          NOT NULL,
    encrypted_data_key VARCHAR(1024)                                         NOT NULL
);

-- 添加唯一约束
ALTER TABLE config_info_beta
    ADD CONSTRAINT uk_configinfobeta_datagrouptenant
        UNIQUE (data_id, group_id, tenant_id);

-- 添加注释
COMMENT ON TABLE config_info_beta IS 'config_info_beta';
COMMENT ON COLUMN config_info_beta.id IS 'id';
COMMENT ON COLUMN config_info_beta.data_id IS 'data_id';
COMMENT ON COLUMN config_info_beta.group_id IS 'group_id';
COMMENT ON COLUMN config_info_beta.app_name IS 'app_name';
COMMENT ON COLUMN config_info_beta.content IS 'content';
COMMENT ON COLUMN config_info_beta.beta_ips IS 'betaIps';
COMMENT ON COLUMN config_info_beta.md5 IS 'md5';
COMMENT ON COLUMN config_info_beta.gmt_create IS '创建时间';
COMMENT ON COLUMN config_info_beta.gmt_modified IS '修改时间';
COMMENT ON COLUMN config_info_beta.src_user IS 'source user';
COMMENT ON COLUMN config_info_beta.src_ip IS 'source ip';
COMMENT ON COLUMN config_info_beta.tenant_id IS '租户字段';
COMMENT ON COLUMN config_info_beta.encrypted_data_key IS '密钥';