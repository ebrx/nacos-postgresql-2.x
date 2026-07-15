package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoBetaMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL数据库ConfigInfoBeta表映射实现类
 * <p>
 * 该类继承自通用PostgreSQL映射基类，提供针对config_info_beta表的：
 * <ul>
 *   <li>分页查询实现</li>
 *   <li>数据表字段映射</li>
 *   <li>SQL语法适配</li>
 * </ul>
 * </p>
 *
 * <p>核心功能示例：</p>
 * <pre>
 * // 获取分页数据
 * MapperResult result = mapper.findAllConfigInfoBetaForDumpAllFetchRows(context);
 * </pre>
 *
 * @author fuhouyu
 * @version 1.1.0
 * @see AbstractMapperByPostgres
 * @since 2.5.1
 */
public class ConfigInfoBetaMapperByPostgres extends AbstractMapperByPostgres implements ConfigInfoBetaMapper {

    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    /**
     * 分页查询配置信息Beta表数据
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>构建分页查询SQL（使用OFFSET/LIMIT语法）</li>
     *   <li>通过子查询实现游标分页</li>
     *   <li>参数化查询防止SQL注入</li>
     * </ol>
     * </p>
     *
     * @param context 查询上下文对象，包含分页参数
     * @return 包含SQL语句和参数的查询结果对象
     * @see MapperContext
     * @see MapperResult
     */
    @Override
    public MapperResult findAllConfigInfoBetaForDumpAllFetchRows(MapperContext context) {
        int startRow = context.getStartRow();
        int pageSize = context.getPageSize();
        String sql = " SELECT t.id, data_id, group_id, tenant_id, app_name, content, md5, gmt_modified, beta_ips, encrypted_data_key" +
                "  FROM ( SELECT id FROM config_info_beta  ORDER BY id OFFSET " + startRow + " LIMIT " + pageSize + " )  g, " +
                "config_info_beta t WHERE g.id = t.id ";
        List<Object> paramList = new ArrayList<>(2);
        paramList.add(startRow);
        paramList.add(pageSize);
        return new MapperResult(sql, paramList);
    }
}
