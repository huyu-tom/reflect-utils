package com.huyu.test.method;

import com.huyu.method.FixedLambdaReflectUtils;
import com.huyu.method.ReflectMethodInvokerUtils;
import com.huyu.method.invoker.MethodReflectInvoker;
import com.huyu.service.XxService;
import com.huyu.service.impl.XxServiceImpl;
import java.lang.reflect.Method;

public class TestMethod {

  public static void main(String[] args) throws Throwable {

    testNoPrivateInstanceMethod();

    testPrivateInstanceMethod();

    testNoPrivateStaticMethod();

    testPrivateStaticMethod();
  }


  private static void testNoPrivateInstanceMethod() throws Throwable {
    System.out.println("=================调用非私有方法============");

    System.out.println("=================调用非私有方法无参============");
    //调用非私有方法
    XxServiceImpl service = new XxServiceImpl();
    Method notPrivateAdd = service.getClass().getDeclaredMethod("notPrivateAdd");
    notPrivateAdd.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker notPrivateStaticLambdaMethodInvoker = FixedLambdaReflectUtils.createLambda(
        notPrivateAdd);
    System.out.println(notPrivateStaticLambdaMethodInvoker.invoke(service));

    //提供动态lambda
    MethodReflectInvoker notPrivateDynasticLambdaMethodInvoker = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        notPrivateAdd);
    System.out.println(notPrivateDynasticLambdaMethodInvoker.invoke(service));

