package com.asterionix.response;

import com.asterionix.main.AsteriskClient;

public class ErrorResponse extends AbstractResponse {

	private static final long serialVersionUID = 1L;
	private String message;
	
	public ErrorResponse(Object source) {
		super(source);
	}
	public void setMessage(String msg){
		this.message = msg;
	}
	@Override
	public String getMessage() {
		return this.message;
	}
	@Override
	public void onResponse(AsteriskClient target, AsteriskResponse response) {
		target.OnErrorResponse(response);
	}

}
