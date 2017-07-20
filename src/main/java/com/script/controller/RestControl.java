package com.script.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.script.Scripting;

@RestController
public class RestControl {
	
	@RequestMapping(value = "/codeLine/{input:.+}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Map<String, Object> codeLine(@PathVariable String input) {
		System.out.println(input);
		return new Scripting().start(input);
		
	}
	
	@RequestMapping(value = "/codeMany/{input:.+}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Map<String, Object> codeMany(@MatrixVariable String... input) {
		String passIn = "";
		for (String str : input) {
			passIn += str + "; ";
		}
		return new Scripting().start(passIn);
		
	}
}
