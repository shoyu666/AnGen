package com.xining.angen.annotation.compiler.builder;

import com.squareup.javapoet.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * @since 2019/6/1
 */
public class BuilderMethod {

    private final String methodName;
    public final TypeMirror paramType;
    private final List<? extends AnnotationMirror> annotationMirrors;

    public BuilderMethod(String methodName, TypeMirror paramType, List<? extends AnnotationMirror> annotationMirrors) {
        this.methodName = methodName;
        this.paramType = paramType;
        this.annotationMirrors = annotationMirrors;
    }

    public void addToBuilder(TypeSpec.Builder builder, ClassName builderClassName) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(paramType), methodName)
                .addStatement("this.$N = $N", methodName, methodName)
                .addStatement("return this")
                .returns(builderClassName);
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(ParameterizedTypeName.get(paramType), methodName, Modifier.PRIVATE);
        if (annotationMirrors != null && annotationMirrors.size() > 0) {
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                fieldBuilder.addAnnotation(AnnotationSpec.get(annotationMirror));
            }
        }
        builder.addMethod(methodBuilder.build());
        builder.addField(fieldBuilder.build());
    }

    public String getMethodName() {
        return methodName;
    }
}
