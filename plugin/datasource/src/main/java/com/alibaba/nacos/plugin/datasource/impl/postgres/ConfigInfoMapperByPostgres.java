package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.ArrayUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.NamespaceUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  The mysql implementation of ConfigInfoMapper.
 *
 *  @author fuhouyu
 */
public class ConfigInfoMapperByPostgres extends AbstractMapperByPostgres implements ConfigInfoMapper {

    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    @Override
    public MapperResult findConfigInfoByAppFetchRows(MapperContext mapperContext) {
        String appName = (String) mapperContext.getWhereParameter("app_name");
        String tenantId = (String) mapperContext.getWhereParameter("tenantId");
        String sql = "SELECT id, data_id, group_id, tenant_id, app_name, content " +
                "FROM config_info " +
                "WHERE tenant_id LIKE ? AND app_name = ? " +
                "OFFSET ? LIMIT ?";
        return new MapperResult(sql,
                CollectionUtils.list(tenantId, appName, mapperContext.getStartRow(), mapperContext.getPageSize()));
    }

    @Override
    public MapperResult getTenantIdList(MapperContext mapperContext) {
        String sql = "SELECT tenant_id " +
                "FROM config_info " +
                "WHERE tenant_id != ? " +
                "GROUP BY tenant_id " +
                "OFFSET ? LIMIT ?";
        return new MapperResult(sql,
                CollectionUtils.list(NamespaceUtil.getNamespaceDefaultId(), mapperContext.getStartRow(), mapperContext.getPageSize()));
    }

    @Override
    public MapperResult getGroupIdList(MapperContext mapperContext) {
        String sql = "SELECT group_id " +
                "FROM config_info " +
                "WHERE tenant_id = ? " +
                "GROUP BY group_id " +
                "OFFSET ? LIMIT ?";
        return new MapperResult(sql,
                CollectionUtils.list(NamespaceUtil.getNamespaceDefaultId(), mapperContext.getStartRow(), mapperContext.getPageSize()));
    }

    @Override
    public MapperResult findAllConfigKey(MapperContext context) {
        String tenantId = (String) context.getWhereParameter("tenantId");
        String sql = "SELECT data_id, group_id, app_name " +
                "FROM config_info " +
                "WHERE tenant_id LIKE ? " +
                "ORDER BY id " +
                "OFFSET ? LIMIT ?";
        return new MapperResult(sql,
                CollectionUtils.list(tenantId, context.getStartRow(), context.getPageSize()));
    }

    @Override
    public MapperResult findAllConfigInfoBaseFetchRows(MapperContext context) {
        String sql = "SELECT t.id, data_id, group_id, content, md5 " +
                "FROM (SELECT id FROM config_info ORDER BY id OFFSET ? LIMIT ?) g, config_info t " +
                "WHERE g.id = t.id";
        return new MapperResult(sql,
                CollectionUtils.list(context.getStartRow(), context.getPageSize()));
    }

    @Override
    public MapperResult findAllConfigInfoFragment(MapperContext context) {
        String sql = "SELECT id, data_id, group_id, tenant_id, app_name, content, md5, gmt_modified, type, encrypted_data_key " +
                "FROM config_info " +
                "WHERE id > ? " +
                "ORDER BY id ASC " +
                "OFFSET ? LIMIT ?";
        return new MapperResult(sql,
                CollectionUtils.list(context.getWhereParameter("id"), context.getStartRow(), context.getPageSize()));
    }

