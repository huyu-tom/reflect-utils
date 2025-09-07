package com.huyu.field;

import static com.huyu.utils.ClassFileUtils.getPkg;
import static com.huyu.utils.ClassFileUtils.getUniSimpleClassName;
import static com.huyu.utils.ClassFileUtils.isSupportClassFileAPI;

import com.huyu.field.invoker.FieldReflectInvoker;
import com.huyu.field.invoker.impl.DefaultReflectFieldInvoker;
import com.huyu.field.invoker.impl.UnsafeReflectFieldInvoker;
import com.huyu.field.invoker.impl.VarHandleReflectFieldInvoker;
import com.huyu.utils.ClassFileUtils;
import com.huyu.utils.UnsafeUtils;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


public class ReflectFieldInvokerUtils {


  /**
   * 创建抽象字段访问器
   *
   * @param field 目标字段
   * @param <T>   FieldReflectInvoker的子类型
   * @return 最优的FieldReflectInvoker实现
   */
  @SuppressWarnings("unchecked")
  public static <T extends FieldReflectInvoker> T createFieldInvoker(Field field) {
    if (field == null) {
      throw new IllegalArgumentException("Field cannot be null");
    }

    boolean isPrivateField = field.accessFlags().contains(AccessFlag.PRIVATE);
    boolean isStaticField = field.accessFlags().contains(AccessFlag.STATIC);

    // 1. 优先尝试 Unsafe 模式
    if (UnsafeUtils.isSupportUnsafe()) {
      try {
        return createUnsafeInvoker(field);
      } catch (Throwable e) {
        //忽略
      }
    }

    // 2. 对于非私有字段或私有静态字段，可以使用ClassFile API
    if (!isPrivateField && isSupportClassFileAPI()) {
      try {
        //需要通过ClassFile API 创建 FieldReflectInvoker实现类
        return createDirectInvoker(field);
      } catch (Throwable e) {

      }
    }

    // 3. 尝试 VarHandle 模式 (需要JDK1.9以上)
    if (isVarHandleAvailable()) {
      try {
        return createVarHandleInvoker(field);
      } catch (Throwable e) {

      }
    }

    // 4. 默认使用标准反射模式
    return createDefaultInvoker(field);
  }


