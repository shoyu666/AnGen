package com.xining.angen.annotation.compiler;

import com.google.auto.service.AutoService;
import com.xining.angen.annotation.Builder;
import com.xining.angen.annotation.compiler.builder.clazz.ClassBuilderClazz;
import com.xining.angen.annotation.compiler.builder.interfac.AnnotationBuilderClazz;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @since 2019/5/27
 */
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {
    private ProcessingEnvironment processingEnv;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Builder.class);
        try {
            for (Element annotatedElement : elements) {
                processElement(annotatedElement);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void processElement(Element annotatedElement) throws IOException {
        if (annotatedElement.getKind() == ElementKind.INTERFACE) {
            TypeElement typeElement = (TypeElement) annotatedElement;
            AnnotationBuilderClazz generator = new AnnotationBuilderClazz(typeElement);
            generator.generate(filer);
        }
        if (annotatedElement.getKind() == ElementKind.CLASS) {
            TypeElement typeElement = (TypeElement) annotatedElement;
            ClassBuilderClazz generator = new ClassBuilderClazz(typeElement);
            generator.generate(filer);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Builder.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
