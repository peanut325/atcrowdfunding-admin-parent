package org.fall.mvc.handler;

import com.github.pagehelper.PageInfo;
import crowd.entity.Admin;
import org.fall.constant.CrowdConstant;
import org.fall.service.api.AdminService;
import org.fall.utils.CrowdUtils;
import org.fall.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class AdminHandler {

    @Autowired
    private AdminService adminService;

    /**
     * 处理登录请求
     *
     * @param loginAcct 账号
     * @param loginPswd 密码
     * @param session   存入session域
     * @return 重定向到主页面
     */
    @RequestMapping("/admin/do/login.html")
    public String doLogin(
            @RequestParam("loginAcct") String loginAcct,
            @RequestParam("loginPswd") String loginPswd,
            HttpSession session
    ) {
        // 调用Service方法检查登录
        // 这个方法如果返回Admin对象则登录成功，如果账号密码不正确则抛出异常
        Admin admin = adminService.getAdminByLoginAcct(loginAcct, loginPswd);
        // 将登录成功返回的对象存入session域中
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_ADMIN, admin);
        // 返回主页面
        return "redirect:/admin/to/main/page.html";
    }

    /**
     * 处理退出登录请求
     *
     * @param session 使session失效
     * @return 重定向到登录页面
     */
    @RequestMapping("/admin/do/logout.html")
    public String doLogout(HttpSession session) {
        // 强制session失败
        session.invalidate();
        return "redirect:/admin/to/login/page.html";
    }

    /**
     * 处理分页页面
     * @param keyword 查询关键字
     * @param pageNum 当前页数
     * @param pageSize 每页数据
     * @param modelMap 将pageInfo存入model中
     * @return 分页页面
     */
    @RequestMapping("/admin/get/page.html")
    public String getPageInfo(
            // 当值为空时，需要指定默认值
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            ModelMap modelMap
    ) {
        PageInfo<Admin> pageInfo = adminService.getPageInfo(keyword, pageNum, pageSize);
        modelMap.addAttribute(CrowdConstant.ATTR_NAME_PAGE_INFO,pageInfo);
        return "admin-page";
    }

    /**
     *  删除用户请求
     * @param adminId 根据用户名删除
     * @param pageNum 返回后显示的页面
     * @param keyword 返回关键字查询时删除的页面
     * @return
     */
    @RequestMapping("/admin/remove/{adminId}/{pageNum}/{keyword}.html")
    public String remove(
            @PathVariable("adminId") Integer adminId,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("keyword") String keyword
    ){
        // 1.调用接口方法删除数据
        adminService.removeOne(adminId);
        // 2.重定向页面
        // 直接返回页面会因为梅县发送分页请求而无法显示数据
        // 使用转发发送分页请求可以实现页面的显示，但用户刷新后会造成后台重复删除
        // 使用重定向可以防止重复删除
        return "redirect:/admin/get/page.html?pageNum="+pageNum+"&keyword="+keyword;
    }

    /**
     * 保存用户请求
     * @param admin
     * @return
     */
    @PreAuthorize("hasAuthority('user:save')")
    @RequestMapping("admin/save.html")
    public String saveAdmin(Admin admin){
        // 1.调用service保存数据
        adminService.saveAdmin(admin);
        // 2.返回页面，由于新增数据在最后一条，将数据定位到最后一页
        return "redirect:/admin/get/page.html?pageNum="+Integer.MAX_VALUE;
    }

    /**
     * 更新用户时用于表单回显
     * @param adminId
     * @param modelMap
     * @return
     */
    @RequestMapping("/admin/to/edit/page.html")
    public String toEditPage(
            @RequestParam("adminId") Integer adminId,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("keyword") String keyword,
            ModelMap modelMap
    ){
        // 1.调用serivce查询数据
        Admin admin=adminService.getAdminById(adminId);
        // 2.将admin数据保存到model中，用于表单回显数据
        modelMap.addAttribute("admin",admin);
        modelMap.addAttribute("pageNum",pageNum);
        modelMap.addAttribute("keyword",keyword);
        // 3.返回到修改页面
        return "admin-edit";
    }

    /**
     * 更新用户
     * @param admin
     * @param pageNum
     * @param keyword
     * @return
     */
    @RequestMapping("admin/update.html")
    public String updateAdmin(
            Admin admin,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("keyword") String keyword
            ){
        // 1.调用service更新信息
        adminService.updateAdmin(admin);
        // 2.重定向到分页页面
        System.out.println(keyword);
        return "redirect:/admin/get/page.html?pageNum="+pageNum+"&keyword="+keyword;
    }
}
