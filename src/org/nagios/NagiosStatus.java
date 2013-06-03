package org.nagios;

public enum NagiosStatus {
	OK("JMX OK"), WARNING ("JMX WARNING"), CRITICAL("JMX CRITICAL"), UNKNOWN("JMX UNKNOWN");
	
	private String status;
	
	NagiosStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status;
	}
}
