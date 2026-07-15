/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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

package com.alibaba.nacos.plugin.auth.impl.persistence.handler.support;

import com.alibaba.nacos.persistence.constants.PersistenceConstant;
import com.alibaba.nacos.plugin.auth.impl.constant.AuthPageConstant;
import com.alibaba.nacos.plugin.auth.impl.model.OffsetFetchResult;
import com.alibaba.nacos.plugin.auth.impl.persistence.handler.PageHandlerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PostgreSQL 分页处理器适配器实现类
 * <p>
 * 该类用于将分页查询参数（页码、每页数量）转换为 PostgreSQL 数据库支持的 SQL 语法格式
 * 通过实现 PageHandlerAdapter 接口，提供分页参数注入和 SQL 改造能力
 * </p>
 * @author huangKeMing
 * @see PageHandlerAdapter
 */
public class PostgresqlPageHandlerAdapter implements PageHandlerAdapter {

    /**
     * 判断是否支持当前数据源类型
     * @param dataSourceType 数据源类型标识
     * @return 如果是 PostgreSQL 数据源返回 true，否则返回 false
     * @see PersistenceConstant#POSTGRESQL
     */
    @Override
    public boolean supports(String dataSourceType) {
        return PersistenceConstant.POSTGRESQL.equals(dataSourceType);
    }

    /**
     * 为原始查询语句添加分页参数（OFFSET/FETCH NEXT）
     * <p>实现逻辑：
     * 1. 检查原始 SQL 是否已包含分页关键字
     * 2. 若未包含，则在末尾追加 PostgreSQL 分页语法
     * 3. 构建包含分页参数的新参数数组
     * </p>
     * @param fetchSql 原始查询语句
     * @param arg 原始参数数组
     * @param pageNo 当前页码（从1开始）
     * @param pageSize 每页显示数量
     * @return 包含分页参数的 OffsetFetchResult 对象
     * @throws IllegalArgumentException 当参数计算溢出时抛出异常
     * @see AuthPageConstant#OFFSET_ROWS
     * @see AuthPageConstant#FETCH_NEXT
     */
    @Override
    public OffsetFetchResult addOffsetAndFetchNext(String fetchSql, Object[] arg, int pageNo, int pageSize) {
        // 检测是否需要添加分页参数
        if (!fetchSql.contains(AuthPageConstant.OFFSET)) {
            // 构建 PostgreSQL 分页语法
            fetchSql += " " + AuthPageConstant.OFFSET_ROWS + " " + AuthPageConstant.FETCH_NEXT;

            // 计算偏移量（PostgreSQL 要求 OFFSET 从0开始）
            int offset = (pageNo - 1) * pageSize;

            // 创建新参数列表（保留原有参数 + 新增偏移量和每页数量）
            List<Object> newArgsList = new ArrayList<>(Arrays.asList(arg));
            newArgsList.add(offset);
            newArgsList.add(pageSize);

            // 转换为数组并返回结果对象
            return new OffsetFetchResult(fetchSql, newArgsList.toArray());
        }

        // 已包含分页参数时直接返回原参数
        return new OffsetFetchResult(fetchSql, arg);
    }

}
