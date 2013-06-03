package org.nagios.format;

import java.io.Serializable;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;

public class CompositeSuportAdapter extends FormatAdapter {

	private CompositeDataSupport composite;

	public CompositeSuportAdapter(Object originalValue) {
		composite = (CompositeDataSupport) originalValue;
	}

	@Override
	public Serializable getValue() {
		return composite.get(property).toString();
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		CompositeType type = composite.getCompositeType();
		out.append('{');
		for (String key : type.keySet()) {
			if (composite.containsKey(key)) {
				out.append(key + '=' + composite.get(key));
				out.append(';');
			}
		}
		out.append('}');
		return out.toString();
	}

}
