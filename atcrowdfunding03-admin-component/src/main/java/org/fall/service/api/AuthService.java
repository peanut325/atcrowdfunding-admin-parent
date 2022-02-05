package org.fall.service.api;

import crowd.entity.Auth;

import java.util.List;

public interface AuthService {
    // 获取所有的Auth
    List<Auth> getAllAuth();
    // 根据角色名获取Auth
    List<Integer> getAssignedAuthIdByRoleId(Integer roleId);
    // 根据roleId删除所有权限
    void deleteOldRelationship(Integer roleId);
    // 将权限根据roleId加入
    void saveNewRelationship(Integer roleId, List<Integer> authIdList);
    // 根据adminId获取Auth
    List<String> getAssignAuthByAdminId(Integer adminId);
}
