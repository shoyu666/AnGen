package com.xining.sample.compiler;

import com.squareup.javapoet.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * @since 2019/5/27
 */
public class BuilderAnnotatedClassMethod {
    private final String methodName;
    private final TypeMirror returnType;
    private final ExecutableElement executableElement;
    private final List<? extends AnnotationMirror> annotationMirrors;

    public BuilderAnnotatedClassMethod(ExecutableElement executableElement) {
        this.executableElement = executableElement;
        methodName = executableElement.getSimpleName().toString();
        returnType = executableElement.getReturnType();
        annotationMirrors = executableElement.getAnnotationMirrors();
    }

    public String getMethodName() {
        return methodName;
    }

    public void addToBuilder(TypeSpec.Builder builder, ClassName builderClassName) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addParameter(ParameterizedTypeName.get(returnType), methodName)
                .addStatement("this.$N = $N", methodName, methodName)
                .addStatement("return this")
                .returns(builderClassName);
        if (annotationMirrors != null && annotationMirrors.size() > 0) {
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                methodBuilder.addAnnotation(AnnotationSpec.get(annotationMirror));
            }
        }
        builder.addMethod(methodBuilder.build());
        builder.addField(ParameterizedTypeName.get(returnType), methodName, Modifier.PRIVATE);
    }
}
