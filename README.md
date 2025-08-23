## 反射工具

>
提供一种统一的方式接口,对不同的反射方式进行封装,用户如果是方法反射只需要MethodReflectInvoker.invoke()
方法,属性反射只需要FieldReflectInvoker的get()或者set()

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

> 注意: 这种方式只支持public方法调用,并且每个method都会生成唯一一个类定义

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


