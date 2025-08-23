package com.huyu.utils;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * ClassFile API UTILS
 */
public class ClassFileUtils {


  public static String getFullClassName(Method method, Class<?> packageClass) {
    // 如果是Java类，使用ClassFileUtils的包名，否则使用declaring的包名
    final String pkg = getPkg(packageClass);
    //构建唯一标识 返回参数 + 方法名 + 参数个数 + 参数类型名
    final String simpleClassName = getUniSimpleClassName(method);
    return (pkg.isEmpty() ? "" : pkg + ".") + simpleClassName;
  }

  public static String getPkg(Class<?> declaring) {
    final boolean isJavaClass = isJavaPackage(declaring);
    return isJavaClass ? ClassFileUtils.class.getPackage().getName()
        : (declaring.getPackage() == null ? "" : declaring.getPackage().getName());
  }

  public static String getUniSimpleClassName(Method method) {
    Class<?> declaring = method.getDeclaringClass();
    final String methodSignature =
        method.getReturnType().getCanonicalName() + "$" + method.getName() + "$"
            + method.getParameterCount() + "$" + java.util.Arrays.stream(method.getParameterTypes())
            .map(Class::getCanonicalName).collect(java.util.stream.Collectors.joining("$"));
    final String simpleClassName = (declaring.getSimpleName() + "$" + methodSignature).replace(".",
        "$");
    return simpleClassName;
  }

  public static String getUniSimpleClassName(Field field) {
    Class<?> declaring = field.getDeclaringClass();
    final String fieldSignature = field.getName() + "$" + field.getType().getName();
    final String simpleClassName = (declaring.getSimpleName() + "$" + fieldSignature).replace(".",
        "$");
    return simpleClassName;
  }

  public static String getFullClassName(Field field, Class<?> interfaceClass) {
    // 如果是Java类，使用ClassFileUtils的包名，否则使用declaring的包名
    final String pkg = getPkg(interfaceClass);
    //构建唯一标识 返回参数 + 方法名 + 参数个数 + 参数类型名
    final String simpleClassName = getUniSimpleClassName(field);
    return (pkg.isEmpty() ? "" : pkg + ".") + simpleClassName;
  }


  /**
   * 添加泛型签名信息到类
   */
  public static void addGenericSignature(ClassBuilder classBuilder, Method method, boolean isVoid,
      Class<?> interfaceClass) {
    Class<?> declaring = method.getDeclaringClass();
    Class<?> returnType = method.getReturnType();

    // 构建泛型签名字符串：Ljava/lang/Object;Lorg/mybatis/test/BaseReflectLambda<LTestDefaultInterface;LLong;>;
    StringBuilder signature = new StringBuilder();
    signature.append("Ljava/lang/Object;");
    signature.append("L").append(interfaceClass.getName().replace('.', '/')).append("<");

    // 添加第一个泛型参数：目标类型
    signature.append("L").append(declaring.getName().replace('.', '/')).append(";");

    // 添加第二个泛型参数：返回类型
    if (isVoid) {
      signature.append("Ljava/lang/Object;");
    } else {
      if (returnType.isPrimitive()) {
        // 基本类型需要装箱类型
        signature.append("L").append(getWrapperClassName(returnType)).append(";");
      } else {
        signature.append("L").append(returnType.getName().replace('.', '/')).append(";");
      }
    }

    signature.append(">;");

    // 正确添加 Signature 属性
    classBuilder.with(java.lang.classfile.attribute.SignatureAttribute.of(
        classBuilder.constantPool().utf8Entry(signature.toString())));
  }

  /**
   * 获取基本类型对应的包装类名
   */
  public static String getWrapperClassName(Class<?> primitiveType) {
    if (primitiveType == boolean.class) {
      return "java/lang/Boolean";
    }
    if (primitiveType == byte.class) {
      return "java/lang/Byte";
    }
    if (primitiveType == char.class) {
      return "java/lang/Character";
    }
    if (primitiveType == short.class) {
      return "java/lang/Short";
    }
    if (primitiveType == int.class) {
      return "java/lang/Integer";
    }
    if (primitiveType == long.class) {
      return "java/lang/Long";
    }
    if (primitiveType == float.class) {
      return "java/lang/Float";
    }
    if (primitiveType == double.class) {
      return "java/lang/Double";
    }
    throw new IllegalArgumentException("Not a primitive type: " + primitiveType);
  }


  /**
   * 添加 @FunctionalInterface 注解
   */
  public static void addFunctionalInterfaceAnnotation(ClassBuilder classBuilder) {
    // 添加 @FunctionalInterface 注解
    ClassDesc functionalInterfaceDesc = ClassDesc.of(FunctionalInterface.class.getName());
    classBuilder.with(java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute.of(
        java.lang.classfile.Annotation.of(functionalInterfaceDesc)));
  }

