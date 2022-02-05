package org.fall.mvc.config;

import org.fall.constant.CrowdConstant;
import org.fall.service.api.CrowdUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration                                     // 配置类
@EnableWebSecurity                                 // 开启web环境下的权限控制功能
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启此功能才可以使用注解来设置权限信息
public class WebAppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder BCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder
                .userDetailsService(userDetailsService)    // 使用数据库登录
                .passwordEncoder(BCryptPasswordEncoder()); // 使用盐值加密
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security
                .authorizeRequests()
                .antMatchers("/admin/to/login/page.html")   // 对登录页放行
                .permitAll()                                            //无条件访问
                .antMatchers(                                           // 对静态资源放行
                        "/bootstrap/**"
                        , "/crowd/**"
                        , "/css/**"
                        , "/fonts/**"
                        , "/img/**"
                        , "/jquery/**"
                        , "/layer/**"
                        , "/script/**"
                        , "/ztree/**")
                .permitAll()
                .antMatchers("/admin/get/page.html")        // 针对分页显示Admin数据设定访问控制
                .access("hasAnyRole('经理') OR hasAuthority('user:get') ") // 要求具备经理角色和user:get权限
                .anyRequest()                                          // 其他未设置的全部请求
                .authenticated()                                       // 需要认证
                .and()
                .formLogin()                                            // 开启表单登录
                .loginPage("/admin/to/login/page.html")                 // 登录页
                .loginProcessingUrl("/admin/security/login.html")       // 登录请求的地址
                .defaultSuccessUrl("/admin/to/main/page.html")          // 登录成功后前往的地址
                .usernameParameter("loginAcct")                         // 账号请求参数的名称
                .passwordParameter("loginPswd")                         // 密码请求参数的名称
                .and()
                .logout()                                               // 开启登录退出功能
                .logoutUrl("/admin/security/logout.html")                // 退出登录请求的地址
                .logoutSuccessUrl("/admin/to/login/page.html")          // 退出成功后跳转的页面
                .and()
                .csrf()                                                 // 为了方便，本项目禁用跨站请求伪造功能
                .disable()
                .exceptionHandling()                                    // 配置403异常处理机制
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        request.setAttribute("exception",new Exception(CrowdConstant.MESSAGE_ACCESS_DENIED));
                        request.getRequestDispatcher("/WEB-INF/system-error.jsp").forward(request,response);
                    }
                });
    }
}
