package com.lilinxi.entity.mongo;


import com.lilinxi.entity.TableEntity;

import java.util.List;
import java.util.Map;

/**
 * mysql一张表只需要一个表信息和列名信息
 * 但是mongo一张表可能需要多个实体类  所以单独用一个bean封装
 *
 * @author lilinxi lilinxi015@163.com
 */
public class MongoGeneratorEntity {
    /***表信息**/
    private Map<String, String> tableInfo;
    /***主类的列名信息**/
    private List<Map<String, String>> columns;


    public TableEntity toTableEntity() {
        TableEntity tableEntity = new TableEntity();
        Map<String, String> tableInfo = this.tableInfo;
        tableEntity.setTableName(tableInfo.get("tableName"));
        tableEntity.setComments("");
        return tableEntity;
    }


    public Map<String, String> getTableInfo() {
        return tableInfo;
    }

    public MongoGeneratorEntity setTableInfo(Map<String, String> tableInfo) {
        this.tableInfo = tableInfo;
        return this;
    }

    public List<Map<String, String>> getColumns() {
        return columns;
    }

    public MongoGeneratorEntity setColumns(List<Map<String, String>> columns) {
        this.columns = columns;
        return this;
    }

}
