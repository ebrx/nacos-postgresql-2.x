package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.enums.postgres.TrustedPostgresFunctionEnum;
import com.alibaba.nacos.plugin.datasource.mapper.AbstractMapper;

/**
 * PostgreSQL数据库抽象映射类
 * <p>
 * 该类继承自通用数据库映射基类，提供针对PostgreSQL数据库的：
 * <ul>
 *   <li>CRUD操作方法实现</li>
 *   <li>函数名自动转换功能</li>
 *   <li>SQL语法适配层</li>
 * </ul>
 * </p>
 *
 * <p>核心功能示例：</p>
 * <pre>
 * // 获取函数映射
 * String pgFunc = mapper.getFunction("NOW()"); // 返回CURRENT_TIMESTAMP
 * </pre>
 *
 * @author zyj
 * @version 1.1.0
 * @see AbstractMapper
 * @since 2.5.1
 */
public abstract class AbstractMapperByPostgres extends AbstractMapper {

    /**
     * 获取数据库函数映射名称
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>校验函数名合法性</li>
     *   <li>通过枚举映射获取Postgresql函数名</li>
     *   <li>处理函数不存在场景</li>
     * </ol>
     * </p>
     *
     * @param functionName 原始函数名（如"NOW()"）
     * @return 对应的Postgresql函数名（如"CURRENT_TIMESTAMP"）
     * @throws IllegalArgumentException 当函数名不存在时抛出
     * @throws NullPointerException 当参数为null时抛出
     * @see TrustedPostgresFunctionEnum#getFunctionByName(String)
     */
    @Override
    public String getFunction(String functionName) {
        if (functionName == null || functionName.trim().isEmpty()) {
            throw new NullPointerException("Function name cannot be null or empty");
        }
        return TrustedPostgresFunctionEnum.getFunctionByName(functionName);
    }
}