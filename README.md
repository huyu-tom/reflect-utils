## 反射工具

#### 背景

> - Java开发当中,反射扮演者很重要的角色,尤其是Spring和FastJson2和Mybatis中会用到大量的反射,然后各种方案,没有统一的接口方案
> - JDK原生的反射性能并不高,尤其是方法调用,所以需要提供一种高性能的反射调用方案

#### 目的

> 提供一种统一的方式接口,对不同的反射方法和属性方式进行封装,并且拥有兜底方案

1. 方法访问模式

```java
package com.huyu.method.invoker;

/**
 *
 * 反射统一调用接口
 *
 * @param <T> 目标对象
 * @param <R> 返回值 如果调用方法无返回值的话,返回的是null
 * @author huyu
 */
public interface MethodReflectInvoker<T, R> {

  /**
   *
   * 调用方法(静态方法,实例方法)
   * <p>
   * 使用方式和Method的invoke方法一样
   * </p>
   *
   * @param target
   * @param args
   * @return
   */
  R invoke(T target, Object... args);

}

```

2. 属性访问模式

```java
public interface FieldReflectInvoker<T, R> {

  /**
   *
   *
   * @param target
   * @return
   */
  void set(T target, R arg);


  /**
   *
   *
   * @param target
   * @return
   */
  R get(T target);
}
```

#### 1. 方法反射

##### 方式1：直接通过Method封装调用,默认实现类是 DefaultMethodReflectInvoker

##### 方式2：采用定义函数式接口,通过LambdaMetafactory.metafactory()方法进行实例化,默认实现方案是: FixedLambdaReflectUtils.createLambda()方法

> 注意: 这种方式必须提前定义函数式接口,所以对方法的参数个数有限制,目前仅支持0-10个参数的方法

##### 方式3: 采用ClassFile动态生成函数式接口,通过LambdaMetafactory.metafactory()方法进行实例化,默认实现方案是: ReflectMethodInvokerUtils.createDynasticLambdaInvoker()

##### 方式4: 采用ClassFile动态生成实现类, 实现方案是 : ReflectMethodInvokerUtils.createDirectMethodInvoker()

```text
//泛型T,R 分别是反射的对象,反射调用的返回值(void返回null)
public class Xxxx implements MethodReflectInvoker<> {
   @Override
   public R invoke(T obj, Object... args) {
      t.method(args)
   }
}
```

> 注意: 这种方式只支持非私有方法调用,并且每个method都会生成唯一一个类定义

##### 基准测试结果:

```text
MethodBenchmark.emptyDirectInvokerMethodInvokerCall  avgt    6   0.618 ± 0.016  ns/op
MethodBenchmark.emptyParamDirectMethodCall           avgt    6   0.422 ± 0.012  ns/op
MethodBenchmark.emptyParamLambdaMethodInvokeCall     avgt    6   0.616 ± 0.003  ns/op
MethodBenchmark.emptyParamRefMethodInvokeCall        avgt    6   8.407 ± 1.213  ns/op
MethodBenchmark.emptyStaticLambdaMethodInvokerCall   avgt    6   0.617 ± 0.020  ns/op
MethodBenchmark.hasDirectInvokerMethodInvokerCall    avgt    6   0.613 ± 0.004  ns/op
MethodBenchmark.hasParamDirectMethodCall             avgt    6   0.419 ± 0.006  ns/op
MethodBenchmark.hasParamLambdaMethodInvokerCall      avgt    6   0.614 ± 0.006  ns/op
MethodBenchmark.hasParamRefMethodInvokerCall         avgt    6  10.159 ± 0.283  ns/op
MethodBenchmark.hasStaticLambdaMethodInvokerCall     avgt    6   0.615 ± 0.006  ns/op
```

> 总结 方式四 >= 方式二 >= 方式三 > 方式一

#### 2. 属性反射

