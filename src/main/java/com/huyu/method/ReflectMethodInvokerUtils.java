package com.huyu.method;

import static com.huyu.utils.ClassFileUtils.addGenericSignature;
import static com.huyu.utils.ClassFileUtils.generateAbstractMethod;
import static com.huyu.utils.ClassFileUtils.generateLambdaInvokeMethodWithGenerics;
import static com.huyu.utils.ClassFileUtils.getClassDesc;
import static com.huyu.utils.ClassFileUtils.getFullClassName;
import static com.huyu.utils.ClassFileUtils.loadClass;
import static com.huyu.utils.ClassFileUtils.saveClassToClasspath;
import static java.lang.invoke.MethodType.methodType;

import com.huyu.method.invoker.MethodReflectInvoker;
import com.huyu.method.invoker.impl.DefaultMethodReflectInvoker;
import com.huyu.utils.AotUtils;
import com.huyu.utils.ClassFileUtils;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * 方法反射调用者
 *
 * @author huyu
 */
public class ReflectMethodInvokerUtils {


  /**
   * 构建反射执行器
   *
   * @param method 目标方法
   * @param <T>    BaseReflectInvoker的子类型
   * @return 实现类实例
   * @throws Throwable 当创建实例失败时抛出
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T extends MethodReflectInvoker> T createMethodInvoker(Method method) {
    if (method == null) {
      throw new IllegalArgumentException("Method cannot be null");
    }

    // 非私有方法,并且支持ClassFile API
    if (isLoopMethod(method) && ClassFileUtils.isSupportClassFileAPI()) {
      try {
        return (T) createDirectMethodInvoker(method);
      } catch (Throwable e) {
        //忽略
      }
    }

    //AOT模式完全不支持lambda的方式调用
    if (!AotUtils.inNativeImage()) {
      try {
        return (T) createLambdaMethodInvoker(method);
      } catch (Throwable e) {
        //忽略
      }
    }

    // 情况3：兜底模式,使用反射调用（有Method属性）
    return (T) createReflectMethodInvoker(method);
  }


  /**
   * 只要不是私有方法即可
   *
   * @param method 目标方法
   * @return 是否为公共方法
   */
  private static boolean isLoopMethod(Executable method) {
    return (!Modifier.isPrivate(method.getModifiers()));
  }

  /**
   * 创建直接调用的实现类（情况1：公共方法，无属性）
   *
   * @param method 目标方法
   * @return BaseReflectInvoker实例
   * @throws Throwable 创建失败时抛出
   */
  @SuppressWarnings("rawtypes")
  public static MethodReflectInvoker createDirectMethodInvoker(Executable method) throws Throwable {

    if (method == null) {
      throw new IllegalArgumentException("Method cannot be null");
    }

    if (Modifier.isPrivate(method.getModifiers())) {
      throw new IllegalArgumentException("Method cannot be private");
    }

    if (!ClassFileUtils.isSupportClassFileAPI()) {
      throw new IllegalStateException(
          "ClassFile API is not supported. Please check your JDK version.");
    }

    String fullClassName =
        getFullClassName(method, method.getDeclaringClass()) + "_DirectMethodInvoker";

    Class<?> declaringClass = method.getDeclaringClass();

    // 检查类是否已存在
    Class<?> existingClass = null;
    try {
      existingClass = declaringClass.getClassLoader().loadClass(fullClassName);
    } catch (ClassNotFoundException ignored) {
      // 类不存在，继续执行
    }

    if (existingClass == null) {
      // 获取泛型类型参数
      ClassDesc targetTypeDesc = ClassDesc.of(method.getDeclaringClass().getName());

      ClassDesc classDesc = ClassDesc.of(fullClassName);
      ClassDesc baseInvokerDesc = ClassDesc.of(MethodReflectInvoker.class.getName());

      // 使用ClassFile API生成字节码
      byte[] classBytes = ClassFile.of().build(classDesc, cb -> {
        cb.withFlags(ClassFile.ACC_PUBLIC | ClassFile.ACC_FINAL)
            .withSuperclass(ConstantDescs.CD_Object).withInterfaceSymbols(baseInvokerDesc);

        //添加泛型
        addGenericSignature(cb, method, MethodReflectInvoker.class);

        // 生成无参构造方法
        cb.withMethod(ConstantDescs.INIT_NAME, MethodTypeDesc.of(ConstantDescs.CD_void),
            ClassFile.ACC_PUBLIC, mb -> mb.withCode(codeb -> codeb.aload(0)
                .invokespecial(ConstantDescs.CD_Object, ConstantDescs.INIT_NAME,
                    MethodTypeDesc.of(ConstantDescs.CD_void)).return_()));

        // 生成invoke方法，使用具体的泛型类型
        generateDirectInvokeMethodWithGenerics(cb, method, targetTypeDesc);
      });

      saveClassToClasspath(fullClassName, classBytes);

      // 加载并实例化类
      existingClass = loadClass(classBytes, fullClassName, method.getDeclaringClass());
    }

    return (MethodReflectInvoker) existingClass.getDeclaredConstructor().newInstance();
  }


