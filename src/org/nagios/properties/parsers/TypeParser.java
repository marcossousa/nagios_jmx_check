package org.nagios.properties.parsers;

import java.io.Serializable;

import org.nagios.NagiosStatus;
import org.nagios.format.FormatAdapter;

public interface TypeParser<T extends Serializable> {
	
	NagiosStatus check(FormatAdapter checkData, T warning, T critical, T expected);
	T parse(Object value);
	
}