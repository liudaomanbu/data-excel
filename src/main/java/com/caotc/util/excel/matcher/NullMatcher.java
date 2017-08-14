package com.caotc.util.excel.matcher;

public class NullMatcher extends DataValueMatcher {
	@Override
	public boolean support(Object value) {
		return Boolean.TRUE;
	}

	@Override
	public boolean matches(Object value) {
		return value!=null;
	}
}