  /**
   * 默认为动态模式
   *
   * @param method
   * @return
   * @throws Throwable
   */
  public static MethodReflectInvoker createLambdaMethodInvoker(Method method) throws Throwable {
    return createLambdaMethodInvoker(method, true, true);
  }

  /**
   * 允许回退模式
   *
   * @param method
   * @param isStatic
   * @return
   * @throws Throwable
   */
  public static MethodReflectInvoker createLambdaMethodInvoker(Method method, boolean isStatic)
      throws Throwable {
    return createLambdaMethodInvoker(method, isStatic, true);
  }

  /**
   * 创建Lambda调用的实现类（情况2：非公共方法且非AOT模式，有BaseReflectLambda属性）
   *
   * <pre>
   *
   * </pre>
   *
   * @param method   目标方法
   * @param isStatic 是否采用固定参数调用
   * @return BaseReflectInvoker实例
   * @throws Throwable 创建失败时抛出
   */
  @SuppressWarnings("rawtypes")
  public static MethodReflectInvoker createLambdaMethodInvoker(Method method, boolean isStatic,
      boolean fallback) throws Throwable {
    if (isStatic) {
      if (FixedLambdaReflectUtils.isSupportFixLambda(method) && !fallback) {
        throw new IllegalArgumentException(
            "method with more than " + FixedLambdaReflectUtils.MAX_SUPPORT_PARAMS_COUNT
                + " parameters is not supported");
      }
      return FixedLambdaReflectUtils.createLambda(method);
    }
    return createDynasticLambdaInvoker(method);
  }


  /**
   * 基于 LambdaMetafactory 创建函数式实例（实现生成的接口）
   *
   * @param method
   * @return
   * @throws Throwable
   */
  public static MethodReflectInvoker createDynasticLambdaInvoker(Executable method)
      throws Throwable {
    if (method == null) {
      throw new IllegalArgumentException("Method cannot be null");
    }

    // step1:  生成函数式接口（定义到与声明类相同 ClassLoader/包中）
    Class<?> fnIf = createReflectLambdaFunctionInterface(method);

    // step2: 使用 LambdaMetafactory 创建函数式接口实例
    return doCreateLambdaInvoker(method, fnIf);
  }