    //提供直接方法调用
    MethodReflectInvoker notPrivateDirectMethodInvoker = ReflectMethodInvokerUtils.createDirectMethodInvoker(
        notPrivateAdd);
    System.out.println(notPrivateDirectMethodInvoker.invoke(service));

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvoker = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        notPrivateAdd);
    System.out.println(reflectMethodInvoker.invoke(service));

    System.out.println("=================调用非私有方法有参============");

    //调用非私有方法
    XxServiceImpl service1 = new XxServiceImpl();
    Method notPrivateAddParams = service.getClass()
        .getDeclaredMethod("notPrivateAdd", long.class, Long.class, Integer.class, XxService.class);
    notPrivateAddParams.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker notPrivateStaticLambdaMethodInvokerParams = FixedLambdaReflectUtils.createLambda(
        notPrivateAddParams);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateStaticLambdaMethodInvokerParams.invoke(service1, 10L,
            10L, 10, service1));

    //提供动态lambda
    MethodReflectInvoker notPrivateDynasticLambdaMethodInvokerParams = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        notPrivateAddParams);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateDynasticLambdaMethodInvokerParams.invoke(service1, 10L,
            10L, 10, service1));

    //提供直接方法调用
    MethodReflectInvoker notPrivateDirectMethodInvokerParams = ReflectMethodInvokerUtils.createDirectMethodInvoker(
        notPrivateAddParams);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateDirectMethodInvokerParams.invoke(service1, 10L, 10L, 10,
            service1));

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvokerParams = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        notPrivateAddParams);
    System.out.println(
        "预期结果30: 结果为: " + reflectMethodInvokerParams.invoke(service1, 10L, 10L, 10,
            service1));
  }


  private static void testPrivateInstanceMethod() throws Throwable {
    System.out.println("=================调用私有方法============");
    //调用非私有方法
    XxServiceImpl service = new XxServiceImpl();
    Method method = service.getClass().getDeclaredMethod("privateAdd");
    method.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker privateStaticLambdaMethodInvoker = FixedLambdaReflectUtils.createLambda(
        method);
    System.out.println(privateStaticLambdaMethodInvoker.invoke(service));

    //提供动态lambda
    MethodReflectInvoker privateDynasticLambdaMethodInvoker = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        method);
    System.out.println(privateDynasticLambdaMethodInvoker.invoke(service));

    //提供直接方法调用
    try {
      MethodReflectInvoker privateDirectMethodInvoker = ReflectMethodInvokerUtils.createDirectMethodInvoker(
          method);
      System.out.println(privateDirectMethodInvoker.invoke(service));
    } catch (Throwable e) {
      System.out.println("私有直接调用报错,符合预期");
    }

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvoker = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        method);
    System.out.println(reflectMethodInvoker.invoke(service));

    System.out.println("=================调用私有方法有参============");

    //调用非私有方法
    XxServiceImpl service1 = new XxServiceImpl();
    Method notPrivateAddParams = service.getClass()
        .getDeclaredMethod("privateAdd", long.class, Long.class, Integer.class, XxService.class);
    notPrivateAddParams.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker notPrivateStaticLambdaMethodInvokerParams = FixedLambdaReflectUtils.createLambda(
        notPrivateAddParams);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateStaticLambdaMethodInvokerParams.invoke(service1, 10L,
            10L, 10, service1));

    //提供动态lambda
    MethodReflectInvoker notPrivateDynasticLambdaMethodInvokerParams = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        notPrivateAddParams);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateDynasticLambdaMethodInvokerParams.invoke(service1, 10L,
            10L, 10, service1));

    //提供直接方法调用
    try {
      MethodReflectInvoker notPrivateDirectMethodInvokerParams = ReflectMethodInvokerUtils.createDirectMethodInvoker(
          notPrivateAddParams);
      System.out.println(
          "预期结果报错,未报错: 结果为: " + notPrivateDirectMethodInvokerParams.invoke(service1,
              10L, 10L, 10, service1));
    } catch (Throwable e) {
      System.out.println("预期调用私有方法报错");
    }

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvokerParams = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        notPrivateAddParams);
    System.out.println(
        "预期结果30: 结果为: " + reflectMethodInvokerParams.invoke(service1, 10L, 10L, 10,
            service1));
  }

  private static void testPrivateStaticMethod() throws Throwable {
    System.out.println("=================调用私有静态有参============>>>");

    XxServiceImpl service = new XxServiceImpl();
    Method staticAdd1 = service.getClass()
        .getDeclaredMethod("privateStaticAdd1", long.class, Long.class, Integer.class,
            XxService.class);
    staticAdd1.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker notPrivateStaticLambdaMethodInvokerParams = FixedLambdaReflectUtils.createLambda(
        staticAdd1);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateStaticLambdaMethodInvokerParams.invoke(null, 10L, 10L,
            10, service));

    //提供动态lambda
    MethodReflectInvoker notPrivateDynasticLambdaMethodInvokerParams = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        staticAdd1);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateDynasticLambdaMethodInvokerParams.invoke(null, 10L, 10L,
            10, service));

    //提供直接方法调用
    try {
      MethodReflectInvoker notPrivateDirectMethodInvokerParams = ReflectMethodInvokerUtils.createDirectMethodInvoker(
          staticAdd1);
      System.out.println(
          "预期结果:报错, 结果为: " + notPrivateDirectMethodInvokerParams.invoke(null, 10L, 10L, 10,
              service));
    } catch (Throwable e) {
      System.out.println("直接方法调用预期调用私有静态方法报错");
    }

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvokerParams = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        staticAdd1);
    System.out.println(
        "预期结果30: 结果为: " + reflectMethodInvokerParams.invoke(null, 10L, 10L, 10, service));

    System.out.println("=================调用非私有静态无参============>>>");

    Method staticAddNoParams = service.getClass().getDeclaredMethod("privateStaticAdd1");
    staticAddNoParams.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker notPrivateStaticLambdaMethodInvoker = FixedLambdaReflectUtils.createLambda(
        staticAddNoParams);
    System.out.println("预期结果100: 结果为: " + notPrivateStaticLambdaMethodInvoker.invoke(null));

    //提供动态lambda
    MethodReflectInvoker notPrivateDynasticLambdaMethodInvoker = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        staticAddNoParams);
    System.out.println(
        "预期结果100: 结果为: " + notPrivateDynasticLambdaMethodInvoker.invoke(null));

    //提供直接方法调用
    try {
      MethodReflectInvoker notPrivateDirectMethodInvoker = ReflectMethodInvokerUtils.createDirectMethodInvoker(
          staticAddNoParams);
      System.out.println("预期结果:报错, 结果为: " + notPrivateDirectMethodInvoker.invoke(null));
    } catch (Throwable e) {
      System.out.println("直接方法调用预期调用私有静态方法报错");
    }

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvoker = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        staticAddNoParams);
    System.out.println("预期结果100: 结果为: " + reflectMethodInvoker.invoke(null));
  }

  private static void testNoPrivateStaticMethod() throws Throwable {

    System.out.println("=================调用非私有静态有参============>>>");

    XxServiceImpl service = new XxServiceImpl();
    Method staticAdd1 = service.getClass()
        .getDeclaredMethod("staticAdd1", long.class, Long.class, Integer.class, XxService.class);
    staticAdd1.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker notPrivateStaticLambdaMethodInvokerParams = FixedLambdaReflectUtils.createLambda(
        staticAdd1);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateStaticLambdaMethodInvokerParams.invoke(null, 10L, 10L,
            10, service));

    //提供动态lambda
    MethodReflectInvoker notPrivateDynasticLambdaMethodInvokerParams = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        staticAdd1);
    System.out.println(
        "预期结果30: 结果为: " + notPrivateDynasticLambdaMethodInvokerParams.invoke(null, 10L, 10L,
            10, service));

    //提供直接方法调用
    MethodReflectInvoker notPrivateDirectMethodInvokerParams = ReflectMethodInvokerUtils.createDirectMethodInvoker(
        staticAdd1);
    System.out.println(
        "预期结果:30, 结果为: " + notPrivateDirectMethodInvokerParams.invoke(null, 10L, 10L, 10,
            service));

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvokerParams = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        staticAdd1);
    System.out.println(
        "预期结果30: 结果为: " + reflectMethodInvokerParams.invoke(null, 10L, 10L, 10, service));

    System.out.println("=================调用非私有静态无参============>>>");

    Method staticAddNoParams = service.getClass().getDeclaredMethod("staticAdd1");
    staticAdd1.setAccessible(true);

    //提供静态lambda
    MethodReflectInvoker notPrivateStaticLambdaMethodInvoker = FixedLambdaReflectUtils.createLambda(
        staticAddNoParams);
    System.out.println("预期结果100: 结果为: " + notPrivateStaticLambdaMethodInvoker.invoke(null));

    //提供动态lambda
    MethodReflectInvoker notPrivateDynasticLambdaMethodInvoker = ReflectMethodInvokerUtils.createDynasticLambdaInvoker(
        staticAddNoParams);
    System.out.println(
        "预期结果100: 结果为: " + notPrivateDynasticLambdaMethodInvoker.invoke(null));

    //提供直接方法调用
    MethodReflectInvoker notPrivateDirectMethodInvoker = ReflectMethodInvokerUtils.createDirectMethodInvoker(
        staticAddNoParams);
    System.out.println("预期结果:100, 结果为: " + notPrivateDirectMethodInvoker.invoke(null));

    //提供反射方法调用
    MethodReflectInvoker reflectMethodInvoker = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        staticAddNoParams);
    System.out.println("预期结果100: 结果为: " + reflectMethodInvoker.invoke(null));
  }
}
