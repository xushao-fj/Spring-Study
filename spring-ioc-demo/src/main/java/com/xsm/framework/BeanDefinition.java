package com.xsm.framework;

/**
 * @author xsm
 * @Date 2020/12/26 23:16
 * bean 定义
 */
public class BeanDefinition {

    private String scope;

    private boolean isLazy;

    private Class beanClass;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
}
