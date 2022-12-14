package com.lilinxi.controller;

import com.lilinxi.service.SysGeneratorService;
import com.lilinxi.utils.PageUtils;
import com.lilinxi.utils.Query;
import com.lilinxi.utils.R;
import com.lilinxi.utils.LinXiException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author lilinxi lilinxi015@163.com
 */
@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {
    @Autowired
    private SysGeneratorService sysGeneratorService;



    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils pageUtil = sysGeneratorService.queryList(new Query(params));

        return R.ok().put("page", pageUtil);
    }



    /**
     * 生成工程
     */
    @RequestMapping("/codeProject")
    public void codeProject(String tables, HttpServletResponse response) throws IOException {
        /*配置信息*/
        Configuration config = getConfig();
        byte[] data = sysGeneratorService.generatorProject(tables.split(","));
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\""+config.getString("project")+".zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }

    /**
     * 生成模块
     */
    @RequestMapping("/codeModule")
    public void codeModule(String tables, HttpServletResponse response) throws IOException {
        /*配置信息*/
        Configuration config = getConfig();
        byte[] data = sysGeneratorService.generatorModule(tables.split(","));
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"package.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
    /**
     * 生成前端
     */
    @RequestMapping("/codeVue")
    public void codeVue(String tables, HttpServletResponse response) throws IOException {
        /*配置信息*/
        Configuration config = getConfig();
        byte[] data = sysGeneratorService.generatorVue(tables.split(","));
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"Vue.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
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
}
