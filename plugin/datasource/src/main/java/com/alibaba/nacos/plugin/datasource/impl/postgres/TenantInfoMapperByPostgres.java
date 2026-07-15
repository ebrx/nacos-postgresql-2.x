package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.TenantInfoMapper;

/**
 *  The mysql implementation of ConfigInfoMapper.
 *
 *  @author fuhouyu
 */
public class TenantInfoMapperByPostgres extends AbstractMapperByPostgres implements TenantInfoMapper {
    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }
}
