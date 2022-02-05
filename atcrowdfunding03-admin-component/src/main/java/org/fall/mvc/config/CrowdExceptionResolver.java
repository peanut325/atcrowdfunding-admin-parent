package org.fall.mvc.config;

import com.google.gson.Gson;
import org.fall.constant.CrowdConstant;
import org.fall.exception.LoginAcctAlreadyInUseException;
import org.fall.exception.LoginAcctAlreadyUpdateException;
import org.fall.exception.LoginFailedException;
import org.fall.utils.CrowdUtils;
import org.fall.utils.ResultEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// @EnableWebMvc
@ControllerAdvice   // 表示一个基于注解的异常类
public class CrowdExceptionResolver {

    // 处理登录失败异常
    @ExceptionHandler(value = {LoginFailedException.class})
    public ModelAndView nullLoginFailedExceptionResolver(
            // 实际捕获到的类型
            NullPointerException nullPointerException,
            // 当前请求对象
            HttpServletRequest request,
            // 当前响应对象
            HttpServletResponse response
            // 指定普通页时去的错误页面
    ) throws IOException {
        String viewName = "system-error";
        return conmonResolver(nullPointerException, request, response, viewName);
    }

    // 处理新增用户名重复的异常
    @ExceptionHandler(value = {LoginAcctAlreadyInUseException.class})
    public ModelAndView loginAcctAlreadyInUseExceptionResolver(
            // 实际捕获到的类型
            LoginAcctAlreadyInUseException LoginAcctAlreadyInUseException,
            // 当前请求对象
            HttpServletRequest request,
            // 当前响应对象
            HttpServletResponse response
            // 指定普通页时去的错误页面
    ) throws IOException {
        String viewName = "admin-add";
        return conmonResolver(LoginAcctAlreadyInUseException, request, response, viewName);
    }

    // 处理更新用户名重复的异常
    @ExceptionHandler(value = {LoginAcctAlreadyUpdateException.class})
    public ModelAndView LoginAcctAlreadyUpdateExceptionResolver(
            // 实际捕获到的类型
            LoginAcctAlreadyUpdateException loginAcctAlreadyUpdateException,
            // 当前请求对象
            HttpServletRequest request,
            // 当前响应对象
            HttpServletResponse response
            // 指定普通页时去的错误页面
    ) throws IOException {
        String viewName = "system-error";
        return conmonResolver(loginAcctAlreadyUpdateException, request, response, viewName);
    }

    // 处理springSecurity抛出的异常
    @ExceptionHandler(value = {Exception.class})
    public ModelAndView resolveException(
            Exception exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String viewName = "system-error";
        return conmonResolver(exception, request, response, viewName);
    }

    // 抽取出公共部分代码
    private ModelAndView conmonResolver(
            // 实际捕获到的类型,使用了泛型的特性
            Exception exception,
            // 当前请求对象
            HttpServletRequest request,
            // 当前响应对象
            HttpServletResponse response,
            // 指定普通页时去的错误页面
            String viewName
    ) throws IOException {
        // 判断当前的类型请求
        boolean judgeRequestType = CrowdUtils.judgeRequestType(request);
        // 是json请求则执行
        if (judgeRequestType) {
            // 创建返回异常的实体类
            ResultEntity<Object> failed = ResultEntity.failed(exception.getMessage());
            // 创建gson对象进行json转换
            Gson gson = new Gson();
            // 转换为json字符串
            String json = gson.toJson(failed);
            // 通过原生的servlet返回
            response.getWriter().write(json);
            // 由于ResultEntity已经由respond返回，不用返回ModelAndView
            return null;
        }
        // 是普通请求则执行
        else {
            // 创建ModelAndView对象
            ModelAndView modelAndView = new ModelAndView();
            // 设置出发跳转的页面
            modelAndView.setViewName(viewName);
            // 将异常信息植入
            modelAndView.addObject(CrowdConstant.ATTR_NAME_EXCEPTION, exception);
            // 返回ModelAndView
            return modelAndView;
        }
    }
}