package org.fall.service.impl;

import crowd.entity.Auth;
import crowd.entity.AuthExample;
import org.fall.mapper.AuthMapper;
import org.fall.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;


    @Override
    public List<Auth> getAllAuth() {
        return authMapper.selectByExample(new AuthExample());
    }

    @Override
    public List<Integer> getAssignedAuthIdByRoleId(Integer roleId) {
        return authMapper.selectAuthIdByRoleId(roleId);
    }

    @Override
    public void deleteOldRelationship(Integer roleId) {
        authMapper.deleteOldRelationship(roleId);
    }

    @Override
    public void saveNewRelationship(Integer roleId, List<Integer> authIdList) {
        authMapper.insertNewRelationship(roleId,authIdList);
    }

    @Override
    public List<String> getAssignAuthByAdminId(Integer adminId) {
        return authMapper.selectAssignAuthByAdminId(adminId);
    }
}
