package com.xining.angen.annotation.compiler.builder.clazz;

import com.squareup.javapoet.*;
import com.xining.angen.annotation.ProxyUtil;
import com.xining.angen.annotation.ValidatorUtil;
import com.xining.angen.annotation.compiler.builder.BaseBuilderClazz;
import com.xining.angen.annotation.compiler.builder.BuilderMethod;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2019/6/1
 */
public class ClassBuilderClazz extends BaseBuilderClazz {
    public ClassBuilderClazz(TypeElement classElement) {
        super(classElement);
        for (Element element : mAnnotatedClassElement.getEnclosedElements()) {
            switch (element.getKind()) {
                case FIELD:
                    VariableElement variableElement = (VariableElement) element;
                    methods.add(new BuilderMethod(variableElement.getSimpleName().toString(),
                            variableElement.asType(),
                            variableElement.getAnnotationMirrors()));
                    break;
            }
        }
    }

    @Override
    public void generate(Filer filer) throws IOException {
        ClassName builderClassName = ClassName.get(packageName, className + "Builder");
        TypeSpec.Builder builder = TypeSpec.classBuilder(builderClassName).
                addModifiers(Modifier.PUBLIC);
        addPrivateConstructors(builder);
        addOfMethod(builder, builderClassName);
//        addOfMethodT(builder, builderClassName, className);
        addFiledMethod(builder, builderClassName);
        addBuildMethod(builder);
        JavaFile javaFile = JavaFile.builder(packageName, builder.build()).
                addFileComment(" This codes are generated automatically. Do not modify!").
                build();
        javaFile.writeTo(filer);
    }

    @Override
    protected void addOfMethodTBody(StringBuilder methodBody) {
        for (BuilderMethod method : methods) {
            String methodName = method.getMethodName();
            methodBody.append(".").append(methodName).append("(t.").append(methodName).append(")");
        }
    }

    @Override
    public void addBuildMethod(TypeSpec.Builder builder) {
        ClassName clazzName = ClassName.get(packageName, className);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("build").addModifiers(Modifier.PUBLIC)
                .addStatement("$T result = new $T(" + getBuilderBodyParams() + ")", mAnnotatedClassElement.asType(),mAnnotatedClassElement.asType())
                .addStatement("$T.validate(result)", ValidatorUtil.class)
                .addStatement("return result")
                .returns(clazzName);
        builder.addMethod(methodBuilder.build());
    }

    private String getBuilderBodyParams() {
        List<String> params = new ArrayList<>(methods.size());
        for (BuilderMethod method : methods) {
            String methodName = method.getMethodName();
            params.add(methodName);
        }
        return StringUtils.join(params, ",");
    }
}
