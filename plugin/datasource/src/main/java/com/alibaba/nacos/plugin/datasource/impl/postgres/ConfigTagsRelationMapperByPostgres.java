package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.ArrayUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigTagsRelationMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  The mysql implementation of ConfigInfoMapper.
 *
 *  @author fuhouyu
 */
public class ConfigTagsRelationMapperByPostgres extends AbstractMapperByPostgres implements ConfigTagsRelationMapper {

    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    @Override
    public MapperResult findConfigInfo4PageFetchRows(MapperContext context) {
        final String tenant = (String) context.getWhereParameter(FieldConstant.TENANT_ID);
        final String dataId = (String) context.getWhereParameter(FieldConstant.DATA_ID);
        final String group = (String) context.getWhereParameter(FieldConstant.GROUP_ID);
        final String appName = (String) context.getWhereParameter(FieldConstant.APP_NAME);
        final String content = (String) context.getWhereParameter(FieldConstant.CONTENT);
        final String[] tagArr = (String[]) context.getWhereParameter(FieldConstant.TAG_ARR);

        List<Object> paramList = new ArrayList<>();
        paramList.add(tenant);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT a.id, a.data_id, a.group_id, a.tenant_id, a.app_name, a.content ");
        sqlBuilder.append("FROM config_info a ");
        sqlBuilder.append("LEFT JOIN config_tags_relation b ON a.id = b.id ");
        sqlBuilder.append("WHERE a.tenant_id = ? ");

        if (StringUtils.isNotBlank(dataId)) {
            sqlBuilder.append("AND a.data_id = ?");
            paramList.add(dataId);
        }
        if (StringUtils.isNotBlank(group)) {
            sqlBuilder.append("AND a.group_id = ?");
            paramList.add(group);
        }
        if (StringUtils.isNotBlank(appName)) {
            sqlBuilder.append("AND a.app_name = ?");
            paramList.add(appName);
        }
        if (StringUtils.isNotBlank(content)) {
            sqlBuilder.append("AND a.content LIKE ?");
            paramList.add("%" + content + "%");
        }
        if (!ArrayUtils.isEmpty(tagArr)) {
            List<String> filteredTags = Arrays.stream(tagArr)
                    .filter(tag -> tag != null && !tag.trim().isEmpty())
                    .collect(Collectors.toList());
            if (!filteredTags.isEmpty()) {
                sqlBuilder.append("AND b.tag_name IN (");
                for (int i = 0; i < filteredTags.size(); i++) {
                    if (i > 0) {
                        sqlBuilder.append(", ");
                    }
                    sqlBuilder.append("?");
                }
                sqlBuilder.append(") ");
                paramList.addAll(filteredTags);
            }
        }
        // 分页处理
        sqlBuilder.append("LIMIT ? OFFSET ?");
        paramList.add(context.getPageSize());
        paramList.add(context.getStartRow());

        String sql = sqlBuilder.toString();
        return new MapperResult(sql, paramList);
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

        List<Object> paramList = new ArrayList<>();
        paramList.add(tenant);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT a.id,a.data_id,a.group_id,a.tenant_id,a.app_name,a.content,a.type ");
        sqlBuilder.append("FROM config_info a ");
        sqlBuilder.append("LEFT JOIN config_tags_relation b ON a.id = b.id ");
        sqlBuilder.append("WHERE a.tenant_id = ? ");
        if (StringUtils.isNotBlank(dataId)) {
            sqlBuilder.append("AND a.data_id LIKE ?");
            paramList.add("%" + dataId + "%");
        }
        if (StringUtils.isNotBlank(group)) {
            sqlBuilder.append("AND a.group LIKE ?");
            paramList.add("%" + group + "%");
        }
        if (StringUtils.isNotBlank(appName)) {
            sqlBuilder.append("AND a.app_name = ?");
            paramList.add(appName);
        }

        if (StringUtils.isNotBlank(content)) {
            sqlBuilder.append("AND a.content LIKE ?");
            paramList.add("%" + content + "%");
        }
        if (!ArrayUtils.isEmpty(tagArr) && tagArr.length > 0) {
                sqlBuilder.append("AND b.tag_name IN (");
                for (int i = 0; i < tagArr.length; i++) {
                    if (i > 0) {
                        sqlBuilder.append(", ");
                    }
                    sqlBuilder.append("?");
                }
                sqlBuilder.append(") ");

                for (String tagName : tagArr) {
                    paramList.add("%" + tagName + "%");
                }
        }

        if (!ArrayUtils.isEmpty(types)) {
            sqlBuilder.append("AND a.type IN (");
            for (int i = 0; i < types.length; i++) {
                if (i > 0) {
                    sqlBuilder.append(", ");
                }
                sqlBuilder.append("?");
            }
            sqlBuilder.append(") ");
            for (String type : types) {
                paramList.add("%" + type + "%");
            }
        }
        sqlBuilder.append("LIMIT ? OFFSET ?");
        paramList.add(context.getPageSize());
        paramList.add(context.getStartRow());

        String sql = sqlBuilder.toString();
        return new MapperResult(sql,paramList);
    }
}
