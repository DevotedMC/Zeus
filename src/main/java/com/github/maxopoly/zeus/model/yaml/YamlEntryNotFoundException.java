package com.github.maxopoly.zeus.model.yaml;

public class YamlEntryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4107653478684873242L;

	public YamlEntryNotFoundException() {
		super();
	}

	public YamlEntryNotFoundException(String msg) {
		super(msg);
	}

}
