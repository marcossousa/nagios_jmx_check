package org.nagios.format;

import java.io.Serializable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonAdapter extends FormatAdapter{

	private String originalValue;
	private Serializable value;

	public JsonAdapter(Object originalValue) {
		this.originalValue = originalValue.toString();
	}
	
	public Serializable getValue() {
		if (value == null) {
			String[] properties = property.split(":");
			JsonParser parser = new JsonParser();
			value = findValueRecursive(parser.parse(originalValue).getAsJsonObject(), properties, 0);
		}
		
		return value;
	}

	private Serializable findValueRecursive(JsonElement jsonTree, String[] properties, int index) {
		if (jsonTree.isJsonObject()) {
			JsonElement e = jsonTree.getAsJsonObject().get(properties[index]);
			if (e == null) {
				throw new IllegalArgumentException("Attribute " + property + " not found in json "+ originalValue);
			} else if (e.isJsonObject() && index < properties.length) {
				return findValueRecursive(e, properties, ++index);
			} else {
				return e.getAsString();
			}
		}
		return jsonTree.getAsString();
	}
	
	@Override
	public String toString() {
		return originalValue;
	}
}
