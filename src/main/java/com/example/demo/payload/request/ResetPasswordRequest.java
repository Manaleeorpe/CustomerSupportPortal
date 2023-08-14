package com.example.demo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String token;
    private String password;
	public ResetPasswordRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ResetPasswordRequest(String token, String password) {
		super();
		this.token = token;
		this.password = password;
	}
	@Override
	public String toString() {
		return "ResetPasswordRequest [token=" + token + ", password=" + password + "]";
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}