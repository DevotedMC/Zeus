package com.github.civcraft.zeus.plugin;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.model.yaml.ConfigSection;
import com.github.civcraft.zeus.model.yaml.YAMLFileConfig;

public class ZeusPluginConfig extends YAMLFileConfig {

	public ZeusPluginConfig(Logger logger, File file) {
		super(logger, file);
	}

	public ConfigSection getConfig() {
		return config;
	}
}
