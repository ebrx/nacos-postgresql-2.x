package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.mapper.HistoryConfigInfoMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/**
 * PostgreSQL数据库history_config_info表映射实现类
 * <p>
 * 该类继承自通用PostgreSQL映射基类，提供针对history_config_info表的：
 * <ul>
 *   <li>数据查询实现</li>
 *   <li>数据删除实现</li>
 * </ul>
 * </p>
 *
 * @author yfjd
 **/
public class HistoryConfigInfoMapperByPostgres extends AbstractMapperByPostgres implements HistoryConfigInfoMapper {

    @Override
    public MapperResult removeConfigHistory(MapperContext context) {
        String sql = "WITH to_delete AS (" +
                "SELECT id " +
                "FROM his_config_info " +
                "WHERE gmt_modified < ?  " +
                "ORDER BY gmt_modified LIMIT ?)" +
                "DELETE FROM " +
                "his_config_info " +
                "WHERE " +
                "id IN (SELECT id FROM to_delete)";
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter(FieldConstant.START_TIME),
                context.getWhereParameter(FieldConstant.LIMIT_SIZE)));
    }

    @Override
    public MapperResult pageFindConfigHistoryFetchRows(MapperContext context) {
        String sql = "SELECT nid,data_id,group_id,tenant_id,app_name,src_ip,src_user,op_type," +
                "ext_info,publish_type,gray_name,gmt_create,gmt_modified " +
                "FROM his_config_info " +
                "WHERE data_id = ? AND group_id = ? AND tenant_id = ? " +
                "ORDER BY nid DESC LIMIT ? OFFSET ?";
        return new MapperResult(sql,
                CollectionUtils.list(
                        context.getWhereParameter(FieldConstant.DATA_ID),
                        context.getWhereParameter(FieldConstant.GROUP_ID),
                        context.getWhereParameter(FieldConstant.TENANT_ID),
                        context.getPageSize(),
                        context.getStartRow()
                )
        );
    }
    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }
}
