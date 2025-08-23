package com.huyu.method;

import static com.huyu.utils.ClassFileUtils.addFunctionalInterfaceAnnotation;
import static com.huyu.utils.ClassFileUtils.addGenericSignature;
import static com.huyu.utils.ClassFileUtils.generateAbstractMethod;
import static com.huyu.utils.ClassFileUtils.generateLambdaInvokeMethodWithGenerics;
import static com.huyu.utils.ClassFileUtils.getClassDesc;
import static com.huyu.utils.ClassFileUtils.getFullClassName;
import static com.huyu.utils.ClassFileUtils.getPkg;
import static com.huyu.utils.ClassFileUtils.loadClass;
import static com.huyu.utils.ClassFileUtils.saveClassToClasspath;
import static java.lang.invoke.MethodType.methodType;


import com.huyu.method.invoker.MethodReflectInvoker;
import com.huyu.method.invoker.impl.DefaultMethodReflectInvoker;
import com.huyu.utils.ClassFileUtils;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * 方法反射调用者
 *
 * @author huyu
 */
public class ReflectMethodInvokerUtils {

  public static final Class<?> PACKAGE_CLASS = MethodReflectInvoker.class;


  /**
   * AOT模式标识，默认为false（可根据实际环境配置）
   */
  private static final boolean IS_AOT_MODE = Boolean.parseBoolean(
      System.getProperty("spring.aot.enabled", "false"));

  /**
   * 构建反射执行器
   *
   * @param method 目标方法
   * @param <T>    BaseReflectInvoker的子类型
   * @return 实现类实例
   * @throws Throwable 当创建实例失败时抛出
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T extends MethodReflectInvoker> T createMethodInvoker(Method method)
      throws Throwable {
    if (method == null) {
      throw new IllegalArgumentException("Method cannot be null");
    }

    // 判断方法类型并选择相应的生成策略
    if (isPublicMethod(method)) {
      // 情况1：公共方法，直接调用（无属性）
      return (T) createDirectMethodInvoker(method);
    } else if (!IS_AOT_MODE) {
      // 情况2：非公共方法且非AOT模式，使用Lambda（有BaseReflectLambda属性）
      return (T) createLambdaMethodInvoker(method);
    } else {
      // 情况3：AOT模式，使用反射调用（有Method属性）
      return (T) createReflectMethodInvoker(method);
    }
  }


  /**
   * 判断方法是否为公共方法
   *
   * @param method 目标方法
   * @return 是否为公共方法
   */
  private static boolean isPublicMethod(Method method) {
    return Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(
        method.getDeclaringClass().getModifiers());
  }