##### 方式1：通过Unsafe模式进行属性设置,默认的实现类是: UnsafeReflectFieldInvoker

##### 方式2：通过varHandle进行属性设置,默认的实现类是: VarHandleReflectFieldInvoker

##### 方式3: 通过ClassFile动态生成实现类, 实现方案是 : ReflectFieldInvokerUtils.createDirectFieldInvoker()

> 注意: 这种方式只支持非私有属性调用,并且每个属性都会生成唯一一个类定义

##### 方式4: 通过Field进行属性设置,默认的实现类是: DefaultReflectFieldInvoker

##### 基准测试结果:

```text
Benchmark                                 Mode  Cnt   Score   Error  Units
FieldBenchmark.directFieldCall            avgt    6   0.447 ± 0.088  ns/op
FieldBenchmark.directFieldInvokerCall     avgt    6   0.634 ± 0.003  ns/op
FieldBenchmark.fieldInvokerCall           avgt    6   8.897 ± 0.429  ns/op
FieldBenchmark.unsafeFieldInvokerCall     avgt    6   1.444 ± 0.041  ns/op
FieldBenchmark.varHandleFieldInvokerCall  avgt    6  38.305 ± 2.037  ns/op
```

> 总结 方式3 > 方式1 > 方式3 > 方式4
>
> 但是基准测试来看,方式2远远落后于方式4,varHandle的性能按理来说是远远大于反射字段的

#### 3. 提供一种优化JDK动态代理的模式写法

```java
package com.huyu.proxy;

import com.huyu.method.ReflectMethodInvokerUtils;
import com.huyu.method.invoker.MethodReflectInvoker;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供一种JDK动态代理优化写法
 *
 * @author huyu
 */
public class OptimizedInvocationHandler implements InvocationHandler {

  private static final VarHandle LAMBDA_CACHE_VARHANDLE;


  static {
    try {
      LAMBDA_CACHE_VARHANDLE = MethodHandles.lookup()
          .findVarHandle(OptimizedInvocationHandler.class, "invokerMap", Map.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 代理对象
   */
  private final Object target;


  /**
   * 基于 LambdaMetafactory 生成的缓存
   */
  private Map<Method, MethodReflectInvoker> invokerMap = Collections.emptyMap();


  /**
   * /** 快速模式
   */
  private final boolean fast;

  /**
   * 0 methodHandler 1 lambda 2 ref
   *
   * @param target 代理的目标对象
   * @param fast
   */
  public OptimizedInvocationHandler(Object target, boolean fast) {
    this.target = target;
    this.fast = fast;
  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (fast) {
      return fastInvoke(proxy, method, args);
    }
    return method.invoke(target, args);
  }


  private Object fastInvoke(Object proxy, Method method, Object[] args) throws Throwable {
    MethodReflectInvoker invoker = invokerMap.get(method);
    if (invoker != null) {
      return invoker.invoke(target, args);
    }
    invoker = ReflectMethodInvokerUtils.createMethodInvoker(method);
    final Map<Method, Object> newCache = new HashMap<>(invokerMap);
    newCache.put(method, invoker);
    LAMBDA_CACHE_VARHANDLE.setVolatile(this, newCache);
    return invoker.invoke(target, args);
  }
}
```

基准测试如下:

```text
ProxyBenchmark.invokerProxyCall           avgt    6  3.168 ± 0.067  ns/op
ProxyBenchmark.proxyCall(原生反射)         avgt    6  6.419 ± 0.095  ns/op
```

> 以上的测试，基准环境如下

```text
JDK环境: 
java version "24.0.1" 2025-04-15
Java(TM) SE Runtime Environment Oracle GraalVM 24.0.1+9.1 (build 24.0.1+9-jvmci-b01)
Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 24.0.1+9.1 (build 24.0.1+9-jvmci-b01, mixed mode, sharing)

系统环境:
M2 MacBook Pro 16G
版本26.0 Beta版(25A5338b)
```

