package org.fall.service.api;

import com.github.pagehelper.PageInfo;
import crowd.entity.Admin;

import java.util.List;

public interface AdminService {
    // 插入管理员数据
    void saveAdmin(Admin admin);
    // 返回所有管理员数据
    List<Admin> getAll();
    // 查询用户账号密码
    Admin getAdminByLoginAcct(String loginAcct, String loginPswd);
    // 按关键字查询数据，并返回分页对象
    PageInfo<Admin> getPageInfo(String keyword,Integer pageNum,Integer pageSize);
    // 按照adminId查询单个Admin对象
    Admin getAdminById(Integer adminId);
    // 删除单个数据
    void removeOne(Integer adminId);
    // 更新用户信息
    void updateAdmin(Admin admin);
    // 保存角色和用户关系
    void saveAdminRoleRelationship(Integer adminId, List<Integer> roleIdList);
    // 通过账户名获取admin
    Admin getAdminByLoginAcct(String username);
}
