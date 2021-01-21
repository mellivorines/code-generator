package com.lilinxi.dao;

import java.util.List;
import java.util.Map;

/**
 * 数据库接口
 *
 * @author lilinxi lilinxi015@163.com
 */
public interface GeneratorDao {
    /**
     * 查询列表
     * @param map map集合
     * @return 返回结果
     */
    List<Map<String, Object>> queryList(Map<String, Object> map);

    /**
     * 查询表名
     * @param tableName 表名
     * @return 返回结果
     */
    Map<String, String> queryTable(String tableName);

    /**
     *查询表字段
     * @param tableName 表名
     * @return 返回结果
     */
    List<Map<String, String>> queryColumns(String tableName);
}