  /**
   * 生成抽象方法 (apply 或 accept)
   */
  public static void generateAbstractMethod(ClassBuilder classBuilder, Method method,
      boolean isVoid) {
    String methodName = isVoid ? "accept" : "apply";
    Class<?> declaring = method.getDeclaringClass();
    Class<?>[] paramTypes = method.getParameterTypes();

    // 构建方���描述符
    ClassDesc[] params = new ClassDesc[paramTypes.length + 1];
    params[0] = getClassDesc(declaring);
    for (int i = 0; i < paramTypes.length; i++) {
      params[i + 1] = getClassDesc(paramTypes[i]);
    }

    ClassDesc returnType = isVoid ? ConstantDescs.CD_void : getClassDesc(method.getReturnType());
    MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(returnType, params);

    classBuilder.withMethod(methodName, methodTypeDesc,
        ClassFile.ACC_PUBLIC | ClassFile.ACC_ABSTRACT, methodBuilder -> {
          // 抽象方法不需要方法体
        });
  }


  /**
   * 生成默认的 invoke 方法 - 高性能版本，直接实现桥接方法避免双重调用
   */
  public static void generateLambdaInvokeMethodWithGenerics(ClassBuilder classBuilder,
      Method method, boolean isVoid, ClassDesc thisClassDesc, String genericMethodName) {
    Class<?> declaring = method.getDeclaringClass();

    // invoke 方法的签名应该是 (Object, Object[]) -> Object  类型擦除
    MethodTypeDesc invokeMethodTypeDesc = MethodTypeDesc.of(getClassDesc(Object.class),
        getClassDesc(Object.class), getClassDesc(Object.class).arrayType());

    classBuilder.withMethod(genericMethodName, invokeMethodTypeDesc, ClassFile.ACC_PUBLIC,
        methodBuilder -> {
          methodBuilder.withCode(codeBuilder -> {
            // 加载 this
            codeBuilder.aload(0);

            // 加载 target 参数并转换为声明类
            codeBuilder.aload(1);
            codeBuilder.checkcast(getClassDesc(declaring));

            Class<?>[] paramTypes = method.getParameterTypes();
            // 处理参数数组 Object[]
            for (int i = 0; i < paramTypes.length; i++) {
              codeBuilder.aload(2); // 加载 args 数组
              codeBuilder.loadConstant(i); // 加载索引
              codeBuilder.aaload(); // 获取数组元素 Object
              generateParameterConversion(codeBuilder, paramTypes[i]);
            }

            // 调用抽象方法 apply/accept
            String methodName = isVoid ? "accept" : "apply";
            ClassDesc[] params = new ClassDesc[paramTypes.length + 1];
            params[0] = getClassDesc(declaring);
            for (int i = 0; i < paramTypes.length; i++) {
              params[i + 1] = getClassDesc(paramTypes[i]);
            }

            ClassDesc returnType =
                isVoid ? ConstantDescs.CD_void : getClassDesc(method.getReturnType());
            MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(returnType, params);
            codeBuilder.invokeinterface(thisClassDesc, methodName, methodTypeDesc);

            // 处理返回值（基本类型需要装箱）
            generateReturnValueBoxing(codeBuilder, method.getReturnType());

            // 返回结果
            codeBuilder.areturn();
          });
        });
  }


  /**
   * ：参数转换（数组/引���/基本类型拆箱）
   *
   * @param codeBuilder
   * @param paramType
   */
  private static void generateParameterConversion(CodeBuilder codeBuilder, Class<?> paramType) {
    if (paramType.isPrimitive()) {
      ClassDesc wrapper = getWrapperClassDesc(paramType);
      codeBuilder.checkcast(wrapper);
      String unbox = getUnboxMethodName(paramType);
      MethodTypeDesc unboxDesc = MethodTypeDesc.of(getClassDesc(paramType));
      codeBuilder.invokevirtual(wrapper, unbox, unboxDesc);
    } else {
      // 引用或数组直接 checkcast
      codeBuilder.checkcast(getClassDesc(paramType));
    }
  }


  /**
   * ：返回值装箱（仅基本类型）
   *
   * @param codeBuilder
   * @param returnType
   */
  public static void generateReturnValueBoxing(CodeBuilder codeBuilder, Class<?> returnType) {
    if (!returnType.isPrimitive()) {
      // 引用类型无需装箱
      return;
    }
    if (returnType == void.class) {
      codeBuilder.aconst_null();
      return;
    }
    ClassDesc wrapper = getWrapperClassDesc(returnType);
    MethodTypeDesc boxDesc = MethodTypeDesc.of(wrapper, getClassDesc(returnType));
    codeBuilder.invokestatic(wrapper, "valueOf", boxDesc);
  }