  /**
   * 使用 LambdaMetafactory 创建函数式接口实例
   *
   * @param method
   * @param fnIf
   * @return
   * @throws Throwable
   */
  private static MethodReflectInvoker doCreateLambdaInvoker(Executable method, Class<?> fnIf)
      throws Throwable {
    Class<?> decl = method.getDeclaringClass();

    MethodHandles.Lookup implLookup = MethodHandles.privateLookupIn(decl, MethodHandles.lookup());
    final MethodHandle impl;
    final int mods = method.getModifiers();
    final boolean isConstructor;
    final Class<?> returnClass;
    final boolean isVoid;
    final String samName;
    if (method instanceof Method findMethod) {
      impl = implLookup.unreflect(findMethod);
      isConstructor = false;
      returnClass = findMethod.getReturnType();
      isVoid = returnClass == void.class;
      samName = isVoid ? (Modifier.isStatic(mods) ? "acceptStatic" : "accept")
          : (Modifier.isStatic(mods) ? "applyStatic" : "apply");
    } else {
      var constructor = (Constructor<?>) method;
      impl = implLookup.unreflectConstructor(constructor);
      isConstructor = true;
      returnClass = decl;
      isVoid = false;
      samName = "applyStatic";
    }

    //工厂类型(我们生成的接口)
    final var factoryType = methodType(fnIf);

    // samMethodType: 抽象方法签名（接口上的 apply/accept：DeclaringClass + 参数列表）
    Class<?>[] samParams;
    if (Modifier.isStatic(method.getModifiers()) || isConstructor) {
      //静态方法不需要传入目标对象
      samParams = method.getParameterTypes();
    } else {
      if (method.getParameterCount() == 0) {
        samParams = new Class<?>[]{decl};
      } else {
        samParams = new Class<?>[method.getParameterCount() + 1];
        samParams[0] = decl;
        System.arraycopy(method.getParameterTypes(), 0, samParams, 1, method.getParameterCount());
      }
    }
    final var samMethodType = methodType(returnClass, samParams);

    // 使用声明类的 lookup 而不是生成接口的 lookup，这样可以访问私有方法
    final CallSite cs = LambdaMetafactory.metafactory(implLookup, samName, factoryType,
        samMethodType, impl, samMethodType);

    // 5) 获取目标工厂并创建实例
    return (MethodReflectInvoker) cs.getTarget().invoke();
  }

  /**
   * 使用 ClassFile API 创建函数式接口
   *
   * @param method
   * @return
   */
  public static Class<?> createReflectLambdaFunctionInterface(Executable method) {
    if (method == null) {
      throw new IllegalArgumentException("method cannot be null");
    }

    boolean isVoid = method instanceof Method method1 && method1.getReturnType() == void.class;
    Class<?> declaring = method.getDeclaringClass();

    //完整的类名
    String fullClassName =
        getFullClassName(method, method.getDeclaringClass()) + "_LambdaMethodInvoker";

    // 检查类是否已存在
    try {
      Class<?> existingClass = declaring.getClassLoader().loadClass(fullClassName);
      if (existingClass != null) {
        return existingClass; // 如果类已存在，直接返回
      }
    } catch (ClassNotFoundException ignored) {
      // 类不存在，继续执行
    }

    // 使用 ClassFile API 生成字节码
    byte[] bytecode = generateInterfaceBytecode(fullClassName, method, isVoid);

    // 将生成的字节码保存到类路径下
    saveClassToClasspath(fullClassName, bytecode);

    // 加载生成的字节码
    return loadClass(bytecode, fullClassName, method.getDeclaringClass());
  }

  /**
   * 使用 ClassFile API 生成函数式接口的字节码
   */
  private static byte[] generateInterfaceBytecode(String fullClassName, Executable method,
      boolean isVoid) {
    ClassFile cf = ClassFile.of();

    // 构建类名描述符
    ClassDesc thisClassDesc = ClassDesc.of(fullClassName);
    ClassDesc baseLambdaDesc = ClassDesc.of(MethodReflectInvoker.class.getName());

    return cf.build(thisClassDesc, classBuilder -> {
      classBuilder.withFlags(
              ClassFile.ACC_PUBLIC | ClassFile.ACC_INTERFACE | ClassFile.ACC_ABSTRACT)
          .withSuperclass(ConstantDescs.CD_Object).withInterfaceSymbols(baseLambdaDesc);

      // 添加泛型签名信息
      addGenericSignature(classBuilder, method, MethodReflectInvoker.class);

      // 添加 @FunctionalInterface 注解
//      addFunctionalInterfaceAnnotation(classBuilder);

      // 生成函数式抽象方法 (apply 或者 accept 或者 applyStatic 或者 acceptStatic) => 用于LambdaMetafacotry
      generateAbstractMethod(classBuilder, method, isVoid);

      // 重写默认方法区域
      // 生成符合BaseReflectLambda接口定义的桥接方法(把可变参数打开,调用apply或者accept或者applyStatic或者acceptStatic)
      generateLambdaInvokeMethodWithGenerics(classBuilder, method, isVoid, thisClassDesc, "invoke");
    });
  }

