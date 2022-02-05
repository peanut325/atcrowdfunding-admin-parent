package org.fall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import crowd.entity.Admin;
import crowd.entity.AdminExample;
import org.fall.constant.CrowdConstant;
import org.fall.exception.LoginAcctAlreadyInUseException;
import org.fall.exception.LoginAcctAlreadyUpdateException;
import org.fall.exception.LoginFailedException;
import org.fall.mapper.AdminMapper;
import org.fall.service.api.AdminService;
import org.fall.utils.CrowdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void saveAdmin(Admin admin) {
        // 1.取出密码进行md5加密
        String password=admin.getUserPswd();
        // 使用盐值加密代替md5加密
        // String passwordMd5 = CrowdUtils.md5(password);
        String encode = bCryptPasswordEncoder.encode(password);
        admin.setUserPswd(encode);
        // 2.设置新增时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = simpleDateFormat.format(date);
        admin.setCreateTime(createTime);
        // 3.添加数据
        try {
            adminMapper.insert(admin);
        } catch (Exception exception) {
            if(exception instanceof DuplicateKeyException){
                throw new LoginAcctAlreadyInUseException(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
            }
        }
    }

    @Override
    public List<Admin> getAll() {
        return adminMapper.selectByExample(new AdminExample());
    }

    @Override
    public Admin getAdminByLoginAcct(String loginAcct, String loginPswd) {
        // 1.根据登录账号查询Admin对象
        // 创建Example对象
        AdminExample adminExample = new AdminExample();
        // 创建Criteria对象
        AdminExample.Criteria criteria = adminExample.createCriteria();
        // 封装查找的条件
        criteria.andLoginAcctEqualTo(loginAcct);
        // 调用adminMapper进行查找
        List<Admin> admins = adminMapper.selectByExample(adminExample);
        // 2.判断Admin是否为空
        if (admins == null && admins.size() == 0) {
            // 3.Admin对象为空则抛出异常
            throw new LoginFailedException(CrowdConstant.MESSAGE_LOGIN_FAILED);
        }
        // 是否出现多条数据
        if (admins.size() > 1) {
            throw new LoginFailedException(CrowdConstant.MESSAGE_SYSTEM_ERROR_LOGIN_NOT_UNIQUE);
        }
        // 4.Admin对象不为空则取出Admin对象
        Admin admin = admins.get(0);
        // 5.为空抛出异常，不为空取出密码
        if (admin == null) {
            throw new LoginFailedException(CrowdConstant.MESSAGE_LOGIN_FAILED);
        }
        String userPswdDb = admin.getUserPswd();
        // 5.将表单提交的数据进行明文加密
        String userPswdForm = CrowdUtils.md5(loginPswd);
        // 6.对密码进行比较
        if (!Objects.equals(userPswdDb, userPswdForm)) {
            // 7.不相等抛出异常
            throw new LoginFailedException(CrowdConstant.MESSAGE_LOGIN_FAILED);
        } else {
            // 8.相等则返回Admin对象
            return admin;
        }
    }

    @Override
    public PageInfo<Admin> getPageInfo(String keyword, Integer pageNum, Integer pageSize) {
        // 1.开启pageHelper功能
        // 体现了pageHelper的”非侵入设计“，原本要做的查询不必有任何修改
        PageHelper.startPage(pageNum, pageSize);
        // 2.按照关键字进行查询
        List<Admin> admins = adminMapper.selectAdminByKeyword(keyword);
        // 3.返回封装的pageInfo对象
        return new PageInfo(admins);
    }

    @Override
    public Admin getAdminById(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }

    @Override
    public void removeOne(Integer adminId) {
        adminMapper.deleteByPrimaryKey(adminId);
    }

    @Override
    public void updateAdmin(Admin admin) {
        try {
            // 有选择的更新
            adminMapper.updateByPrimaryKeySelective(admin);
        } catch (Exception exception) {
            // 抛出更新时，用户名相同异常
            throw new LoginAcctAlreadyUpdateException(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
        }
    }

    @Override
    public void saveAdminRoleRelationship(Integer adminId, List<Integer> roleIdList) {
        // 为了简化操作：先将adminId的角色全部删除
        adminMapper.deleteOldRelationship(adminId);
        // 根据roleIdList和adminId保存新的关系
        if(roleIdList!=null&&roleIdList.size()>=0){
            adminMapper.saveNewRelationship(adminId,roleIdList);
        }
    }

    @Override
    public Admin getAdminByLoginAcct(String username) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andLoginAcctLike(username);
        List<Admin> admins = adminMapper.selectByExample(adminExample);
        Admin admin = admins.get(0);
        return admin;
    }
}