    @Override
    public MapperResult findChangeConfigFetchRows(MapperContext context) {
        final String tenant = (String) context.getWhereParameter(FieldConstant.TENANT_ID);
        final String dataId = (String) context.getWhereParameter(FieldConstant.DATA_ID);
        final String group = (String) context.getWhereParameter(FieldConstant.GROUP_ID);
        final String appName = (String) context.getWhereParameter(FieldConstant.APP_NAME);
        final String tenantTmp = StringUtils.isBlank(tenant) ? StringUtils.EMPTY : tenant;
        final Timestamp startTime = (Timestamp) context.getWhereParameter(FieldConstant.START_TIME);
        final Timestamp endTime = (Timestamp) context.getWhereParameter(FieldConstant.END_TIME);

        List<Object> paramList = new ArrayList<>();

        // 构建 WHERE 子句和参数列表
        StringBuilder whereBuilder = new StringBuilder("1=1");

        if (!StringUtils.isBlank(dataId)) {
            whereBuilder.append(" AND data_id LIKE ?");
            paramList.add("%" + dataId + "%");
        }
        if (!StringUtils.isBlank(group)) {
            whereBuilder.append(" AND group_id LIKE ?");
            paramList.add("%" + group + "%");
        }
        if (!StringUtils.isBlank(tenantTmp)) {
            whereBuilder.append(" AND tenant_id = ?");
            paramList.add(tenantTmp);
        }
        if (!StringUtils.isBlank(appName)) {
            whereBuilder.append(" AND app_name = ?");
            paramList.add(appName);
        }
        if (startTime != null) {
            whereBuilder.append(" AND gmt_modified >= ?");
            paramList.add(startTime);
        }
        if (endTime != null) {
            whereBuilder.append(" AND gmt_modified <= ?");
            paramList.add(endTime);
        }

        // 获取最后一个最大ID参数
        Object lastMaxId = context.getWhereParameter(FieldConstant.LAST_MAX_ID);
        if (lastMaxId == null) {
            throw new IllegalArgumentException("LAST_MAX_ID parameter is required");
        }
        whereBuilder.append(" AND id > ?");

        // 添加分页参数
        whereBuilder.append(" ORDER BY id ASC LIMIT ? OFFSET ?");

        // 构建完整的SQL语句
        String sql = "SELECT id, data_id, group_id, tenant_id, app_name, type, md5, gmt_modified " +
                "FROM config_info " +
                whereBuilder;

        // 添加分页参数到参数列表
        paramList.add(context.getPageSize());
        paramList.add(context.getStartRow());

        return new MapperResult(sql, paramList);
    }

    @Override
    public MapperResult listGroupKeyMd5ByPageFetchRows(MapperContext context) {
        String sql = "SELECT t.id, data_id, group_id, tenant_id, app_name, md5, type, gmt_modified, encrypted_data_key " +
                "FROM (SELECT id FROM config_info ORDER BY id OFFSET ? LIMIT ?) g, config_info t " +
                "WHERE g.id = t.id";
        return new MapperResult(sql,
                CollectionUtils.list(context.getStartRow(), context.getPageSize()));
    }

    @Override
    public MapperResult findConfigInfoBaseLikeFetchRows(MapperContext context) {
        final String dataId = (String) context.getWhereParameter(FieldConstant.DATA_ID);
        final String group = (String) context.getWhereParameter(FieldConstant.GROUP_ID);
        final String content = (String) context.getWhereParameter(FieldConstant.CONTENT);

        final String sqlFetchRows = "SELECT id, data_id, group_id, tenant_id, content FROM config_info WHERE ";
        String where = " 1=1 AND tenant_id = ? ";

        List<Object> paramList = new ArrayList<>();
        paramList.add(NamespaceUtil.getNamespaceDefaultId());

        if (!StringUtils.isBlank(dataId)) {
            where += " AND data_id LIKE ? ";
            paramList.add("%" + dataId + "%");
        }
        if (!StringUtils.isBlank(group)) {
            where += " AND group_id LIKE ? ";
            paramList.add("%" + group + "%");
        }
        if (!StringUtils.isBlank(content)) {
            where += " AND content LIKE ? ";
            paramList.add("%" + content + "%");
        }

        int offset = context.getStartRow();
        int limit = context.getPageSize();

        String finalSql = sqlFetchRows + where + " LIMIT ? OFFSET ?";
        paramList.add(limit);
        paramList.add(offset);

        return new MapperResult(finalSql, paramList);
    }

