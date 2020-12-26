package com.xsm.framework;

/**
 * @author xsm
 * @Date 2020/12/27 0:31
 */
@Component
public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    public void autowired() {
        System.out.println("处理@Autowired注解注入...");
    }
}
