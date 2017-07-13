package com.script.rule;

import java.util.HashMap;
import java.util.Map;

public class Condition {
	private String field;
	
	private Object value;
	
	private Condition.Operator operator;
	
	public String getField() {
		
		return field;
	}
	
	public void setField(String field) {
		
		this.field = field;
	}
	
	public Object getValue() {
		
		return value;
	}
	
	public void setValue(Object value) {
		
		this.value = value;
	}
	
	public Condition.Operator getOperator() {
		
		return operator;
	}
	
	public void setOperator(Condition.Operator o) {
		
		this.operator = o;
	}
	
	public enum Operator {
		NOT_EQUAL_TO("NOT_EQUAL_TO"), 
		EQUAL_TO("EQUAL_TO"),
		GREATER_THAN("GREATER_THAN"), 
		LESS_THAN("LESS_THAN"), 
		GREATER_THAN_OR_EQUAL_TO("GREATER_THAN_OR_EQUAL_TO"), 
		LESS_THAN_OR_EQUAL_TO("LESS_THAN_OR_EQUAL_TO");
		
		private final String value;
		
		private static Map<String, Operator> constants =
				new HashMap<String, Operator>();
		
		static {
			for (Condition.Operator co : values())
				constants.put(co.value, co);
		}
		
		Operator(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			
			return this.value;
		}
		
		public static Condition.Operator fromValue(String value) {
			
			Condition.Operator co = constants.get(value);
			if (co == null)
				throw new IllegalArgumentException(value);
			return co;
		}
	}
}
