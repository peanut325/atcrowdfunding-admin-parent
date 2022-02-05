package org.fall.mvc.handler;


import com.github.pagehelper.PageInfo;
import crowd.entity.Role;
import org.fall.service.api.RoleService;
import org.fall.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleHandler {

    @Autowired
    private RoleService roleService;

    // 拥有部长的角色才可以访问
    @PreAuthorize("hasAnyRole('部长')")
    @RequestMapping("/role/get/page/info.json")
    public ResultEntity<PageInfo<Role>> getPageInfo(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        // 1.调用service查询
        PageInfo<Role> roleByKeyword = roleService.getRoleByKeyword(keyword, pageNum, pageSize);
        // 2.封装到ResultEntity中,上面抛出异常是交给异常映射机制处理
        return ResultEntity.successWithData(roleByKeyword);
    }

    @RequestMapping("/role/save.json")
    public ResultEntity<String> saveRole(Role role) {
        roleService.saveRole(role);
        return ResultEntity.successWithoutData();
    }

    @RequestMapping("/role/update.json")
    public ResultEntity<String> updateRole(Role role) {
        roleService.updateRole(role);
        return ResultEntity.successWithoutData();
    }

    @RequestMapping("/role/remove.json")
    public ResultEntity<String> removeRole(@RequestBody List<Integer> roleIdList) {
        roleService.deleteRole(roleIdList);
        return ResultEntity.successWithoutData();
    }
}
