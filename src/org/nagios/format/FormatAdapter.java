package org.nagios.format;

import java.io.Serializable;

import javax.management.openmbean.CompositeDataSupport;

public abstract class FormatAdapter {
	
	protected String property;
	
	public static FormatAdapter getInstance(Object originalValue, String property) {
		if (originalValue instanceof CompositeDataSupport) {
			return new CompositeSuportAdapter(originalValue).withProperty(property);
		} else if (property != null) {
			return new JsonAdapter(originalValue).withProperty(property);
		} else {
			return new SimpleAdapter(originalValue);
		}
	}
	
	public FormatAdapter withProperty(String property) {
		this.property = property;
		return this;
	}
	
	public abstract Serializable getValue();
}
