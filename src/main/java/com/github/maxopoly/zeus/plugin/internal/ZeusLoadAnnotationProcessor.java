package com.github.maxopoly.zeus.plugin.internal;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.github.maxopoly.zeus.plugin.ZeusLoad;
import com.github.maxopoly.zeus.plugin.ZeusPlugin;

@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.github.civcraft.zeus.plugin.ZeusLoad")
public class ZeusLoadAnnotationProcessor extends AbstractAnnotationProcessor<ZeusLoad, ZeusPlugin> {

	@Override
	protected Class<ZeusLoad> getAnnotationClass() {
		return ZeusLoad.class;
	}

	@Override
	protected Class<ZeusPlugin> getLoadedObjClass() {
		return ZeusPlugin.class;
	}

}
