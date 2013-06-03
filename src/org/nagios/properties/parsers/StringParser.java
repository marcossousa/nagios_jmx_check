package org.nagios.properties.parsers;

import org.nagios.NagiosStatus;
import org.nagios.format.FormatAdapter;

public class StringParser implements TypeParser<String> {

	

	@Override
	public String parse(Object value) {
		return value.toString();
	}

	@Override
	public NagiosStatus check(FormatAdapter checkData, String warning, String critical, String expected) {
		String check = parse(checkData.getValue());
		
		if (expected != null && expected.indexOf(check) >= 0) {
			return NagiosStatus.OK;
		} else if (warning != null && warning.indexOf(check) >= 0) {
			return NagiosStatus.WARNING;
		} else if (critical != null && critical.indexOf(check) >= 0) {
			return NagiosStatus.CRITICAL;
		}
		return NagiosStatus.UNKNOWN;
	}
}