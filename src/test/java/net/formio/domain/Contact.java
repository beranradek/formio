package net.formio.domain;

import java.io.Serializable;

import net.formio.validation.constraints.AnyNotEmpty;

@AnyNotEmpty(fields = {"email", "phone"})
public class Contact implements Serializable {

	private static final long serialVersionUID = -3737080347468640583L;
	
	private String email;
	private String phone;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
