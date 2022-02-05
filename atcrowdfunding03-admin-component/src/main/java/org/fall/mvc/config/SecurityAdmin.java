package org.fall.mvc.config;

import crowd.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * 为了能方便地获取到原始地Admin对象，因此创建一个SecurityAdmin类，继承User。
 */
public class SecurityAdmin extends User {

    // 原始的Admin对象，包含Admin的所有属性
    private Admin originalAdmin;

    public SecurityAdmin(
            // 传入原始的admin
            Admin originalAdmin,
            // 创建角色，权限的集合
            List<GrantedAuthority> authorities) {
        // 调用父类的构造器
        super(originalAdmin.getLoginAcct(), originalAdmin.getUserPswd(), authorities);

        this.originalAdmin = originalAdmin;

        // 擦除originalAdmin中的密码，即将originalAdmin设置为空
        originalAdmin.setUserPswd(null);
    }

    // 对外提供获取原始的Admin对象的get方法
    public Admin getOriginalAdmin() {
        return originalAdmin;
    }
}