  /**
   * <pre>
   * 使用 ClassFile API 创建 FieldReflectInvoker 实现类
   *
   * 生成的类实现 FieldReflectInvoker&lt;T&gt; 接口，其中 invoke 方法直接访问字段：
   * public void invoke(T target, Object arg) {
   *   target.fieldName = (FieldType) arg;
   * }
   *
   * 优势：
   * 1. 直接字段访问，无反射开销
   * 2. JIT 编译器可以内联优化
   * 3. 类型安全的字段访问
   * </pre>
   *
   * @param field 目标字段，必须是 public 字段或者私有静态字段
   * @param <T>   FieldReflectInvoker 的实现类型
   * @return 动态生成的高性能字段访问器
   * @throws IllegalArgumentException      如果字段为 null 或者是私有实例字段
   * @throws UnsupportedOperationException 如果 ClassFile API 不可用
   */
  @SuppressWarnings("unchecked")
  public static <T extends FieldReflectInvoker> T createDirectInvoker(Field field) {
    if (field == null) {
      throw new IllegalArgumentException("Field cannot be null");
    }

    // 只有私有实例字段不能使用直接访问器，私有静态字段可以通过静态内部类访问
    final boolean isPrivateField = field.accessFlags().contains(AccessFlag.PRIVATE);
    if (isPrivateField) {
      throw new IllegalArgumentException(
          "Private instance field cannot use ClassFile API invoker: " + field);
    }

    //不支持classFile API
    if (!isSupportClassFileAPI()) {
      throw new UnsupportedOperationException("ClassFile API is not available on this JDK version");
    }

    //因为通过classFile生成静态内部类就可以访问私有静态属性，但是实际还是不能访问(无权限)
    boolean isStaticField = field.accessFlags().contains(AccessFlag.STATIC);

    Class<?> generatedClass = generateFieldInvokerClass(field);

    if (UnsafeUtils.isSupportUnsafe()) {
      try {
        return (T) UnsafeUtils.newInstance(generatedClass);
      } catch (InstantiationException e) {
        //忽略
      }
    }
    try {
      Constructor<?> declaredConstructor = generatedClass.getDeclaredConstructor();
      return (T) declaredConstructor.newInstance();
    } catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * 生成字段的唯一缓存键
   *
   * @param field 目标字段
   * @return 唯一的缓存键
   */
  private static String generateCacheKey(Field field) {
    return field.getDeclaringClass().getName() + "#" + field.getName() + ":" + field.getType()
        .getName();
  }

  /**
   * 使用 ClassFile API 动态生成字段访问器类
   *
   * @param field 目标字段
   * @return 生成的类
   * @throws Exception 如果生成失败
   */
  private static Class<?> generateFieldInvokerClass(Field field) {
    //生成的全类名
    // 如果是Java类，使用ClassFileUtils的包名，否则使用declaring的包名
    final String pkg = getPkg(field.getDeclaringClass());
    //构建唯一标识 返回参数 + 方法名 + 参数个数 + 参数类型名
    final String simpleClassName = getUniSimpleClassName(field) + "_FieldInvoker";
    String fullClassName = (pkg.isEmpty() ? "" : pkg + ".") + simpleClassName;

    //字段所在类
    Class<?> declaringClass = field.getDeclaringClass();

    Class<?> existingClass = null;
    try {
      existingClass = field.getDeclaringClass().getClassLoader().loadClass(fullClassName);
    } catch (ClassNotFoundException ignored) {
      // 类不存在，继续执行
    }

    if (existingClass != null) {
      return existingClass;
    }

    //字段的类型
    Class<?> fieldType = field.getType();
    //字段名称
    String fieldName = field.getName();
    //检查是否为静态字段
    boolean isStaticField = field.accessFlags().contains(AccessFlag.STATIC);
    //检查是否为私有字段
    boolean isPrivateField = field.accessFlags().contains(AccessFlag.PRIVATE);
    //检查是否为私有静态字段
    boolean isPrivateStaticField = isStaticField && isPrivateField;

    // 获取类描述符
    ClassDesc thisClassDesc = ClassDesc.of(fullClassName);
    ClassDesc fieldReflectInvokerDesc = ClassDesc.of(FieldReflectInvoker.class.getName());
    ClassDesc declaringClassDesc = getClassDesc(declaringClass);
    ClassDesc fieldTypeDesc = getClassDesc(fieldType);
    ClassDesc setMethodFieldTypeDesc;
    ClassDesc getMethodReturnTypeDesc;

    // 构建泛型签名：FieldReflectInvoker<DeclaringClass, FieldType>
    StringBuilder genericSignatureBuilder = new StringBuilder("Ljava/lang/Object;L").append(
            FieldReflectInvoker.class.getName().replace('.', '/')).append("<L")
        .append(declaringClass.getName().replace('.', '/')).append(";");

    if (fieldType.isPrimitive()) {
      // 基本类型需要装箱类型
      String wrapperClassName = ClassFileUtils.getWrapperClassName(fieldType);
      genericSignatureBuilder.append("L").append(wrapperClassName).append(";");
      setMethodFieldTypeDesc = ClassDesc.of(wrapperClassName.replace("/", "."));
      getMethodReturnTypeDesc = ClassDesc.of(wrapperClassName.replace("/", "."));
    } else {
      genericSignatureBuilder.append("L").append(fieldType.getName().replace('.', '/')).append(";");
      setMethodFieldTypeDesc = fieldTypeDesc;
      getMethodReturnTypeDesc = fieldTypeDesc;
    }
    genericSignatureBuilder.append(">;");

    // 使用 ClassFile API 生成字节码
    byte[] classBytes = ClassFile.of().build(thisClassDesc, classBuilder -> {

      //类的访问标志
      classBuilder.withFlags(isPrivateStaticField ? (ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC)
              : (ClassFile.ACC_PUBLIC | ClassFile.ACC_FINAL))

          //继承超类
          .withSuperclass(ConstantDescs.CD_Object)

          //实现接口
          .withInterfaceSymbols(fieldReflectInvokerDesc)

          // 添加泛型签名
          .with(java.lang.classfile.attribute.SignatureAttribute.of(
              classBuilder.constantPool().utf8Entry(genericSignatureBuilder.toString())))

          // 生成默认构造函数
          .withMethod(ConstantDescs.INIT_NAME, ConstantDescs.MTD_void, ClassFile.ACC_PUBLIC,
              methodBuilder -> {
                methodBuilder.withCode(codeBuilder -> {
                  codeBuilder.aload(0) // load this
                      .invokespecial(ConstantDescs.CD_Object, ConstantDescs.INIT_NAME,
                          ConstantDescs.MTD_void).return_();
                });
              });

      // 如果是私有静态字段，生成两个accept方法
      if (isPrivateStaticField) {

        //用于描述静态内部类
        classBuilder.with(java.lang.classfile.attribute.InnerClassesAttribute.of(
            // 描述内部类自身
            java.lang.classfile.attribute.InnerClassInfo.of(
                //内部类描述
                thisClassDesc,
                // 外部类的ClassDesc
                Optional.of(declaringClassDesc),
                // 内部类的简单名称
                Optional.of(simpleClassName),
                ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC | ClassFile.ACC_FINAL)));

        // 生成设置静态字段的accept方法：void accept(fieldType value)
        classBuilder.withMethod("accept", MethodTypeDesc.of(ConstantDescs.CD_void, fieldTypeDesc),
                ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC, methodBuilder -> {
                  methodBuilder.withCode(codeBuilder -> {
                    // 加载参数值 - 注意静态方法的参数索引从0开始
                    if (fieldType == long.class) {
                      codeBuilder.lload(0); // 加载long类型参数
                    } else if (fieldType == double.class) {
                      codeBuilder.dload(0); // 加载double类型参数
                    } else if (fieldType == float.class) {
                      codeBuilder.fload(0); // 加载float类型参数
                    } else if (fieldType.isPrimitive()) {
                      codeBuilder.iload(0); // 加载其他基本类型参数(int, short, byte, char, boolean)
                    } else {
                      codeBuilder.aload(0); // 加载引用类型参数
                    }

                    codeBuilder.putstatic(declaringClassDesc, fieldName, fieldTypeDesc) // 设置静态字段
                        .return_();
                  });
                })

            // 生成获取静态字段的accept方法：fieldType accept()
            .withMethod("accept", MethodTypeDesc.of(fieldTypeDesc),
                ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC, methodBuilder -> {
                  methodBuilder.withCode(codeBuilder -> {
                    codeBuilder.getstatic(declaringClassDesc, fieldName, fieldTypeDesc); // 获取静态字段

                    // 根据字段类型使用正确的返回指令
                    if (fieldType == long.class || fieldType == double.class) {
                      // long和double类型使用双字节返回指令
                      if (fieldType == long.class) {
                        codeBuilder.lreturn(); // 返回long值
                      } else {
                        codeBuilder.dreturn(); // 返回double值
                      }
                    } else if (fieldType == float.class) {
                      codeBuilder.freturn(); // 返回float值
                    } else if (fieldType.isPrimitive()) {
                      codeBuilder.ireturn(); // 返回int/short/byte/char/boolean值
                    } else {
                      codeBuilder.areturn(); // 返回引用类型值
                    }
                  });
                });
      }

      // 生成 set 方法：void set(T target, R arg)
      classBuilder.withMethod("set",
              MethodTypeDesc.of(ConstantDescs.CD_void, declaringClassDesc, setMethodFieldTypeDesc),
              ClassFile.ACC_PUBLIC, methodBuilder -> {
                methodBuilder.withCode(codeBuilder -> {
                  if (isPrivateStaticField) {
                    // 私有静态字段：调用自身的accept方法
                    codeBuilder.aload(2); // load arg

                    // 类型转换
                    if (fieldType.isPrimitive()) {
                      generateUnboxing(codeBuilder, fieldType);
                    } else {
                      codeBuilder.checkcast(fieldTypeDesc);
                    }

                    // 调用自身的accept方法
                    codeBuilder.invokestatic(thisClassDesc, "accept",
                        MethodTypeDesc.of(ConstantDescs.CD_void, fieldTypeDesc));

                  } else if (isStaticField) {
                    // 非私有静态字段：直接加载参数值，不需要target
                    codeBuilder.aload(2); // load arg

                    // 类型转换：将包装类型 arg 转换为字段类型
                    if (fieldType.isPrimitive()) {
                      // 基本类型需要拆箱
                      generateUnboxing(codeBuilder, fieldType);
                    } else {
                      // 引用类型直接强制转换
                      codeBuilder.checkcast(fieldTypeDesc);
                    }

                    // 静态字段使用 putstatic
                    codeBuilder.putstatic(declaringClassDesc, fieldName, fieldTypeDesc);
                  } else {
                    // 实例字段：需要加载target和参数值
                    codeBuilder.aload(1) // load target
                        .aload(2); // load arg

                    // 类型转换：将包装类型 arg 转换为字段类型
                    if (fieldType.isPrimitive()) {
                      // 基本类型需要拆箱
                      generateUnboxing(codeBuilder, fieldType);
                    } else {
                      // 引用类型直接强制转换
                      codeBuilder.checkcast(fieldTypeDesc);
                    }

                    // 实例字段使用 putfield
                    codeBuilder.putfield(declaringClassDesc, fieldName, fieldTypeDesc);
                  }

                  codeBuilder.return_();
                });
              })

          // 生成 get 方法：R get(T target)
          .withMethod("get", MethodTypeDesc.of(getMethodReturnTypeDesc, declaringClassDesc),
              ClassFile.ACC_PUBLIC, methodBuilder -> {
                methodBuilder.withCode(codeBuilder -> {
                  if (isPrivateStaticField) {
                    // 私有静态字段：调用自身的accept方法获取值
                    codeBuilder.invokestatic(thisClassDesc, "accept",
                        MethodTypeDesc.of(fieldTypeDesc));

                    // 类型转换：将字段类型转换为返回类型
                    if (fieldType.isPrimitive()) {
                      // 基本类型需要装箱
                      generateBoxing(codeBuilder, fieldType);
                    }

                  } else if (isStaticField) {
                    // 非私有静态字段：直接获取字段值，不需要target
                    codeBuilder.getstatic(declaringClassDesc, fieldName, fieldTypeDesc);

                    // 类型转换：将字段类型转换为返回类型
                    if (fieldType.isPrimitive()) {
                      // 基本类型需要装箱
                      generateBoxing(codeBuilder, fieldType);
                    }
                  } else {
                    // 实例字段：需要加载target然后获取字段值
                    codeBuilder.aload(1) // load target
                        .getfield(declaringClassDesc, fieldName, fieldTypeDesc);

                    // 类型转换：将字段类型转换为返回类型
                    if (fieldType.isPrimitive()) {
                      // 基本类型需要装箱
                      generateBoxing(codeBuilder, fieldType);
                    }
                  }
                  // 引用类型不需要额外转换

                  codeBuilder.areturn(); // 返回值
                });
              })

          // 生成桥接方法：void set(Object target, Object arg) - 由于泛型擦除需要
          .withMethod("set", MethodTypeDesc.of(ConstantDescs.CD_void, getClassDesc(Object.class),
                  getClassDesc(Object.class)),
              ClassFile.ACC_PUBLIC | ClassFile.ACC_BRIDGE | ClassFile.ACC_SYNTHETIC,
              methodBuilder -> {
                methodBuilder.withCode(codeBuilder -> {
                  codeBuilder.aload(0); // load this

                  // 对于静态字段和实例字段，都需要正确的参数处理
                  codeBuilder.aload(1) // load target (即使是静态字段也要传递，在具体方法中会被忽略)
                      .checkcast(declaringClassDesc) // cast to specific type
                      .aload(2); // load arg

                  // 根据字段类型进行适当的类型转换，确保调用正确的方法签名
                  if (fieldType.isPrimitive()) {
                    // 基本类型：转换为对应的包装类型
                    codeBuilder.checkcast(setMethodFieldTypeDesc);
                  } else {
                    // 引用类型：保持 Object 类型，但仍需要进行类型转换以匹配方法签名
                    codeBuilder.checkcast(setMethodFieldTypeDesc);
                  }

                  // 调用具体的 set 方法，使用正确的方法签名
                  codeBuilder.invokevirtual(thisClassDesc, "set",
                      MethodTypeDesc.of(ConstantDescs.CD_void, declaringClassDesc,
                          setMethodFieldTypeDesc)).return_();
                });
              })

          // 生成桥接方法：Object get(Object target) - 由于泛型擦除需要
          .withMethod("get",
              MethodTypeDesc.of(getClassDesc(Object.class), getClassDesc(Object.class)),
              ClassFile.ACC_PUBLIC | ClassFile.ACC_BRIDGE | ClassFile.ACC_SYNTHETIC,
              methodBuilder -> {
                methodBuilder.withCode(codeBuilder -> {
                  codeBuilder.aload(0); // load this

                  // 对于静态字段和实例字段，都需要正确的参数处理
                  codeBuilder.aload(1) // load target (即使是静态字段也要传递，在具体方法中会被忽略)
                      .checkcast(declaringClassDesc); // cast to specific type

                  codeBuilder.invokevirtual(thisClassDesc, "get",
                          MethodTypeDesc.of(getMethodReturnTypeDesc, declaringClassDesc))
                      .areturn(); // 返回值
                });
              });
    });

    //保存类字节码到文件
    ClassFileUtils.saveClassToClasspath(fullClassName, classBytes);

    //加载定义类
    return ClassFileUtils.loadClass(classBytes, fullClassName, field.getDeclaringClass());
  }


  /**
   * 生成基本类型装箱字节码
   *
   * @param codeBuilder   字节码构建器
   * @param primitiveType 基本类型
   */
  private static void generateBoxing(CodeBuilder codeBuilder, Class<?> primitiveType) {
    if (primitiveType == boolean.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Boolean"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Boolean"), ConstantDescs.CD_boolean));
    } else if (primitiveType == byte.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Byte"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Byte"), ConstantDescs.CD_byte));
    } else if (primitiveType == char.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Character"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Character"), ConstantDescs.CD_char));
    } else if (primitiveType == short.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Short"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Short"), ConstantDescs.CD_short));
    } else if (primitiveType == int.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Integer"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Integer"), ConstantDescs.CD_int));
    } else if (primitiveType == long.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Long"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Long"), ConstantDescs.CD_long));
    } else if (primitiveType == float.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Float"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Float"), ConstantDescs.CD_float));
    } else if (primitiveType == double.class) {
      codeBuilder.invokestatic(ClassDesc.of("java.lang.Double"), "valueOf",
          MethodTypeDesc.of(ClassDesc.of("java.lang.Double"), ConstantDescs.CD_double));
    } else {
      throw new IllegalArgumentException("Unsupported primitive type for boxing: " + primitiveType);
    }
  }

  /**
   * 生成基本类型拆箱字节码
   *
   * @param codeBuilder   字节码构建器
   * @param primitiveType 基本类型
   */
  private static void generateUnboxing(CodeBuilder codeBuilder, Class<?> primitiveType) {
    if (primitiveType == boolean.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Boolean"))
          .invokevirtual(ClassDesc.of("java.lang.Boolean"), "booleanValue",
              MethodTypeDesc.of(ConstantDescs.CD_boolean));
    } else if (primitiveType == byte.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Byte"))
          .invokevirtual(ClassDesc.of("java.lang.Byte"), "byteValue",
              MethodTypeDesc.of(ConstantDescs.CD_byte));
    } else if (primitiveType == char.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Character"))
          .invokevirtual(ClassDesc.of("java.lang.Character"), "charValue",
              MethodTypeDesc.of(ConstantDescs.CD_char));
    } else if (primitiveType == short.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Short"))
          .invokevirtual(ClassDesc.of("java.lang.Short"), "shortValue",
              MethodTypeDesc.of(ConstantDescs.CD_short));
    } else if (primitiveType == int.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Integer"))
          .invokevirtual(ClassDesc.of("java.lang.Integer"), "intValue",
              MethodTypeDesc.of(ConstantDescs.CD_int));
    } else if (primitiveType == long.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Long"))
          .invokevirtual(ClassDesc.of("java.lang.Long"), "longValue",
              MethodTypeDesc.of(ConstantDescs.CD_long));
    } else if (primitiveType == float.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Float"))
          .invokevirtual(ClassDesc.of("java.lang.Float"), "floatValue",
              MethodTypeDesc.of(ConstantDescs.CD_float));
    } else if (primitiveType == double.class) {
      codeBuilder.checkcast(ClassDesc.of("java.lang.Double"))
          .invokevirtual(ClassDesc.of("java.lang.Double"), "doubleValue",
              MethodTypeDesc.of(ConstantDescs.CD_double));
    } else {
      throw new IllegalArgumentException("Unsupported primitive type: " + primitiveType);
    }
  }

  /**
   * 获取类的 ClassDesc 描述符
   *
   * @param clazz 目标类
   * @return ClassDesc 描述符
   */
  private static ClassDesc getClassDesc(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      return getPrimitiveClassDesc(clazz);
    } else if (clazz.isArray()) {
      return getClassDesc(clazz.getComponentType()).arrayType();
    } else {
      return ClassDesc.of(clazz.getName());
    }
  }

  /**
   * 获取基本类型的 ClassDesc 描述符
   *
   * @param primitiveType 基本类型
   * @return ClassDesc 描述符
   */
  private static ClassDesc getPrimitiveClassDesc(Class<?> primitiveType) {
    if (primitiveType == boolean.class) {
      return ConstantDescs.CD_boolean;
    }
    if (primitiveType == byte.class) {
      return ConstantDescs.CD_byte;
    }
    if (primitiveType == char.class) {
      return ConstantDescs.CD_char;
    }
    if (primitiveType == short.class) {
      return ConstantDescs.CD_short;
    }
    if (primitiveType == int.class) {
      return ConstantDescs.CD_int;
    }
    if (primitiveType == long.class) {
      return ConstantDescs.CD_long;
    }
    if (primitiveType == float.class) {
      return ConstantDescs.CD_float;
    }
    if (primitiveType == double.class) {
      return ConstantDescs.CD_double;
    }
    if (primitiveType == void.class) {
      return ConstantDescs.CD_void;
    }
    throw new IllegalArgumentException("Unknown primitive type: " + primitiveType);
  }

  /**
   * 检查 VarHandle 是否可用
   *
   * @return true if VarHandle is available, false otherwise
   */
  private static boolean isVarHandleAvailable() {
    try {
      Class.forName("java.lang.invoke.VarHandle");
      return true;
    } catch (ClassNotFoundException | Error e) {
      return false;
    }
  }


  @SuppressWarnings("unchecked")
  public static <T extends FieldReflectInvoker> T createUnsafeInvoker(Field field) {
    if (field == null) {
      throw new IllegalArgumentException("Field cannot be null");
    }
    return (T) new UnsafeReflectFieldInvoker(field);
  }

  @SuppressWarnings("unchecked")
  public static <T extends FieldReflectInvoker> T createVarHandleInvoker(Field field) {
    if (field == null) {
      throw new IllegalArgumentException("Field cannot be null");
    }
    return (T) new VarHandleReflectFieldInvoker(field);
  }

  @SuppressWarnings("unchecked")
  public static <T extends FieldReflectInvoker> T createDefaultInvoker(Field field) {
    if (field == null) {
      throw new IllegalArgumentException("Field cannot be null");
    }
    return (T) new DefaultReflectFieldInvoker(field);
  }

}
