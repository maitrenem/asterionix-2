package com.asterionix.controllers;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
	@RequestMapping("/csrf1")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
