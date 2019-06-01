package com.xining.angen.annotation.compiler.builder.interfac;

import com.squareup.javapoet.*;
import com.xining.angen.annotation.ProxyUtil;
import com.xining.angen.annotation.ValidatorUtil;
import com.xining.angen.annotation.compiler.builder.BaseBuilderClazz;
import com.xining.angen.annotation.compiler.builder.BuilderMethod;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @since 2019/6/1
 */
public class AnnotationBuilderClazz extends BaseBuilderClazz {

    public AnnotationBuilderClazz(TypeElement classElement) {
        super(classElement);
        for (Element element : mAnnotatedClassElement.getEnclosedElements()) {
            switch (element.getKind()) {
                case METHOD:
                    ExecutableElement executableElement = (ExecutableElement) element;
                    methods.add(new BuilderMethod(executableElement.getSimpleName().toString(),
                            executableElement.getReturnType(),
                            executableElement.getAnnotationMirrors()));
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
    @Override
    public void generate(Filer filer) throws IOException {
        ClassName builderClassName = ClassName.get(packageName, className + "Builder");
        TypeSpec.Builder builder = TypeSpec.classBuilder(builderClassName).
                addModifiers(Modifier.PUBLIC);
        addPrivateConstructors(builder);
        addOfMethod(builder, builderClassName);
        addOfMethodT(builder, builderClassName, className);
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
     * build 方法
     * ProxyUtil.proxy(this, T.class);
     *
     * @param builder
     */
    @Override
    public void addBuildMethod(TypeSpec.Builder builder) {
        ClassName clazzName = ClassName.get(packageName, className);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("build").addModifiers(Modifier.PUBLIC)
                .addStatement("$T.validate(this)", ValidatorUtil.class)
                .addStatement("return $T.proxy(this,$T.class)", ProxyUtil.class, clazzName)
                .returns(clazzName);
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

    @Override
    protected void addOfMethodTBody(StringBuilder methodBody) {
        for (BuilderMethod method : methods) {
            String methodName = method.getMethodName();
            methodBody.append(".").append(methodName).append("(t.").append(methodName).append("())");
        }
    }
}
