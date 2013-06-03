package org.nagios.format;

import java.io.Serializable;

public class SimpleAdapter extends FormatAdapter {
	
	private Serializable value;

	public SimpleAdapter(Object originalValue) {
		value = (Serializable) originalValue;
	}

	@Override
	public Serializable getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
}
