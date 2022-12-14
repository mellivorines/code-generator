package com.lilinxi.utils;

import com.lilinxi.entity.ColumnEntity;
import com.lilinxi.entity.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author lilinxi lilinxi015@163.com
 */
public class GenProjectUtils {

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        /*module*/
        templates.add("template/module/DTO.java.vm");
        templates.add("template/module/Entity.java.vm");
        templates.add("template/module/Mapper.java.vm");
        templates.add("template/module/Mapper.xml.vm");
        templates.add("template/module/Service.java.vm");
        templates.add("template/module/ServiceImpl.java.vm");
        templates.add("template/module/Controller.java.vm");
        /*common*/
//        templates.add("template/project/common/BaseEntity.java.vm");
        templates.add("template/project/common/validator/AssertUtils.java.vm");
        templates.add("template/project/common/validator/ValidatorUtils.java.vm");
        templates.add("template/project/common/validator/group/AddGroup.java.vm");
        templates.add("template/project/common/validator/group/DefaultGroup.java.vm");
        templates.add("template/project/common/validator/group/DeleteGroup.java.vm");
        templates.add("template/project/common/validator/group/UpdateGroup.java.vm");
        /*exception*/
        templates.add("template/project/exception/CommonException.java.vm");
        /*service*/
//        templates.add("template/project/service/AbstractBaseServiceImpl.java.vm");
//        templates.add("template/project/service/IBaseService.java.vm");
        /*utils*/
        templates.add("template/project/utils/ExceptionUtils.java.vm");
        templates.add("template/project/utils/MessageUtils.java.vm");
        templates.add("template/project/utils/R.java.vm");
        templates.add("template/project/utils/PageQuery.java.vm");
        templates.add("template/project/utils/PageResult.java.vm");
        templates.add("template/project/utils/StringUtils.java.vm");
        templates.add("template/project/utils/SpringContextUtils.java.vm");
        templates.add("template/project/utils/ShortUUIDUtil.java.vm");
        templates.add("template/project/utils/HttpContextUtils.java.vm");
        templates.add("template/project/utils/BeanCopyUtils.java.vm");
        /*config*/
        templates.add("template/project/config/RedisCacheConfig.java.vm");
        templates.add("template/project/config/MybatisPlusConfig.java.vm");
        templates.add("template/project/config/SwaggerConfig.java.vm");

