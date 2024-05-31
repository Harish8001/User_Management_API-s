package com.example.demo.service;

import java.util.List;

import com.example.demo.request.ActivateAccountDTo;
import com.example.demo.request.Login;
import com.example.demo.request.UserRegistrationRequestDTO;

public interface IUserRegistration {

	public boolean saveUser(UserRegistrationRequestDTO request);

	public UserRegistrationRequestDTO getByID(Integer userID);

	public Boolean deleteById(Integer userID);

	public boolean changeAccountStatus(Integer userId, String accStatus);

	public String forgetPassword(String email);

	public String login(Login login);

	public boolean acctivateAccount(ActivateAccountDTo acctivate);

	public List<UserRegistrationRequestDTO> listUser();

}
