package com.example.demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.request.ActivateAccountDTo;
import com.example.demo.request.Login;
import com.example.demo.request.UserRegistrationRequestDTO;
import com.example.demo.service.UserManagementService;

import io.swagger.annotations.Api;

@RestController
@Api(value = "APIs for managing resources")
public class UserManagementRest {

	@Autowired
	private UserManagementService service;

	@PostMapping("/Register")
	public ResponseEntity<String> getMethodName(@RequestBody UserRegistrationRequestDTO request) {
		boolean saveUser = service.saveUser(request);
		if (saveUser) {
			return new ResponseEntity<>("Registration Success", HttpStatus.OK);
		}
		return new ResponseEntity<>("Registration Success", HttpStatus.OK);
	}

	@GetMapping("/ListUser")
	public ResponseEntity<List<UserRegistrationRequestDTO>> listUser() {
		List<UserRegistrationRequestDTO> listUser = service.listUser();
		return new ResponseEntity<>(listUser, HttpStatus.OK);
	}

	@GetMapping("/ActivateAccount")
	public ResponseEntity<String> getMethodName(@RequestBody ActivateAccountDTo request) {
		
		System.out.println("printing in rest layer++++"+request.getTemproaryPassword()+" "+request.getNewPassword());
		boolean acctivateAccount = service.acctivateAccount(request);
		if (acctivateAccount) {
			return new ResponseEntity<>("Account successfully activated", HttpStatus.OK);
		}
		return new ResponseEntity<>("Account Failed to activated", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/login")
	public ResponseEntity<String> login(@RequestBody Login request) {
		String login = service.login(request);
		return new ResponseEntity<>(login, HttpStatus.OK);

	}

	@GetMapping("/forgot/{email}")
	public ResponseEntity<String> forgotPassword(@PathVariable String email) {
		String forgetPassword = service.forgetPassword(email);
		return new ResponseEntity<String>(forgetPassword, HttpStatus.OK);
	}

	@GetMapping("/delete/{userID}")
	public ResponseEntity<String> accountStatus(@PathVariable Integer userID) {
		Boolean deleteById = service.deleteById(userID);
		if (deleteById) {
			return new ResponseEntity<String>("User deleted", HttpStatus.OK);
		}
		return new ResponseEntity<>("NO User Found", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@GetMapping("/statusChange/{userID}/{status}")
	public ResponseEntity<String> chnageAccountState(@PathVariable Integer userID,@PathVariable  String status) {
		System.out.println("printing the ID"+userID);
		boolean changeAccountStatus = service.changeAccountStatus(userID, status);
		if (changeAccountStatus) {
			return new ResponseEntity<String>("User status changed", HttpStatus.OK);
		}
		return new ResponseEntity<>("NO User Found", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@GetMapping("/getUser/{userID}")
	public ResponseEntity<UserRegistrationRequestDTO> getUser(@PathVariable Integer userID) {
		UserRegistrationRequestDTO changeAccountStatus = service.getByID(userID);
		if (changeAccountStatus.getUserName() != null) {
			return new ResponseEntity<>(changeAccountStatus, HttpStatus.OK);
		}
		return new ResponseEntity<>(changeAccountStatus, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