  /**
   * 创建反射调用的实现类（情况3：AOT模式，有Method属性）
   *
   * @param method 目标方法
   * @return BaseReflectInvoker实例
   * @throws Throwable 创建失败时抛出
   */
  @SuppressWarnings("rawtypes")
  public static MethodReflectInvoker createReflectMethodInvoker(Method method) {
    if (method == null) {
      throw new IllegalArgumentException("method cannot be null");
    }
    return new DefaultMethodReflectInvoker(method);
  }


  /**
   * 生成直接调用的invoke方法（支持泛型）
   *
   * @param cb             类构建器
   * @param method         目标方法
   * @param targetTypeDesc 目标对象类型描述符（泛型T）
   */
  private static void generateDirectInvokeMethodWithGenerics(ClassBuilder cb, Executable method,
      ClassDesc targetTypeDesc) {
    Class<?>[] paramTypes = method.getParameterTypes();

    // 生成泛型擦除后的invoke方法: Object invoke(Object target, Object... args)
    // 这是BaseReflectInvoker接口在字节码层面的真实签名
    var erasedMethodTypeDesc = MethodTypeDesc.of(ClassDesc.of(Object.class.getName()),
        ClassDesc.of(Object.class.getName()), ClassDesc.of(Object.class.getName()).arrayType());

    cb.withMethod("invoke", erasedMethodTypeDesc, ClassFile.ACC_PUBLIC | ClassFile.ACC_VARARGS,
        mb -> mb.withCode(codeb -> {

          if (method instanceof Constructor<?>) {
            // 处理构造函数
            codeb.new_(targetTypeDesc)     // 创建对象实例
                .dup();                    // 复制引用用于构造函数调用

            // 加载参数
            loadParameters(codeb, paramTypes);

            // 调用构造函数
            codeb.invokespecial(targetTypeDesc, ConstantDescs.INIT_NAME, getMethodTypeDesc(method));

          } else {
            // 如果是静态方法，不需要加载目标对象
            if (!Modifier.isStatic(method.getModifiers())) {
              // 加载目标对象并转换为具体类型T
              codeb.aload(1).checkcast(targetTypeDesc);
            }

            // 加载参数
            loadParameters(codeb, paramTypes);

            // 调用目标方法
            if (Modifier.isStatic(method.getModifiers())) {
              codeb.invokestatic(targetTypeDesc, method.getName(), getMethodTypeDesc(method));
            } else {
              Class<?> declaringClass = method.getDeclaringClass();
              if (declaringClass.isInterface()) {
                // 接口方法使用 invokeinterface
                codeb.invokeinterface(targetTypeDesc, method.getName(), getMethodTypeDesc(method));
              } else {
                // 普通类方法使用 invokevirtual
                codeb.invokevirtual(targetTypeDesc, method.getName(), getMethodTypeDesc(method));
              }
            }

            ClassFileUtils.generateReturnValueBoxing(codeb, ((Method) method).getReturnType());
          }

          codeb.areturn();
        }));
  }


  /**
   * 生成反射调用的invoke方法
   *
   * @param cb         类构建器
   * @param classDesc  当前类描述符
   * @param methodDesc Method字段描述符
   */
  private static void generateReflectInvokeMethod(java.lang.classfile.ClassBuilder cb,
      ClassDesc classDesc, ClassDesc methodDesc) {
    cb.withMethod("invoke", MethodTypeDesc.of(ClassDesc.of(Object.class.getName()),
            ClassDesc.of(Object.class.getName()), ClassDesc.of(Object.class.getName()).arrayType()),
        ClassFile.ACC_PUBLIC, mb -> mb.withCode(codeb -> {
          // 使用try-catch处理异常
          codeb.aload(0).getfield(classDesc, "method", methodDesc).aload(1).aload(2)
              .invokevirtual(methodDesc, "invoke",
                  MethodTypeDesc.of(ClassDesc.of(Object.class.getName()),
                      ClassDesc.of(Object.class.getName()),
                      ClassDesc.of(Object.class.getName()).arrayType())).areturn();
        }));
  }


