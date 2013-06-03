package org.nagios.properties.parsers;

import org.nagios.NagiosStatus;
import org.nagios.format.FormatAdapter;

public class LongParser implements TypeParser<Long> {

	@Override
	public Long parse(Object value) {
		if(value instanceof Number) {
			return ((Number)value).longValue();
		} else { 
			return Long.parseLong(value.toString());
		}
	}

	@Override
	public NagiosStatus check(FormatAdapter checkData, Long warning, Long critical, Long expected) {
		Long check = parse(checkData.getValue());
		if(compare( check, critical, warning<critical)){
			return NagiosStatus.CRITICAL;
		}else if (compare(check, warning, warning<critical)){
			return NagiosStatus.WARNING;
		}else if (expected == null || expected == check){
			return NagiosStatus.OK;
		} else {
			return NagiosStatus.UNKNOWN;
		}
	}
	
	private boolean compare(long checkData, long level, boolean more) {
		if(more){ 
			return checkData>=level;
		} else {
			return checkData<=level;
		}
	}
}
