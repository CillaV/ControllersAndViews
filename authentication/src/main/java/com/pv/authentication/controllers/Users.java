package com.pv.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.pv.authentication.models.User;
import com.pv.authentication.services.UserService;


@Controller
public class Users {
	private final UserService userService;
    
    public Users(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	// if result has errors, return the registration page (don't worry about validations just now)
        // else, save the user in the database, save the user id in session, and redirect them to the /home route
    	if(result.hasErrors()) {
    		return "registrationPage.jsp";
    	}
    	else {
    		// create a variable to use to find id in session - creating an instance of User
    		User a = this.userService.registerUser(user);
    		// id needs to be saved to session - "user" is now our key
    		session.setAttribute("user", a.getId());
    		return "redirect:/home";
    	}
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        // if the user is authenticated, save their user id in session
        // else, add error messages and return the login page
    	// from service declare same type, based off service need email to authenticate
    	boolean isAuthenticated = this.userService.authenticateUser(email, password);
    	// if true
    	if(isAuthenticated) {
    		// to store in session declare instance and search by email provided
    		User validUser = this.userService.findByEmail(email);
    		// set to session and use built in get id since it is an instance of User
    		session.setAttribute("user", validUser.getId());
    		return "redirect:/home";
    	}
    	else {
    		// model on page is error; because writing string needs to be in quotes
    		model.addAttribute("error", "Invalid Login. Try Again.");
    		return "loginPage.jsp";
    	}
    	
    	
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    	Long id =  (Long) session.getAttribute("user");
    	User b = this.userService.findUserById(id);
//    	System.out.println(id);
    	model.addAttribute("user", b );
    	return "homePage.jsp";
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
        // redirect to login page
    	session.invalidate();
    	return "redirect:/login";
    }
}
