package com.xsm.service;

import com.xsm.framework.Autowired;
import com.xsm.framework.BeanNameAware;
import com.xsm.framework.Component;
import com.xsm.framework.InitializingBean;

/**
 * @author xsm
 * @Date 2020/12/26 21:41
 */
@Component(value = "userService")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test(){
        System.out.println(orderService);
        System.out.println(beanName);
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;

    }

    public void afterPropertiesSet() {
        // 初始化操作

        if (orderService == null) {
            throw new NullPointerException();
        }
    }
}
