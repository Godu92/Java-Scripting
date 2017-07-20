package com.script.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rule {
	private List<Condition> conditions;
	
	private Rule.eventType eventType;
	
	private String name;
	
	private String consequent;
	
	private String dialect;
	
	public String getDialect() {
		
		return dialect;
	}
	
	public void setDialect(String dialect) {
		
		this.dialect = dialect;
	}
	
	public String getName() {
		
		return name;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getConsequent() {
		
		return consequent;
	}
	
	public void setConsequent(String consequent) {
		
		this.consequent = consequent;
	}
	
	public List<Condition> getConditions() {
		
		return conditions;
	}
	
	public void setConditions(List<Condition> cs) {
		
		this.conditions = cs;
	}
	
	public Rule.eventType getEventType() {
		
		return eventType;
	}
	
	public void setEventType(Rule.eventType re) {
		
		this.eventType = re;
	}
	
	public static Rule getRule(String name, Rule.eventType t,
			List<Condition> cs, String errorMessage) {
		
		Rule r = new Rule();
		r.setName(name);
		r.setEventType(t);
		r.setConditions(cs);
		r.setConsequent(errorMessage);
		return r;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb;
		sb = new StringBuilder();
		for (Condition condition : getConditions()) {
			
			String operator = null;
			
			switch (condition.getOperator()) {
			case EQUAL_TO:
				operator = "==";
				break;
			case NOT_EQUAL_TO:
				operator = "!=";
				break;
			case GREATER_THAN:
				operator = ">";
				break;
			case LESS_THAN:
				operator = "<";
				break;
			case GREATER_THAN_OR_EQUAL_TO:
				operator = ">=";
				break;
			case LESS_THAN_OR_EQUAL_TO:
				operator = "<=";
				break;
			}
			
			sb.append(condition.getField()).append(" ").append(operator)
					.append(" ");
			
			if (!(condition.getValue() instanceof String))
				sb.append(condition.getValue());
			else
				sb.append("'").append(condition.getValue()).append("'");
			
			sb.append(" && ");
		}
		
		return (sb + "").substring(0, (sb + "").length() - 4);
	}
	
	public enum eventType {
		
		TRANSACTION("TRANSACTION");
		
		private final String value;
		
		private static Map<String, Rule.eventType> constants =
				new HashMap<String, Rule.eventType>();
		
		static {
			for (Rule.eventType c : values())
				constants.put(c.value, c);
		}
		
		eventType(String value) {
			this.value = value;
		}
		
		public static Rule.eventType fromValue(String value) {
			
			Rule.eventType re = constants.get(value);
			if (re == null)
				throw new IllegalArgumentException(value);
			return re;
		}
		
	}
}
