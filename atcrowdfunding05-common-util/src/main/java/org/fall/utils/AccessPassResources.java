package org.fall.utils;

import org.fall.constant.CrowdConstant;

import java.util.HashSet;
import java.util.Set;

public class AccessPassResources {

    public static Set<String> PASS_RES_SET = new HashSet<>();       // 放行的路径

    static {
        PASS_RES_SET.add("/");
        PASS_RES_SET.add("/auth/to/member/reg/page");
        PASS_RES_SET.add("/auth/to/member/login/page");
        PASS_RES_SET.add("/auth/member/logout");
        PASS_RES_SET.add("/auth/member/do/login");
        PASS_RES_SET.add("/auth/do/member/register");
        PASS_RES_SET.add("/auth/member/send/short/message.json");
    }

    public static Set<String> STATIC_RES_SET = new HashSet<>();    // 放行的静态资源

    static {
        STATIC_RES_SET.add("bootstrap");
        STATIC_RES_SET.add("css");
        STATIC_RES_SET.add("fonts");
        STATIC_RES_SET.add("img");
        STATIC_RES_SET.add("jquery");
        STATIC_RES_SET.add("layer");
        STATIC_RES_SET.add("script");
        STATIC_RES_SET.add("ztree");
    }

    public static boolean judgeCurrentServletPathWhetherStaticResource(String servletPath){

        // 字符串无效的情况
        if (servletPath == null || servletPath.length() == 0){
            throw new RuntimeException(CrowdConstant.MESSAGE_STRING_INVALIDATE);
        }

        // 以”/“截取字符串
        String[] split = servletPath.split("/");
        // 获取的字符数组中第一个/的左边为空字符串，考虑到他的索引为0，所以我们需要一级路径判断，取索引为1的字符串
        String splitFirst = split[1];
        // 判断是否在静态资源中
        return STATIC_RES_SET.contains(splitFirst);
    }

}
