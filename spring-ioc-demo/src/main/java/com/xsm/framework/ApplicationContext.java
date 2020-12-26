package com.xsm.framework;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xsm
 * @Date 2020/12/26 21:44
 */
public class ApplicationContext {

    /** 配置类*/
    private Class configClass;

    /** beanDefinition缓存*/
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();

    /** 单例bean缓存*/
    private Map<String, Object> singletonObjects = new HashMap<String, Object>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<BeanPostProcessor>();




    public Class getConfigClass() {
        return configClass;
    }

    public void setConfigClass(Class configClass) {
        this.configClass = configClass;


    }

    public ApplicationContext() {
    }

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;
        // 扫描--beanDefinition
        scan(configClass);
        // 创建非懒加载的单例bean
        createNotLazeSingleton();
    }

    /**
     * 创建非来加载的单例bean
     */
    private void createNotLazeSingleton() {

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            if ("singleton".equals(beanDefinition.getScope()) && !beanDefinition.isLazy()) {
                Object bean = createBean(entry.getValue(), entry.getKey());
                singletonObjects.put(entry.getKey(), bean);
            }
        }
    }

    /**
     * 创建bean
     * @return
     */
    private Object createBean(BeanDefinition beanDefinition, String beanName) {
        // 创建 bean -> 对象
        Class beanClass = beanDefinition.getBeanClass();
        Object instance = null;
        try {
            // 1 实例化bean
            instance = beanClass.getDeclaredConstructor().newInstance();

            // 使用 beanPostProcessor处理
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.autowired();
            }

            // 2 填充属性
            // 反射,遍历属性
            for (Field field : beanClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) { // 在spring中, Autowired注解由AutowiredAnnotationBeanPostProcessor实现
                    // 判断是否有Autowired注解
                    // 给对象的哪个字段赋值, 值从哪里来呢?
                    // byType, byName
                    // spring 首先获取 byType(保证类型安全) -> 但是有可能 byType会有多个的情况, 那么再用byName去拿, 因为name是唯一的
                    // 为什么不先用byName, 因为byName有可能会拿不到(用户可以自定义name, 或者可能拿错了)
                    Class<?> type = field.getType();
                    Object bean = getBean(field.getName());// byName方式
                    field.setAccessible(true);
                    field.set(instance, bean);
                }
            }

            // 判断是否实现了BeanNameAware接口
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            // 属性设置完了之后,判断是否实现了InitializingBean接口,做后续操作
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }


            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return instance;
    }

    private void scan(Class configClass) {
        // 扫描
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            path = path.replace(".", "/");
            System.out.println(path);
            // 获取 classLoader
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            // 获取 classLoader 加载路径
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            for (File f : file.listFiles()) {
                String s = f.getAbsolutePath();
                if (s.endsWith(".class")) {
                    s = s.substring(s.indexOf("com"), s.indexOf(".class"));
                    s = s.replace("\\", ".");
                }
                // E:\learn\SpringStudy\spring-ioc\target\classes\com\xsm\service\UserService.class
                try {
                    Class<?> clazz = classLoader.loadClass(s);

                    System.out.println(clazz);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        // bean
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setBeanClass(clazz);
                        Component component = clazz.getAnnotation(Component.class);
                        String beanName = component.value();

                        // 表示是一个bean
                        if (clazz.isAnnotationPresent(Lazy.class)) {
                            beanDefinition.setLazy(true);
                        }
                        if (clazz.isAnnotationPresent(Scope.class)) {
                            // 判断是否scope
                            Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                            String value = scopeAnnotation.value();
                            beanDefinition.setScope(value);
                        } else {
                            // 单例
                            beanDefinition.setScope("singleton");
                        }
                        beanDefinitionMap.put(beanName, beanDefinition);

                        // 判断类是否实现了 BeanPostProcessor接口, 如果是
                        if (BeanPostProcessor.class.isAssignableFrom(clazz)) { // 派生
                            BeanPostProcessor o = null;
                            try {
                                o = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                            beanPostProcessorList.add(o);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                System.out.println(f);
            }
        }
    }

    public Object getBean(String beanName){

        Object bean = null;
        // map维护 bean
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new NullPointerException();
        }
        else {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                // 单例bean
                bean = singletonObjects.get(beanName);
                if (bean == null) {
                    bean = createBean(beanDefinition, beanName);
                    singletonObjects.put(beanName, bean);
                }
            }
            else if("prototype".equals(beanDefinition.getScope())) {
                // 创建一个bean
                bean = createBean(beanDefinition, beanName);
            }
        }
        return bean;
    }

}
