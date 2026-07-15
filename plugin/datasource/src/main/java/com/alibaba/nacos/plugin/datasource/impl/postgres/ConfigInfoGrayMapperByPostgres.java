/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.impl.mysql.AbstractMapperByMysql;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoGrayMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.Collections;

/**
 * PostgreSQL数据库ConfigInfoBeta表映射实现类
 * <p>
 * 该类继承自通用PostgreSQL映射基类，提供针对config_info_gray表的：
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
 * MapperResult result = mapper.findAllConfigInfoGrayForDumpAllFetchRows(context);
 * </pre>
 *
 * @author yfjd
 * @version 1.1.0
 * @see AbstractMapperByPostgres
 * @since 2.5.1
 */

public class ConfigInfoGrayMapperByPostgres extends AbstractMapperByMysql implements ConfigInfoGrayMapper {
    
    @Override
    public MapperResult findAllConfigInfoGrayForDumpAllFetchRows(MapperContext context) {
        String sql = " SELECT id,data_id,group_id,tenant_id,gray_name,gray_rule,app_name,content,md5,gmt_modified "
                + " FROM  config_info_gray  ORDER BY id OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize();
        return new MapperResult(sql, Collections.emptyList());
    }
    
    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }
}
