package com.github.maxopoly.zeus.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.maxopoly.zeus.commands.ZeusCommand;
import org.apache.logging.log4j.Logger;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.plugin.event.ZeusListener;
import com.github.maxopoly.zeus.rabbit.incoming.RabbitRequest;

public abstract class ZeusPlugin {

	private File dataFolder;
	private ZeusPluginConfig config;
	private boolean running;
	protected Logger logger;

	boolean enable(Logger logger, File pluginsFolder) {
		this.logger = logger;
		this.running = true;
		this.dataFolder = new File(pluginsFolder, getName());
		this.config = new ZeusPluginConfig(logger, new File(dataFolder, "config.yml"));
		return onEnable();
	}

	void disable() {
		onDisable();
		this.running = false;
	}

	/**
	 * @return Is the plugin active
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Automatically called when the plugin is enabled
	 */
	public abstract boolean onEnable();

	/**
	 * Automatically called when the plugin is disabled
	 */
	public abstract void onDisable();

	protected ZeusPluginConfig getConfig() {
		return config;
	}

	protected void registerPluginListener(ZeusListener listener, ZeusListener... listeners) {
		ZeusMain.getInstance().getEventManager().registerListener(listener);
		for (ZeusListener lis : listeners) {
			ZeusMain.getInstance().getEventManager().registerListener(lis);
		}
	}
	
	protected void registerRabbitListener(RabbitRequest request, RabbitRequest ...rabbitRequests) {
		ZeusMain.getInstance().getRabbitGateway().getInputHandler().registerCommand(request);
		for (RabbitRequest lis : rabbitRequests) {
			ZeusMain.getInstance().getRabbitGateway().getInputHandler().registerCommand(lis);
		}
	}

	protected void registerCommand(ZeusCommand command, ZeusCommand... commands) {
		ZeusMain.getInstance().getCommandHandler().registerCommand(command);
		for (ZeusCommand com : commands) {
			ZeusMain.getInstance().getCommandHandler().registerCommand(com);
		}
	}

	/**
	 * @return Folder in which this plugins data (like its YAML config) is kept
	 */
	public File getDataFolder() {
		return dataFolder;
	}

	/**
	 * @return Optional description of the plugin as specified in its annotation
	 */
	public String getDescription() {
		ZeusLoad pluginAnnotation = getPluginAnnotation();
		if (pluginAnnotation == null) {
			return null;
		}
		return pluginAnnotation.description();
	}

	public List<String> getDependencies() {
		ZeusLoad pluginAnnotation = getPluginAnnotation();
		if (pluginAnnotation == null) {
			return Collections.emptyList();
		}
		String unsplit = pluginAnnotation.dependencies();
		if (unsplit == null || unsplit.isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.asList(unsplit.split(" "));
	}

	/**
	 * @return Unique identifying name
	 */
	public String getName() {
		ZeusLoad pluginAnnotation = getPluginAnnotation();
		if (pluginAnnotation == null) {
			return null;
		}
		return pluginAnnotation.name();
	}

	private ZeusLoad getPluginAnnotation() {
		Class<? extends ZeusPlugin> pluginClass = this.getClass();
		return pluginClass.getAnnotation(ZeusLoad.class);
	}

	/**
	 * @return Version of the plugin
	 */
	public String getVersion() {
		ZeusLoad pluginAnnotation = getPluginAnnotation();
		if (pluginAnnotation == null) {
			return null;
		}
		return pluginAnnotation.version();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ZeusPlugin)) {
			return false;
		}
		return ((ZeusPlugin) o).getName().equals(this.getName());
	}

}
