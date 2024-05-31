package com.example.demo.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UserRegistrationEntity;
import com.example.demo.repo.IUserRegistratinRepo;
import com.example.demo.request.ActivateAccountDTo;
import com.example.demo.request.Login;
import com.example.demo.request.UserRegistrationRequestDTO;

import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
@Service
public class UserManagementService implements IUserRegistration {
	private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String NUMBERS = "0123456789";
	private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+";

	@Autowired
	private IUserRegistratinRepo repo;
	@Autowired
	private JavaMailSender emailSender;
	 @PersistenceContext
	    private EntityManager entityManager;

	@Override
	public boolean saveUser(UserRegistrationRequestDTO request) {
		UserRegistrationEntity entity = new UserRegistrationEntity();
		BeanUtils.copyProperties(request, entity);
		entity.setStatus("In-Active");
		entity.setPassword(generatePassword(6));
		UserRegistrationEntity save = repo.save(entity);
		// TODO: Sending email
		String FileName="TempPassword";
		String mailBody = this.mailBody(save.getUserName(), save.getPassword(),FileName);
		boolean sendSimpleMessage = this.sendSimpleMessage(save.getEmail(), "Temprory Password", mailBody);
		return save.getID() != null && sendSimpleMessage;

	}

	@Override
	public String forgetPassword(String email) {
		// TODO Auto-generated method stub
		UserRegistrationEntity byEmail = repo.findByEmail(email);
		if (byEmail == null) {
			return "Invalid email";
		} else {
			// TODO:send password through email
			String FileName="Temp_Password";
			String mailBody = this.mailBody(byEmail.getUserName(), byEmail.getPassword(),FileName);
			this.sendSimpleMessage(email, "Your Password", mailBody);
			return "Mail Sent to your email";
		}
		
	}

	@Override
	public String login(Login login) {
		// TODO Auto-generated method stub
		
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRegistrationEntity> query = cb.createQuery(UserRegistrationEntity.class);
        Root<UserRegistrationEntity> root = query.from(UserRegistrationEntity.class);

        Predicate emailPredicate = cb.equal(root.get("email"), login.getEmail());
        Predicate passwordPredicate = cb.equal(root.get("password"), login.getPassword());

        query.where(cb.and(emailPredicate, passwordPredicate));
        
        List<UserRegistrationEntity> resultList = entityManager.createQuery(query).getResultList();
        
		
		if (resultList.isEmpty()) {
			return "Invalid User creds";
		}
		UserRegistrationEntity userRegistrationEntity = resultList.get(0);
		if (userRegistrationEntity.isStatus().equalsIgnoreCase("ACTIVE")) {
			return "Scusses";
		} else {
			return "Account not activated";
		}

	}

	@Override
	public boolean acctivateAccount(ActivateAccountDTo acctivate) {
		// TODO Auto-generated method stub 
		UserRegistrationEntity entity = new UserRegistrationEntity();
		entity.setEmail(acctivate.getEmail());
		entity.setPassword(acctivate.getTemproaryPassword());
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRegistrationEntity> query = cb.createQuery(UserRegistrationEntity.class);
        Root<UserRegistrationEntity> root = query.from(UserRegistrationEntity.class);

        Predicate emailPredicate = cb.equal(root.get("email"), acctivate.getEmail());
        Predicate passwordPredicate = cb.equal(root.get("password"), acctivate.getTemproaryPassword());

        query.where(cb.and(emailPredicate, passwordPredicate));
        
        List<UserRegistrationEntity> resultList = entityManager.createQuery(query).getResultList();
        
      
		if (resultList.isEmpty()) {
			return false;
		}
		UserRegistrationEntity userRegistrationEntity = resultList.get(0);
		userRegistrationEntity.setPassword(acctivate.getConformPassword());
		userRegistrationEntity.setStatus("Active");
		repo.save(userRegistrationEntity);
		
		return true;

	}

	@Override
	public List<UserRegistrationRequestDTO> listUser() {
		List<UserRegistrationRequestDTO> response = new ArrayList();
		List<UserRegistrationEntity> all = repo.findAll();
		for (UserRegistrationEntity userRegistrationEntity : all) {
			UserRegistrationRequestDTO user = new UserRegistrationRequestDTO();
			BeanUtils.copyProperties(userRegistrationEntity, user);
			response.add(user);
		}
		return response;

	}

	@Override
	public UserRegistrationRequestDTO getByID(Integer userID) {
		Optional<UserRegistrationEntity> byId = repo.findById(userID);
		UserRegistrationRequestDTO user = new UserRegistrationRequestDTO();
		if (byId.isPresent()) {
			UserRegistrationEntity userRegistrationEntity = byId.get();
			System.out.println("userRegistrationEntity"+userRegistrationEntity.getStatus());
			BeanUtils.copyProperties(userRegistrationEntity, user);	
			System.out.println("userRegistrationEntity"+user);

		}
		return user;
	}

	@Override
	public Boolean deleteById(Integer userID) {
		try {
			repo.deleteById(userID);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String accStatus) {
		// TODO Auto-generated method stub
		Optional<UserRegistrationEntity> byId = repo.findById(userId);
		if (byId.isPresent()) {
			System.out.println("Inside the if++++"+byId.get()+""+ accStatus);
			UserRegistrationEntity userRegistrationEntity = byId.get();
			userRegistrationEntity.setStatus(accStatus);
			repo.save(userRegistrationEntity);
			return true;
		}

		return false;
	}

	private static String generatePassword(int length) {

		StringBuilder password = new StringBuilder();
		SecureRandom random = new SecureRandom();

		String allChars = LOWERCASE_CHARS + UPPERCASE_CHARS + NUMBERS + SPECIAL_CHARS;

		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(allChars.length());
			password.append(allChars.charAt(randomIndex));
		}

		return password.toString();
	}

	private boolean sendSimpleMessage(String to, String subject, String text) {
		boolean mailSent = false;
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text);
			emailSender.send(message);
			mailSent = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mailSent;
	}

	private String mailBody(String fullName, String password,String fileName) {
		String FileName = fileName;
		String url = "";
		String mailBody = null;

		try {
			System.out.println("Pringting file name++++"+FileName);
	        String fileName1 = "C:/MiniProject/04_UserLogin_mini_project/src/main/resources/TempPassword.txt";
	        BufferedReader br = new BufferedReader(new FileReader(fileName1));
			//FileReader read = new FileReader(fileName1);
			BufferedReader bf = new BufferedReader(br);
			StringBuffer sb = new StringBuffer();
			String line = bf.readLine();
			if (line != null) {
				sb.append(line);
				line = bf.readLine();
			}
			mailBody = sb.toString();
			mailBody.replace("{FULLNAME}", "fullName");
			mailBody.replace("{PASSWORD}", "password");
			mailBody.replace("{URL}", "url");
			bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mailBody;
	}

}
