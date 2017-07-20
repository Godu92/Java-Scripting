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
	public static String codeOutput = "";
	public static Map<String, Object> outMap = new HashMap<>();
	private static final String templateName = "template.drl";
	
	/* Modify this to take in the strings to run */
	public Map<String, Object> start(String input) {
		Outputer op = new Outputer();
		String code = input;
		
		Scanner scan;
		scan = new Scanner(System.in);
		List<String> codeIn = new ArrayList<>();
		if (code.trim().isEmpty()) {
			
			System.out.println("Please type the code you wish to run. "
					+ "\nType \"End\" on it's own line to submit code:");
			while (scan.hasNext()) {
				// Example:
				// System.out.println("Hello");
				code = scan.nextLine();
				if ("end".equalsIgnoreCase(code)) {
					if (code.trim().isEmpty()) {
						break;
					}
					break;
				} else {
					codeIn.add(code);
				}
				
			}
		}
		if (!code.contains(";")) {
			code += ";";
		}
		codeIn.add(code);
		
		String codeLine = "";
		for (String c : codeIn) {
			codeLine += c;
		}
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setValid(true);
		getStatelessKieSession(getTransactionValidationDrl(codeLine, op))
				.execute(orderEvent);
		scan.close();
		return outMap;
	}
	
	private static String getTransactionValidationDrl(String code,
			Outputer op) {
		
		List<Rule> rl = new ArrayList<>();
		
		rl.add(Rule
				.getRule("Always passes, runs code", Rule.eventType.TRANSACTION,
						Arrays.asList(Condition.getCondition(true,
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
			new Scripting().start(null);
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
			outMap = map;
			coc.start();
			outMap.put("{CODE}: \n\t", r.getConsequent());
			codeOutput = "{CODE}: \n\t" + r.getConsequent();
			System.out.println(codeOutput);
			
			op.setOutput(coc.stop());
		}
		String template = null;
		try {
			template = odc.compile(ruleMaps, Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(templateName));
		} catch (NullPointerException e) {
			template = odc.compile(ruleMaps,
					Thread.currentThread().getContextClassLoader()
							.getResourceAsStream("resources/" + templateName));
		}
		return template;
	}
	
}
