package com.xining.angen.annotation.compiler.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2019/6/1
 */
public abstract class BaseBuilderClazz {
    protected final List<BuilderMethod> methods = new ArrayList<>();

    protected abstract void generate(Filer filer) throws IOException;

    protected abstract void addOfMethodTBody(StringBuilder methodBody);

    public abstract void addBuildMethod(TypeSpec.Builder builder);

    /**
     * 私有构造方法
     *
     * @param builder
     */
    protected void addPrivateConstructors(TypeSpec.Builder builder) {
        MethodSpec methodSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
        builder.addMethod(methodSpec);
    }

    /**
     * interface 的所有方法
     *
     * @param builder
     * @param builderClassName
     */
    protected void addFiledMethod(TypeSpec.Builder builder, ClassName builderClassName) {
        for (BuilderMethod method : methods) {
            method.addToBuilder(builder, builderClassName);
        }
    }

    protected TypeElement mAnnotatedClassElement;
    protected String packageName;
    protected final String className;

    protected BaseBuilderClazz(TypeElement classElement) {
        this.mAnnotatedClassElement = classElement;
        this.className = mAnnotatedClassElement.getSimpleName().toString();
        Element packageElement = mAnnotatedClassElement;
        while (packageElement.getKind() != ElementKind.PACKAGE) {
            packageElement = packageElement.getEnclosingElement();
        }
        PackageElement realPackageElement = (PackageElement) packageElement;
        packageName = realPackageElement.getQualifiedName().toString();
    }


    /**
     * 添加of方法
     *
     * @param builder
     * @param builderClassName
     */
    protected void addOfMethod(TypeSpec.Builder builder, ClassName builderClassName) {
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
    protected void addOfMethodT(TypeSpec.Builder builder, ClassName builderClassName, String tName) {
        ClassName clazz = ClassName.get(builderClassName.packageName(), tName);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("of")
                .addParameter(clazz, "t")
                .returns(builderClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return new $T()");
        addOfMethodTBody(methodBody);
        methodBuilder.addStatement(methodBody.toString(), builderClassName);
        builder.addMethod(methodBuilder.build());
    }


}
