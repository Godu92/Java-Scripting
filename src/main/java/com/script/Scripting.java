package com.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.StatelessKieSession;

import com.script.event.OrderEvent;
import com.script.output.Outputer;
import com.script.rule.Condition;
import com.script.rule.Rule;
import com.script.util.ConsoleOutputCatcher;

/**
 * @author dougs
 *
 */
public class Scripting {
	
	private static final String templateName = "template.drl";
	
	/* Modify this to take in the strings to run */
	public void start() {
		Outputer op = new Outputer();
		String code = "";
		
		// List<String> arguments = Arrays.asList(args);
		/* Needs a lot more work */
		// Collect command line args, if any
		// if (args.length != 0) {
		// String line = "";
		// for (String s : arguments) {
		// line = "";
		// // Maybe recombine every string until run into ;
		// while (!s.contains(";")) {
		// line = s;
		// int i = arguments.indexOf(s);
		// s = arguments.get(++i);
		// line += " " + s;
		// if (s.contains(";")) {
		// break;
		// }
		// }
		//
		// System.out.println(line);
		// }
		// } else {
		Scanner scan;
		scan = new Scanner(System.in);
		List<String> codeIn = new ArrayList<>();
		System.out.println("Please type the code you wish to run. "
				+ "\nType \"End\" on it's own line to submit code:");
		while (scan.hasNext()) {
			// Example:
			// System.out.println("Hello");
			code = scan.nextLine();
			if ("end".equalsIgnoreCase(code)) {
				if (code.trim().isEmpty()) {
					System.exit(0);
				}
				break;
			} else {
				codeIn.add(code);
			}
			
		}
		String codeLine = "";
		for (String c : codeIn) {
			codeLine += c;
		}
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setValid(true);
		getStatelessKieSession(getTransactionValidationDrl(codeLine, op))
				.execute(orderEvent);
		scan.close();
		// }
	}
	
	private static String getTransactionValidationDrl(String code,
			Outputer op) {
		
		List<Rule> rl = new ArrayList<>();
		
		rl.add(getRule("Always passes, runs code", Rule.eventType.TRANSACTION,
				Arrays.asList(getCondition("valid",
						Condition.Operator.NOT_EQUAL_TO, false)),
				code));
		
		return getRuleTemplate(rl, op);
	}
	
	private static StatelessKieSession getStatelessKieSession(String drl) {
		
		KieServices ks = KieServices.Factory.get();
		
		KieFileSystem kieFileSystem = ks.newKieFileSystem();
		kieFileSystem.write(
				"src/main/resources/" + FilenameUtils.getBaseName(drl) + ".drl",
				drl);
		ks.newKieBuilder(kieFileSystem).buildAll();
		
		StatelessKieSession sks = null;
		
		try {
			sks = ks.newKieContainer(ks.getRepository().getDefaultReleaseId())
					.getKieBase().newStatelessKieSession();
		} catch (Exception e) {
			System.out.println("Hit error, retrying");
			new Scripting().start();
		}
		
		return sks;
	}
	
	private static String getRuleTemplate(List<Rule> rs, Outputer op) {
		
		ObjectDataCompiler odc = new ObjectDataCompiler();
		Collection<Map<String, Object>> ruleMaps = new ArrayList<>();
		
		Collections.reverse(rs);
		
		for (Rule r : rs) {
			Map<String, Object> map = new HashMap<>();
			map.put("ruleName", r.getName());
			map.put("transaction", OrderEvent.class.getName());
			map.put("condition", r);
			map.put("consequent", "System.out.println(\"\\n[RESULT]:\");"
					+ "\n\t"
					+ r.getConsequent());
			map.put("type", r.getDialect());
			ruleMaps.add(map);
			
			ConsoleOutputCatcher coc = new ConsoleOutputCatcher();
			
			coc.start();
			
			System.out.println("{CODE}: \n\t" + r.getConsequent());
			
			op.setOutput(coc.stop());
		}
		String object = null;
		try {
			object = odc.compile(ruleMaps, Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(templateName));
		} catch (NullPointerException e) {
			object = odc.compile(ruleMaps,
					Thread.currentThread().getContextClassLoader()
							.getResourceAsStream("resources/" + templateName));
		}
		return object;
	}
	
	private static Condition getCondition(String field, Condition.Operator o,
			Object value) {
		
		Condition c = new Condition();
		c.setField(field);
		c.setOperator(o);
		c.setValue(value);
		return c;
	}
	
	private static Rule getRule(String name, Rule.eventType t,
			List<Condition> cs, String errorMessage) {
		
		Rule r = new Rule();
		r.setName(name);
		r.setEventType(t);
		r.setConditions(cs);
		r.setConsequent(errorMessage);
		return r;
	}
}
