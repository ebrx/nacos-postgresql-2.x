package com.alibaba.nacos.plugin.datasource.enums.postgres;

import java.util.HashMap;
import java.util.Map;

/**
 * PostgreSQL内置函数映射枚举
 * <p>
 * 该枚举类用于维护MySQL与PostgreSQL函数名称的映射关系，
 * 提供函数别名与实际函数名的双向转换能力
 * </p>
 *
 * <p>示例用法：</p>
 * <pre>
 * String pgFunc = TrustedPostgresFunctionEnum.NOW.getFunction(); // 获取实际函数名
 * String alias = TrustedPostgresFunctionEnum.getFunctionByName("CURRENT_TIMESTAMP"); // 通过实际函数名获取别名
 * </pre>
 *
 * @author fuhouyu
 * @version 1.1.0
 * @since 2025-04-11
 */
public enum TrustedPostgresFunctionEnum {

    /**
     * NOW()函数映射配置
     * <p>
     * 映射关系：
     * - MySQL别名：NOW()
     * - PostgreSQL实际函数：CURRENT_TIMESTAMP
     * </p>
     */
    NOW("NOW()", "CURRENT_TIMESTAMP");

    private static final Map<String, TrustedPostgresFunctionEnum> LOOKUP_MAP = new HashMap<>();

    static {
        // 初始化函数映射表
        for (TrustedPostgresFunctionEnum entry : TrustedPostgresFunctionEnum.values()) {
            LOOKUP_MAP.put(entry.functionName, entry);
        }
    }

    /** 函数别名（MySQL兼容名称） */
    private final String functionName;

    /** 实际执行的PostgreSQL函数名 */
    private final String function;

    /**
     * 构造函数
     * @param functionName 函数别名（MySQL兼容名称）
     * @param function 实际PostgreSQL函数名
     */
    TrustedPostgresFunctionEnum(String functionName, String function) {
        this.functionName = functionName;
        this.function = function;
    }

    /**
     * 根据函数别名获取实际PostgreSQL函数名
     * @param functionName 函数别名（如"NOW()"）
     * @return 对应的PostgreSQL函数名（如"CURRENT_TIMESTAMP"）
     * @throws IllegalArgumentException 当函数别名不存在时抛出异常
     * @see #getFunction()
     */
    public static String getFunctionByName(String functionName) {
        TrustedPostgresFunctionEnum entry = LOOKUP_MAP.get(functionName);
        if (entry != null) {
            return entry.function;
        }
        throw new IllegalArgumentException(String.format("Unsupported function alias: %s", functionName));
    }

    /**
     * 获取函数别名
     * @return MySQL兼容的函数别名
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * 获取实际PostgreSQL函数名
     * @return 数据库实际执行的函数名
     */
    public String getFunction() {
        return function;
    }
}