        templates.add("template/project/application.yml.vm");
        templates.add("template/project/bootstrap.yml.vm");
        templates.add("template/project/Application.java.vm");
        templates.add("template/project/ApplicationTest.java.vm");
        templates.add("template/project/pom.xml.vm");
        return templates;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(Map<String, String> table,
                                     List<Map<String, String>> columns, ZipOutputStream zip) {
        /*配置信息*/
        Configuration config = getConfig();
        boolean hasBigDecimal = false;
        boolean hasTime = false;
        /*表信息*/
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        /*表名转换成Java类名*/
        String className = tableToJava(tableEntity.getTableName(), config.getString("tablePrefix"));
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        /*列信息*/
        List<ColumnEntity> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));

            /*列名转换成Java属性名*/
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            /*列的数据类型，转换成Java类型*/
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && attrType.equals("BigDecimal")) {
                hasBigDecimal = true;
            }
            if (!hasTime && attrType.equals("Time")) {
                hasTime = true;
            }
            /*是否主键*/
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columsList.add(columnEntity);
        }
        tableEntity.setColumns(columsList);

        /*没主键，则第一个字段为主键*/
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        /*设置velocity资源加载器*/
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        /*封装模板数据*/
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("hasTime", hasTime);
        map.put("version", config.getString("version"));
        map.put("package", config.getString("package"));
        map.put("moduleName", StringUtils.isBlank(config.getString("moduleName")) ? tableEntity.getClassname().toLowerCase() : config.getString("moduleName"));
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        map.put("date", DateUtils.format(new Date(), DateUtils.DATE_PATTERN));

        for (int i = 0; i <= 10; i++) {
            map.put("id" + i, IdWorker.getId());
        }

        VelocityContext context = new VelocityContext(map);

        /*获取模板列表*/
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                /*添加到zip*/
                zip.putNextEntry(new ZipEntry(getFileName(template, tableEntity.getClassName(), config.getString("package"),config.getString("project"), (String) map.get("moduleName"))));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new LinXiException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isBlank(tablePrefix)&&(tableName.contains("_"))) {
            tablePrefix = tableName.substring(0, tableName.indexOf("_"));
        }
        tableName = tableName.replaceFirst(tablePrefix, "");
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new LinXiException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName,String project, String moduleName) {
        System.out.println(template);
        String packagePath = project + File.separator +"src" + File.separator +"main" + File.separator + "java" + File.separator;
        String utils = packagePath;
        String config = packagePath;
        String common = packagePath;
        String service = packagePath;
        String exception = packagePath;
        String resources = project + File.separator +"src" + File.separator +"main" + File.separator+"resources"+ File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + "modules" + File.separator + moduleName + File.separator;
            utils += packageName.replace(".", File.separator) + File.separator + "utils" ;
            config += packageName.replace(".", File.separator) + File.separator + "config" ;
            common += packageName.replace(".", File.separator) + File.separator + "common" ;
            service += packageName.replace(".", File.separator) + File.separator + "service" ;
            exception += packageName.replace(".", File.separator) + File.separator + "exception" ;
        }
        //module
        /*实体类*/
        if (template.contains("DTO.java.vm")) {
            return packagePath + "dto" + File.separator + className + "DTO.java";
        }
        if (template.contains("Entity.java.vm")) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }
        /*Mapper*/
        if (template.contains("Mapper.java.vm")) {
            return packagePath + "dao" + File.separator + className + "Mapper.java";
        }
        /*业务逻辑接口*/
        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }
        /*业务逻辑接口实现*/
        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }
        /*业controller接口*/
        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        //common
        if (template.contains("BaseEntity.java.vm")) {
            return common + File.separator +  "BaseEntity.java";
        }
        if (template.contains("AssertUtils.java.vm")) {
            return common + File.separator + "validator" + File.separator+"AssertUtils.java";
        }
        if (template.contains("ValidatorUtils.java.vm")) {
            return common + File.separator + "validator" + File.separator+  "ValidatorUtils.java";
        }
        if (template.contains("AddGroup.java.vm")) {
            return common + File.separator + "validator" + File.separator+"group" + File.separator+ "AddGroup.java";
        }
        if (template.contains("DefaultGroup.java.vm")) {
            return common + File.separator + "validator" + File.separator+"group" + File.separator+ "DefaultGroup.java";
        }
        if (template.contains("DeleteGroup.java.vm")) {
            return common + File.separator + "validator" + File.separator+"group" + File.separator+ "DeleteGroup.java";
        }
        if (template.contains("UpdateGroup.java.vm")) {
            return common + File.separator + "validator" + File.separator+"group" + File.separator+ "UpdateGroup.java";
        }

        //exception
        if (template.contains("CommonException.java.vm")) {
            return exception + File.separator +  "CommonException.java";
        }

        //service
        if (template.contains("AbstractBaseServiceImpl.java.vm")) {
            return service + File.separator +  "AbstractBaseServiceImpl.java";
        }
        if (template.contains("IBaseService.java.vm")) {
            return service + File.separator +  "IBaseService.java";
        }

        //utils
        if (template.contains("ExceptionUtils.java.vm")) {
            return utils + File.separator +  "ExceptionUtils.java";
        }
        if (template.contains("R.java.vm")) {
            return utils + File.separator +  "R.java";
        }
        if (template.contains("MessageUtils.java.vm")) {
            return utils + File.separator +  "MessageUtils.java";
        }
        if (template.contains("PageQuery.java.vm")) {
            return utils + File.separator +  "PageQuery.java";
        }
        if (template.contains("PageResult.java.vm")) {
            return utils + File.separator +  "PageResult.java";
        }
        if (template.contains("StringUtils.java.vm")) {
            return utils + File.separator +  "StringUtils.java";
        }
        if (template.contains("SpringContextUtils.java.vm")) {
            return utils + File.separator +  "SpringContextUtils.java";
        }
        if (template.contains("ShortUUIDUtil.java.vm")) {
            return utils + File.separator +  "ShortUUIDUtil.java";
        }
        if (template.contains("HttpContextUtils.java.vm")) {
            return utils + File.separator +  "HttpContextUtils.java";
        }
        if (template.contains("BeanCopyUtils.java.vm")) {
            return utils + File.separator +  "BeanCopyUtils.java";
        }

        //config
        if (template.contains("RedisCacheConfig.java.vm")) {
            return config + File.separator +  "RedisCacheConfig.java";
        }
        if (template.contains("MybatisPlusConfig.java.vm")) {
            return config + File.separator +  "MybatisPlusConfig.java";
        }
        if (template.contains("SwaggerConfig.java.vm")) {
            return config + File.separator +  "SwaggerConfig.java";
        }

        /*resources的mapper*/
        if (template.contains("Mapper.xml.vm")) {
            return resources+ "mapper" + File.separator + moduleName + File.separator + className + "Mapper.xml";
        }
        /*application.yml配置文件*/
        if (template.contains("application.yml.vm")) {
            return resources+ "application.yml";
        }
        /*bootstrap.yml配置文件*/
        if (template.contains("bootstrap.yml.vm")) {
            return resources+ "bootstrap.yml";
        }

        /*启动类*/
        if (template.contains("Application.java.vm")) {
            return project + File.separator +"src" + File.separator +"main" + File.separator + "java" + File.separator+packageName.replace(".", File.separator) + File.separator +  "Application.java";
        }
        /*测试类*/
        if (template.contains("ApplicationTest.java.vm")) {
            return project + File.separator +"src" + File.separator +"test" + File.separator + "java" + File.separator+packageName.replace(".", File.separator) + File.separator +  "ApplicationTest.java";
        }

        /*工程的pom配置文件*/
        if (template.contains("pom.xml.vm")) {
            return project + File.separator +  "pom.xml";
        }
        return null;
    }
}