  /**
   * 创建直接调用的实现类（情况1：公共方法，无属性）
   *
   * @param method 目标方法
   * @return BaseReflectInvoker实例
   * @throws Throwable 创建失败时抛出
   */
  @SuppressWarnings("rawtypes")
  public static MethodReflectInvoker createDirectMethodInvoker(Method method) throws Throwable {
    String fullClassName = getFullClassName(method, PACKAGE_CLASS) + "$Direct";

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
        addGenericSignature(cb, method, method.getReturnType() == void.class,
            MethodReflectInvoker.class);

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
      existingClass = loadClass(classBytes, fullClassName, PACKAGE_CLASS);
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
   * @param method 目标方法
   * @return BaseReflectInvoker实例
   * @throws Throwable 创建失败时抛出
   */
  @SuppressWarnings("rawtypes")
  public static MethodReflectInvoker createLambdaMethodInvoker(Method method, boolean isStatic,
      boolean fallback) throws Throwable {
    if (isStatic) {
      if (method.getParameterCount() > 10 && !fallback) {
        throw new IllegalArgumentException(
            "Static method with more than 10 parameters is not supported");
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
  public static MethodReflectInvoker createDynasticLambdaInvoker(Method method) throws Throwable {
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
  private static MethodReflectInvoker doCreateLambdaInvoker(Method method, Class<?> fnIf)
      throws Throwable {
    Class<?> decl = method.getDeclaringClass();

    MethodHandles.Lookup implLookup = MethodHandles.privateLookupIn(decl, MethodHandles.lookup());
    var mt = methodType(method.getReturnType(), method.getParameterTypes());
    MethodHandle impl;
    int mods = method.getModifiers();
    if (Modifier.isStatic(mods)) {
      impl = implLookup.findStatic(decl, method.getName(), mt);
      // 为静态方法补一个接收者占位，以对齐 SAM 首参为 DeclaringClass 的要求
      impl = MethodHandles.dropArguments(impl, 0, decl);
    } else if (decl.isInterface() && method.isDefault()) {
      // 接口默认方法，使用 findSpecial
      impl = implLookup.findSpecial(decl, method.getName(), mt, decl);
    } else {
      // 普通实例方法
      impl = implLookup.findVirtual(decl, method.getName(), mt);
    }

    // 3) 装配三个 MethodType
    // invokedType: () -> BaseReflectLambda (使用BaseReflectLambda接口类型)
    final var invokedType = methodType(fnIf);

    // samMethodType: 抽象方法签名（接口上的 apply/accept：DeclaringClass + 参数列表）
    Class<?>[] samParams;
    if (method.getParameterCount() == 0) {
      samParams = new Class<?>[]{decl};
    } else {
      samParams = new Class<?>[method.getParameterCount() + 1];
      samParams[0] = decl;
      System.arraycopy(method.getParameterTypes(), 0, samParams, 1, method.getParameterCount());
    }
    final var samMethodType = methodType(method.getReturnType(), samParams);

    // 4) 选择 SAM 名称并创建 CallSite
    boolean isVoid = method.getReturnType() == void.class;
    // SAM方法应该是apply或accept，因为这是我们生成的函数式接口中的抽象方法
    final String samName = isVoid ? "accept" : "apply";

    // 使用生成的接口类进行 lookup，确保可以访问默认方法
    MethodHandles.Lookup fnIfLookup = MethodHandles.privateLookupIn(fnIf, MethodHandles.lookup());

    // samMethodType应该是函数式接口方法的签名，即(DeclaringClass, paramTypes...)ReturnType
    // invokedType应该是() -> BaseReflectLambda
    final CallSite cs = LambdaMetafactory.metafactory(fnIfLookup, samName, invokedType,
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
  public static Class<?> createReflectLambdaFunctionInterface(Method method) {
    if (method == null) {
      throw new IllegalArgumentException("method cannot be null");
    }

    boolean isVoid = method.getReturnType() == void.class;
    Class<?> declaring = method.getDeclaringClass();

    //完整的类名
    String fullClassName = getFullClassName(method, PACKAGE_CLASS);

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
    saveClassToClasspath(getPkg(method.getDeclaringClass()), bytecode);

    // 加载生成的字节码
    return loadClass(bytecode, fullClassName, PACKAGE_CLASS);
  }

  /**
   * 使用 ClassFile API 生成函数式接口的字节码
   */
  private static byte[] generateInterfaceBytecode(String fullClassName, Method method,
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
      addGenericSignature(classBuilder, method, isVoid, MethodReflectInvoker.class);

      // 添加 @FunctionalInterface 注解
      addFunctionalInterfaceAnnotation(classBuilder);

      // 生成函数式抽象方法 (apply 或 accept) => 用于LambdaMetafacotry
      generateAbstractMethod(classBuilder, method, isVoid);

      // 重写默认方法区域
      // 生成符合BaseReflectLambda接口定义的桥接方法(把可变参数打开,调用apply或者accept)
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
  private static void generateDirectInvokeMethodWithGenerics(java.lang.classfile.ClassBuilder cb,
      Method method, ClassDesc targetTypeDesc) {
    Class<?> returnType = method.getReturnType();
    Class<?>[] paramTypes = method.getParameterTypes();

    // 生成泛型擦除后的invoke方法: Object invoke(Object target, Object... args)
    // 这是BaseReflectInvoker接口在字节码层面的真实签名
    var erasedMethodTypeDesc = MethodTypeDesc.of(ClassDesc.of(Object.class.getName()),
        ClassDesc.of(Object.class.getName()), ClassDesc.of(Object.class.getName()).arrayType());

    cb.withMethod("invoke", erasedMethodTypeDesc, ClassFile.ACC_PUBLIC | ClassFile.ACC_VARARGS,
        mb -> mb.withCode(codeb -> {

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
            codeb.invokevirtual(targetTypeDesc, method.getName(), getMethodTypeDesc(method));
          }

          ClassFileUtils.generateReturnValueBoxing(codeb, returnType);

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
  private static MethodTypeDesc getMethodTypeDesc(Method method) {
    ClassDesc returnType = getClassDesc(method.getReturnType());
    ClassDesc[] paramTypes = new ClassDesc[method.getParameterTypes().length];
    for (int i = 0; i < paramTypes.length; i++) {
      paramTypes[i] = getClassDesc(method.getParameterTypes()[i]);
    }
    return MethodTypeDesc.of(returnType, paramTypes);
  }


}