  /**
   * 加载方法参数到栈中
   *
   * @param codeb      代码构建器
   * @param paramTypes 参数类型数组
   */
  private static void loadParameters(CodeBuilder codeb, Class<?>[] paramTypes) {
    for (int i = 0; i < paramTypes.length; i++) {
      codeb.aload(2)  // 加载args数组
          .ldc(i)    // 加载索引
          .aaload(); // 获取数组元素

      // 类型转换
      if (paramTypes[i].isPrimitive()) {
        unboxPrimitive(codeb, paramTypes[i]);
      } else {
        codeb.checkcast(ClassDesc.of(paramTypes[i].getName()));
      }
    }
  }

  /**
   * 拆箱��本类型
   *
   * @param codeb 代码构建器
   * @param type  基本类型
   */
  private static void unboxPrimitive(CodeBuilder codeb, Class<?> type) {
    if (type == int.class) {
      codeb.checkcast(ClassDesc.of(Integer.class.getName()))
          .invokevirtual(ClassDesc.of(Integer.class.getName()), "intValue",
              MethodTypeDesc.of(ConstantDescs.CD_int));
    } else if (type == long.class) {
      codeb.checkcast(ClassDesc.of(Long.class.getName()))
          .invokevirtual(ClassDesc.of(Long.class.getName()), "longValue",
              MethodTypeDesc.of(ConstantDescs.CD_long));
    } else if (type == boolean.class) {
      codeb.checkcast(ClassDesc.of(Boolean.class.getName()))
          .invokevirtual(ClassDesc.of(Boolean.class.getName()), "booleanValue",
              MethodTypeDesc.of(ConstantDescs.CD_boolean));
    } else if (type == double.class) {
      codeb.checkcast(ClassDesc.of(Double.class.getName()))
          .invokevirtual(ClassDesc.of(Double.class.getName()), "doubleValue",
              MethodTypeDesc.of(ConstantDescs.CD_double));
    } else if (type == float.class) {
      codeb.checkcast(ClassDesc.of(Float.class.getName()))
          .invokevirtual(ClassDesc.of(Float.class.getName()), "floatValue",
              MethodTypeDesc.of(ConstantDescs.CD_float));
    } else if (type == byte.class) {
      codeb.checkcast(ClassDesc.of(Byte.class.getName()))
          .invokevirtual(ClassDesc.of(Byte.class.getName()), "byteValue",
              MethodTypeDesc.of(ConstantDescs.CD_byte));
    } else if (type == short.class) {
      codeb.checkcast(ClassDesc.of(Short.class.getName()))
          .invokevirtual(ClassDesc.of(Short.class.getName()), "shortValue",
              MethodTypeDesc.of(ConstantDescs.CD_short));
    } else if (type == char.class) {
      codeb.checkcast(ClassDesc.of(Character.class.getName()))
          .invokevirtual(ClassDesc.of(Character.class.getName()), "charValue",
              MethodTypeDesc.of(ConstantDescs.CD_char));
    }
  }


  /**
   * 获取方法的类型描述符
   *
   * @param method 目标方法
   * @return 方法类型描述符
   */
  private static MethodTypeDesc getMethodTypeDesc(Executable method) {
    ClassDesc returnType;
    if (method instanceof Method findMethod) {
      returnType = getClassDesc(findMethod.getReturnType());
    } else {
      returnType = ConstantDescs.CD_void;
    }
    ClassDesc[] paramTypes = new ClassDesc[method.getParameterTypes().length];
    for (int i = 0; i < paramTypes.length; i++) {
      paramTypes[i] = getClassDesc(method.getParameterTypes()[i]);
    }
    return MethodTypeDesc.of(returnType, paramTypes);
  }


}