  /**
   * 获取类描述符 - 统一使用内部名称格式
   */
  public static ClassDesc getClassDesc(Class<?> clazz) {
    if (clazz == void.class) {
      return ConstantDescs.CD_void;
    }
    if (clazz == boolean.class) {
      return ConstantDescs.CD_boolean;
    }
    if (clazz == byte.class) {
      return ConstantDescs.CD_byte;
    }
    if (clazz == char.class) {
      return ConstantDescs.CD_char;
    }
    if (clazz == short.class) {
      return ConstantDescs.CD_short;
    }
    if (clazz == int.class) {
      return ConstantDescs.CD_int;
    }
    if (clazz == long.class) {
      return ConstantDescs.CD_long;
    }
    if (clazz == float.class) {
      return ConstantDescs.CD_float;
    }
    if (clazz == double.class) {
      return ConstantDescs.CD_double;
    }

    if (clazz.isArray()) {
      return getClassDesc(clazz.getComponentType()).arrayType();
    }

    return ClassDesc.of(clazz.getName());
  }

  /**
   * 获��包装类描述符 - 统一使用内部名称格式
   */
  private static ClassDesc getWrapperClassDesc(Class<?> primitiveType) {
    if (primitiveType == boolean.class) {
      return ClassDesc.of("java.lang.Boolean");
    }
    if (primitiveType == byte.class) {
      return ClassDesc.of("java.lang.Byte");
    }
    if (primitiveType == char.class) {
      return ClassDesc.of("java.lang.Character");
    }
    if (primitiveType == short.class) {
      return ClassDesc.of("java.lang.Short");
    }
    if (primitiveType == int.class) {
      return ClassDesc.of("java.lang.Integer");
    }
    if (primitiveType == long.class) {
      return ClassDesc.of("java.lang.Long");
    }
    if (primitiveType == float.class) {
      return ClassDesc.of("java.lang.Float");
    }
    if (primitiveType == double.class) {
      return ClassDesc.of("java.lang.Double");
    }

    throw new IllegalArgumentException("Not a primitive type: " + primitiveType);
  }

  /**
   * 获取拆箱方法名
   */
  private static String getUnboxMethodName(Class<?> primitiveType) {
    if (primitiveType == boolean.class) {
      return "booleanValue";
    }
    if (primitiveType == byte.class) {
      return "byteValue";
    }
    if (primitiveType == char.class) {
      return "charValue";
    }
    if (primitiveType == short.class) {
      return "shortValue";
    }
    if (primitiveType == int.class) {
      return "intValue";
    }
    if (primitiveType == long.class) {
      return "longValue";
    }
    if (primitiveType == float.class) {
      return "floatValue";
    }
    if (primitiveType == double.class) {
      return "doubleValue";
    }

    throw new IllegalArgumentException("Not a primitive type: " + primitiveType);
  }

  /**
   * 加载生成的字节码
   */
  public static Class<?> loadClass(byte[] bytecode, String fullClassName, Class<?> targetClass) {
    try {
      MethodHandles.Lookup hostLookup = MethodHandles.privateLookupIn(targetClass,
          MethodHandles.lookup());
      return hostLookup.defineClass(bytecode);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to load generated class: " + fullClassName, e);
    }
  }

  /**
   * 将生成的字节码保存到类路径下（target/classes）
   */
  public static void saveClassToClasspath(String fqn, byte[] bytecode) {
    try {

//      if (true) {
//        return;
//      }

      // 获取当前工作目录下的 target/classes 目录
      String workingDir = System.getProperty("user.dir");
      String classesDir = workingDir + "/target/classes";

      // 构建类文件路径
      String classFilePath = fqn.replace('.', '/') + ".class";
      java.io.File classFile = new java.io.File(classesDir, classFilePath);

      // 确保父目录存在
      classFile.getParentFile().mkdirs();

      // 写入字节码
      try (java.io.FileOutputStream fos = new java.io.FileOutputStream(classFile)) {
        fos.write(bytecode);
        System.out.println("生成的类已保存到: " + classFile.getAbsolutePath());
      }

    } catch (java.io.IOException e) {
      // 保存失败不影响主流程，只打印警告
      System.err.println("警告：无法保存生成的类到文件系统: " + e.getMessage());
    }
  }


  /**
   * 是否是Java包
   *
   * @param declaring
   * @return
   */
  private static boolean isJavaPackage(Class<?> declaring) {
    return declaring.getName().startsWith("java.") || declaring.getName().startsWith("javax")
        || declaring.getName().startsWith("sun");
  }
}
