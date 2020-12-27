
## 一, 手写模拟Spring框架核心逻辑
1. 了解Spring工作的大概流程
2. 熟悉BeanDefinition, BeanFactory, Bean等基本概念
3. 熟悉Bean的声明周期, Bean的后置处理器等概念

## 二, Spring中比较重要的几个概念详解
1. BeanDefinition(Bean的定义)
   
   Bean定义方式:  
   xml定义, @Bean, @Component(@Service, @Controller...)  
   
2. BeanDefinitionReader AnnotatedBeanDefinitionReader
 
   将bean定义(xml, @Bean, @Component)转化为BeanDefinition
   


3. XmlBeanDefinitionReader
4. ClassPathBeanDefinitionScanner
5. BeanFactory
6. ApplicationContext
7. BeanPostProcessor
8. BeanFactoryPostProcessor
9. FactoryBean

## 三, Spring中Bean的生命周期详解

## BeanDefinition - Bean 定义

1. scope 单例, 原型
2. isLazy


## BeanPostProcessor - Bean后置处理器