    @Override
    public MapperResult findConfigInfo4PageFetchRows(MapperContext context) {
        final String tenant = (String) context.getWhereParameter(FieldConstant.TENANT_ID);
        final String dataId = (String) context.getWhereParameter(FieldConstant.DATA_ID);
        final String group = (String) context.getWhereParameter(FieldConstant.GROUP_ID);
        final String appName = (String) context.getWhereParameter(FieldConstant.APP_NAME);
        final String content = (String) context.getWhereParameter(FieldConstant.CONTENT);

        List<Object> paramList = new ArrayList<>();

        final String sql = "SELECT id,data_id,group_id,tenant_id,app_name,content,type,encrypted_data_key FROM config_info";
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" tenant_id= ? ");
        paramList.add(tenant);
        if (StringUtils.isNotBlank(dataId)) {
            where.append(" AND data_id= ? ");
            paramList.add(dataId);
        }
        if (StringUtils.isNotBlank(group)) {
            where.append(" AND group_id= ? ");
            paramList.add(group);
        }
        if (StringUtils.isNotBlank(appName)) {
            where.append(" AND app_name= ? ");
            paramList.add(appName);
        }
        if (!StringUtils.isBlank(content)) {
            where.append(" AND content LIKE ? ");
            paramList.add("%" + content + "%");
        }
        int offset = context.getStartRow();
        int limit = context.getPageSize();
        paramList.add(limit);
        paramList.add(offset);
        return new MapperResult(sql + where + " LIMIT ? OFFSET ?",
                paramList);
    }

    @Override
    public MapperResult findConfigInfoBaseByGroupFetchRows(MapperContext context) {
        String groupId = (String) context.getWhereParameter("groupId");
        String tenantId = (String) context.getWhereParameter("tenantId");

        String sql = "SELECT id, data_id, group_id, content " +
                "FROM config_info " +
                "WHERE group_id = ? AND tenant_id = ? " +
                "OFFSET ? LIMIT ?";
        return new MapperResult(sql,
                CollectionUtils.list(groupId, tenantId, context.getStartRow(), context.getPageSize()));
    }

    @Override
    public MapperResult findConfigInfoLike4PageFetchRows(MapperContext context) {
        final String tenant = (String) context.getWhereParameter(FieldConstant.TENANT_ID);
        final String dataId = (String) context.getWhereParameter(FieldConstant.DATA_ID);
        final String group = (String) context.getWhereParameter(FieldConstant.GROUP_ID);
        final String appName = (String) context.getWhereParameter(FieldConstant.APP_NAME);
        final String content = (String) context.getWhereParameter(FieldConstant.CONTENT);
        final String[] tagArr = (String[]) context.getWhereParameter(FieldConstant.TAG_ARR);
        final String[] types = (String[]) context.getWhereParameter(FieldConstant.TYPE);

        String sql = "SELECT a.id,a.data_id,a.group_id,a.tenant_id,a.app_name,a.content,a.type "
                + "FROM config_info a LEFT JOIN config_tags_relation b ON a.id=b.id";

        List<Object> paramList = new ArrayList<>();

        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" a.tenant_id like ? ");
        paramList.add(tenant);
        if (StringUtils.isNotBlank(dataId)) {
            where.append(" AND a.data_id like ? ");
            paramList.add("%"+dataId+"%");
        }
        if (StringUtils.isNotBlank(group)) {
            where.append(" AND a.group_id like ? ");
            paramList.add("%"+group+"%");
        }
        if (StringUtils.isNotBlank(appName)) {
            where.append(" AND a.app_name = ? ");
            paramList.add(appName);
        }
        if (StringUtils.isNotBlank(content)) {
            where.append(" AND a.content like ? ");
            paramList.add("%"+content+"%");
        }
        if (!ArrayUtils.isEmpty(tagArr)) {
            where.append("AND b.tag_name IN (");
            for (int i = 0; i < tagArr.length; i++) {
                if (i > 0) {
                    where.append(", ");
                }
                where.append("?");
            }
            where.append(") ");
            paramList.addAll(Arrays.asList(tagArr));
        }
        if (!ArrayUtils.isEmpty(types)) {
            if (!ArrayUtils.isEmpty(tagArr)) {
                where.append("AND a.type IN (");
                for (int i = 0; i < tagArr.length; i++) {
                    if (i > 0) {
                        where.append(", ");
                    }
                    where.append("?");
                }
                where.append(") ");
                paramList.addAll(Arrays.asList(types));
            }
        }
        int offset = context.getStartRow();
        int limit = context.getPageSize();
        paramList.add(limit);
        paramList.add(offset);
        return new MapperResult(sql + where + " LIMIT ? OFFSET ?",
                paramList);
    }

    @Override
    public MapperResult findAllConfigInfoFetchRows(MapperContext context) {
        String tenantId = (String) context.getWhereParameter("tenantId");
        String sql = "SELECT t.id, data_id, group_id, tenant_id, app_name, content, md5 " +
                "FROM (SELECT id FROM config_info WHERE tenant_id LIKE ? ORDER BY id OFFSET ? LIMIT ?) g, config_info t " +
                "WHERE g.id = t.id";
        return new MapperResult(sql,
                CollectionUtils.list(tenantId, context.getStartRow(), context.getPageSize()));
    }
}
