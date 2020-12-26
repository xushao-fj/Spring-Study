package com.xsm;

import com.xsm.framework.ApplicationContext;
import com.xsm.service.UserService;

/**
 * @author xsm
 * @Date 2020/12/26 21:44
 */
public class Test {

    public static void main(String[] args) {
        /** 启动, 扫描, 创建bean(非懒加载的单例bean)*/
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        System.out.println(userService);

        userService.test();

    }
}
