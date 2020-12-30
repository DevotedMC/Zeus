package com.github.maxopoly.zeus.plugin.internal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public abstract class AbstractAnnotationProcessor<A extends Annotation, P> extends AbstractProcessor {

	protected abstract Class<A> getAnnotationClass();

	protected abstract Class<P> getLoadedObjClass();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return false;
		}
		Set<String> pluginClasses = new HashSet<>();
		Elements elements = processingEnv.getElementUtils();
		for (Element element : roundEnv.getElementsAnnotatedWith(getAnnotationClass())) {
			A annot = element.getAnnotation(getAnnotationClass());
			if (annot == null) {
				// ????
				continue;
			}
			if (!validConstructor(element)) {
				return true;
			}
			pluginClasses.add(elements.getBinaryName((TypeElement) element).toString());
		}
		// load existing
		Filer filer = processingEnv.getFiler();
		try {
			FileObject file = filer.getResource(StandardLocation.CLASS_OUTPUT, "",
					"META-INF/services/" + getLoadedObjClass().getName());
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(file.openInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					pluginClasses.add(line);
				}
			}
		} catch (NoSuchFileException | FileNotFoundException x) {
			// doesn't exist, that's fine
		} catch (IOException x) {
			processingEnv.getMessager().printMessage(Kind.ERROR,
					"Failed to load existing service definition files: " + x);
		}

		// write back
		try {
			FileObject f = filer.createResource(StandardLocation.CLASS_OUTPUT, "",
					"META-INF/services/" + getLoadedObjClass().getName());
			PrintWriter printWriter = new PrintWriter(
					new OutputStreamWriter(f.openOutputStream(), StandardCharsets.UTF_8));
			processingEnv.getMessager().printMessage(Kind.NOTE, "Writing " + f.getName());
			for (String value : pluginClasses) {
				printWriter.println(value);
			}
			printWriter.close();
		} catch (IOException x) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to write service definition files: " + x);
		}
		return true;
	}

	private boolean validConstructor(Element el) {
		for (Element subelement : el.getEnclosedElements()) {
			if (subelement.getKind() == ElementKind.CONSTRUCTOR) {
				if (!subelement.getModifiers().contains(Modifier.PUBLIC)) {
					processingEnv.getMessager().printMessage(Kind.ERROR,
							"Invalid constructor visibility for object " + subelement.toString());
					return false;
				}
				ExecutableType mirror = (ExecutableType) subelement.asType();
				if (!mirror.getParameterTypes().isEmpty()) {
					processingEnv.getMessager().printMessage(Kind.ERROR,
							"Invalid constructor for object, taking arguments is not allowed: "
									+ subelement.toString());
					return false;
				}
			}
		}
		return true;
	}
}
