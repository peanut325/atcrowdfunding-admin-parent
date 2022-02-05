package org.fall.mvc.handler;

import crowd.entity.Admin;
import crowd.entity.Student;
import org.fall.utils.CrowdUtils;
import org.fall.utils.ResultEntity;
import org.fall.service.api.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class TestHandler {

    @Autowired
    AdminService adminService;

    @RequestMapping("/test/ssm.html")
    public String testSSM(Model model, HttpServletRequest httpServletRequest) {
        boolean judgeRequestType = CrowdUtils.judgeRequestType(httpServletRequest);
        System.out.println(judgeRequestType);
        List<Admin> admins = adminService.getAll();
        model.addAttribute("admins", admins);
        return "target";
    }

    @RequestMapping("/send/array/one.html")
    @ResponseBody
    public String testAjaxOne(@RequestParam("array[]")List<Integer> array){
        for (Integer integer : array) {
            System.out.println(integer);
        }
        return "success";
    }

    // @RequestBody来获取请求体中的json数据
    @RequestMapping("/send/array/two.html")
    @ResponseBody
    public String testAjaxTwo(@RequestBody Integer[] array) {
        for (Integer integer : array) {
            System.out.println(integer);
        }
        return "success";
    }

    // @RequestBody来获取请求体中的复杂数据
    @RequestMapping("send/compose/object.html")
    @ResponseBody
    public String testAjaxObject(@RequestBody Student student){
        System.out.println(student.toString());
        return "success";
    }

    // 测试ResultEntity
    @RequestMapping("send/compose/object.json")
    @ResponseBody
    public ResultEntity<Student> testAjaxResultEntity(@RequestBody Student student, HttpServletRequest httpServletRequest) {
        boolean judgeRequestType = CrowdUtils.judgeRequestType(httpServletRequest);
        System.out.println(judgeRequestType);
        // 将查询到的数据通过ResultEntity返回
        return ResultEntity.successWithData(student);
    }
}