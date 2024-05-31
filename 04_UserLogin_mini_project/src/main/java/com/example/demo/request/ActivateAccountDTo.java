package com.example.demo.request;

public class ActivateAccountDTo {

	private String email;
	private String temproaryPassword;
	private String newPassword;
	private String conformPassword;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTemproaryPassword() {
		return temproaryPassword;
	}

	public void setTemproaryPassword(String temproaryPassword) {
		this.temproaryPassword = temproaryPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConformPassword() {
		return conformPassword;
	}

	public void setConformPassword(String conformPassword) {
		this.conformPassword = conformPassword;
	}

}
