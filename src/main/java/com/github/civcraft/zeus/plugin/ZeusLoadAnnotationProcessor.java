package com.github.civcraft.zeus.plugin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.github.civcraft.zeus.plugin.ZeusLoad")
public class ZeusLoadAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return false;
		}
		Set<String> pluginClasses = new HashSet<>();
		Elements elements = processingEnv.getElementUtils();
		for (Element element : roundEnv.getElementsAnnotatedWith(ZeusLoad.class)) {
			ZeusLoad annot = element.getAnnotation(ZeusLoad.class);
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
					"META-INF/services/" + ZeusPlugin.class.getName());
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
					"META-INF/services/" + ZeusPlugin.class.getName());
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
							"Invalid constructor visibility for plugin " + subelement.toString());
					return false;
				}
				ExecutableType mirror = (ExecutableType) subelement.asType();
				if (!mirror.getParameterTypes().isEmpty()) {
					processingEnv.getMessager().printMessage(Kind.ERROR,
							"Invalid constructor for plugin, taking arguments is not allowed: "
									+ subelement.toString());
					return false;
				}
			}
		}
		return true;
	}
}
