package com.xining.sample.compiler;

import com.squareup.javapoet.*;
import com.xining.sample.ProxyUtil;
import com.xining.sample.ValidatorUtil;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder class 生成器
 *
 * @since 2019/5/27
 */
public class BuilderClassGenerator {
    private TypeElement mAnnotatedClassElement;
    private final List<BuilderAnnotatedClassMethod> methods = new ArrayList<>();
    private String packageName;
    private final String className;

    public BuilderClassGenerator(TypeElement classElement) {
        this.mAnnotatedClassElement = classElement;
        this.className = mAnnotatedClassElement.getSimpleName().toString();
        init();
    }

    public void init() {
        Element packageElement = mAnnotatedClassElement;
        while (packageElement.getKind() != ElementKind.PACKAGE) {
            packageElement = packageElement.getEnclosingElement();
        }
        PackageElement realPackageElement = (PackageElement) packageElement;
        packageName = realPackageElement.getQualifiedName().toString();
        for (Element element : mAnnotatedClassElement.getEnclosedElements()) {
            switch (element.getKind()) {
                case METHOD:
                    ExecutableElement executableElement = (ExecutableElement) element;
                    methods.add(new BuilderAnnotatedClassMethod(executableElement));
                    break;
            }
        }

    }

    /**
     * 生成 *Builder.Java
     *
     * @param filer
     * @throws IOException
     */
    public void generate(Filer filer) throws IOException {
        ClassName builderClassName = ClassName.get(packageName, className + "Builder");
        TypeSpec.Builder builder = TypeSpec.classBuilder(builderClassName).
                addModifiers(Modifier.PUBLIC);
        addPrivateConstructors(builder);
        addOfMethod(builder, builderClassName);
        addOfMethodT(builder, builderClassName);
        addInterface(builder);
        addInterfaceImpl(builder);
        addFiledMethod(builder, builderClassName);
        addBuildMethod(builder);
        JavaFile javaFile = JavaFile.builder(packageName, builder.build()).
                addFileComment(" This codes are generated automatically. Do not modify!").
                build();
        javaFile.writeTo(filer);
    }

    /**
     * 私有构造方法
     *
     * @param builder
     */
    private void addPrivateConstructors(TypeSpec.Builder builder) {
        MethodSpec methodSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
        builder.addMethod(methodSpec);
    }

    /**
     * 添加of方法
     *
     * @param builder
     * @param builderClassName
     */
    private void addOfMethod(TypeSpec.Builder builder, ClassName builderClassName) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("of").
                addStatement("return new $T()", builderClassName).
                returns(builderClassName).
                addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.addMethod(methodBuilder.build());
    }

    /**
     * 添加of(T)方法
     *
     * @param builder
     * @param builderClassName
     */
    private void addOfMethodT(TypeSpec.Builder builder, ClassName builderClassName) {
        ClassName clazz = ClassName.get(packageName, className);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("of")
                .addParameter(clazz, "t")
                .returns(builderClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return new $T()");
        for (BuilderAnnotatedClassMethod method : methods) {
            String methodName = method.getMethodName();
            methodBody.append(".").append(methodName).append("(t.").append(methodName).append("())");
        }
        methodBuilder.addStatement(methodBody.toString(), builderClassName);
        builder.addMethod(methodBuilder.build());
    }

    /**
     * InvocationHandler 实现
     *
     * @param builder
     */
    private void addInterfaceImpl(TypeSpec.Builder builder) {
        FieldSpec fieldMap = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(Object.class)), "fieldMap", Modifier.PRIVATE).initializer("new $T()", HashMap.class).build();
        builder.addField(fieldMap);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("invoke").
                addModifiers(Modifier.PUBLIC).
                addException(NoSuchFieldException.class).
                addException(IllegalAccessException.class).
                returns(Object.class).
                addParameter(Object.class, "proxy").
                addParameter(Method.class, "method").
                addParameter(Object[].class, "args").
                addAnnotation(Override.class).
                addStatement("String methodName = method.getName()");

        methodBuilder.addStatement("Object value = fieldMap.get(methodName)");
        methodBuilder.beginControlFlow("if(value == null)");
        methodBuilder.addStatement("$T field = this.getClass().getDeclaredField(methodName)", Field.class);
        methodBuilder.addStatement("field.setAccessible(true)");
        methodBuilder.addStatement("fieldMap.put(methodName, value = field.get(this))");
        methodBuilder.endControlFlow();
        methodBuilder.addStatement("return value");
        builder.addMethod(methodBuilder.build());
    }

    /**
     * implemented InvocationHandler
     *
     * @param builder
     */
    private void addInterface(TypeSpec.Builder builder) {
        ClassName builderClassName = ClassName.get("java.lang.reflect", "InvocationHandler");
        builder.addSuperinterface(builderClassName);
    }

    /**
     * build 方法
     * ProxyUtil.proxy(this, T.class);
     *
     * @param builder
     */
    private void addBuildMethod(TypeSpec.Builder builder) {
        ClassName clazzName = ClassName.get(packageName, className);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("build")
                .addStatement("$T.validate(this)", ValidatorUtil.class)
                .addStatement("return $T.proxy(this,$T.class)", ProxyUtil.class, clazzName)
                .returns(clazzName);
        builder.addMethod(methodBuilder.build());
    }

    /**
     * interface 的所有方法
     *
     * @param builder
     * @param builderClassName
     */
    private void addFiledMethod(TypeSpec.Builder builder, ClassName builderClassName) {
        for (BuilderAnnotatedClassMethod method : methods) {
            method.addToBuilder(builder, builderClassName);
        }
    }
}
