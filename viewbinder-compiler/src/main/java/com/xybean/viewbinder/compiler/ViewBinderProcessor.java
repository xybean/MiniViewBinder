package com.xybean.viewbinder.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xybean.viewbinder.annotations.BindView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ViewBinderProcessor extends AbstractProcessor {

    private Messager messager;
    private HashMap<String, ArrayList<Element>> annotatedElementMap;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        annotatedElementMap = new HashMap<>();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            String name = element.getEnclosingElement().asType().toString();
            if (annotatedElementMap.get(name) == null) {
                annotatedElementMap.put(name, new ArrayList<Element>());
            }
            annotatedElementMap.get(name).add(element);
        }

        if (annotatedElementMap.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, ArrayList<Element>> entry : annotatedElementMap.entrySet()) {
            Element classElement = entry.getValue().get(0).getEnclosingElement();
            try {
                MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.get(classElement.asType()), "target");
                for (Element e : entry.getValue()) {
                    BindView annotation = e.getAnnotation(BindView.class);
                    int id = annotation.viewId();
                    builder.addStatement("target." + e.getSimpleName() + " = target.findViewById($L)", id);
                }
                MethodSpec method = builder.build();
                TypeSpec binder = createClass(getClassName(entry.getKey()), method);
                JavaFile javaFile = JavaFile.builder(getPackage(entry.getKey()), binder).build();
                javaFile.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        annotatedElementMap.clear();

        return true;
    }

    private TypeSpec createClass(String className, MethodSpec constructor) {
        return TypeSpec.classBuilder(className + "_ViewBinder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructor)
                .build();
    }

    private String getPackage(String qualifier) {
        return qualifier.substring(0, qualifier.lastIndexOf("."));
    }

    private String getClassName(String qualifier) {
        return qualifier.substring(qualifier.lastIndexOf(".") + 1);
    }
}

