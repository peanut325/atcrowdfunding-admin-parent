package org.fall.mvc.handler;

import crowd.entity.Auth;
import crowd.entity.Role;
import org.fall.service.api.AdminService;
import org.fall.service.api.AuthService;
import org.fall.service.api.RoleService;
import org.fall.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class AssignHandler {
    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthService authService;

    @RequestMapping("/assign/to/page.html")
    public String toAssignPage(
            @RequestParam("adminId") Integer adminId,
            ModelMap modelMap
    ) {
        // 查询已分配角色
        List<Role> assignRoleList = roleService.getAssignedRole(adminId);
        // 查询未分配的角色
        List<Role> unAssignRoleList = roleService.getUnAssignedRole(adminId);
        // 存入模型
        modelMap.addAttribute("assignRoleList", assignRoleList);
        modelMap.addAttribute("unAssignRoleList", unAssignRoleList);
        // 转发到assign-role
        return "assign-role";
    }

    @RequestMapping("/assign/do/role/assign.html")
    public String saveAdminRoleRelationship(
            @RequestParam(value = "adminId") Integer adminId,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("keyword") String keyword,
            // 管理员可以没有权限，所以可以设置roleId可以为空
            @RequestParam(value = "roleIdList", required = false) List<Integer> roleIdList,
            ModelMap modelMap
    ) {

        // 调用service层方法
        adminService.saveAdminRoleRelationship(adminId, roleIdList);
        return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
    }

    @ResponseBody
    @RequestMapping("/assign/get/all/auth.json")
    public ResultEntity<List<Auth>> getAllAuth() {
        List<Auth> authList = authService.getAllAuth();
        return ResultEntity.successWithData(authList);
    }

    @ResponseBody
    @RequestMapping("/assign/get/assigned/auth/id/by/role/id.json")
    public ResultEntity<List<Integer>> getAssignedAuthIdByRoleId(@RequestParam("roleId") Integer roleId) {
        List<Integer> authList = authService.getAssignedAuthIdByRoleId(roleId);
        return ResultEntity.successWithData(authList);
    }

    @ResponseBody
    @RequestMapping("/assign/do/role/assign/auth.json")
    public ResultEntity<String> saveRoleAuthRelathinship(@RequestBody Map<String, List<Integer>> map) {
        // 取出roleId进行删除
        List<Integer> roleIdList = map.get("roleId");
        Integer roleId = roleIdList.get(0);
        // 根据roleId进行删除
        authService.deleteOldRelationship(roleId);
        // 取出新增的权限的authId
        List<Integer> authIdList = map.get("authIdList");
        // 判断是否为空，再进行插入
        if (authIdList != null && authIdList.size() > 0) {
            authService.saveNewRelationship(roleId, authIdList);
        }
        return ResultEntity.successWithoutData();
    }
}
