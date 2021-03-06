package com.github.maxopoly.zeus.plugin.internal;

import javax.annotation.processing.SupportedAnnotationTypes;

import com.github.maxopoly.zeus.commands.ZCommand;
import com.github.maxopoly.zeus.commands.ZeusCommand;

@SupportedAnnotationTypes("com.github.maxopoly.zeus.commands.ZCommand")
public class ZCommandAnnotationProcessor extends AbstractAnnotationProcessor<ZCommand, ZeusCommand> {

	@Override
	protected Class<ZCommand> getAnnotationClass() {
		return ZCommand.class;
	}

	@Override
	protected Class<ZeusCommand> getLoadedObjClass() {
		return ZeusCommand.class;
	}

}
