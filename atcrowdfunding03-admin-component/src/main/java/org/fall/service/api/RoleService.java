package org.fall.service.api;

import com.github.pagehelper.PageInfo;
import crowd.entity.Role;

import java.util.List;

public interface RoleService {

    // 根据关键字查询Role
    PageInfo<Role> getRoleByKeyword(String keyword,Integer pageNum,Integer pageSize);
    // 保存角色信息
    void saveRole(Role role);
    // 更新角色信息
    void updateRole(Role role);
    // 删除角色信息
    void deleteRole(List<Integer> roleIdList);
    // 查询已分配角色
    List<Role> getAssignedRole(Integer adminId);
    // 查询未分配角色
    List<Role> getUnAssignedRole(Integer adminId);
}
