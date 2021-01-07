package com.webprogramming.restmoviesapi;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
	
	private UserService userService;
 
    @Autowired
    public UserController(UserService userService) {
		
    	this.userService = userService;
    }

	// Return registration form template
	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user){
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	// Process registration form input data
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView processRegistrationForm(ModelAndView modelAndView, User user, BindingResult bindingResult) {
				
		// Lookup user in database by e-mail
		User userExists = userService.findByEmail(user.getEmail());
		
		//If a user exists with this email
		if (userExists != null) {
			modelAndView.addObject("errorMessage", "Oops!  There is already a user registered with the email provided.");
			bindingResult.reject("email");
		}
		
		//If no user exists with this email
		if (!bindingResult.hasErrors()) {	
			// new user so we create user and send confirmation e-mail		        
		    userService.saveUser(user);			
			modelAndView.setViewName("redirect:/login");
		}
			
		return modelAndView;
	}
	
	// Return login form template
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public ModelAndView showLoginPage(ModelAndView modelAndView, User user){
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	// Process login form input data
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView processLoginForm(ModelAndView modelAndView, User user, BindingResult bindingResult, HttpServletRequest request) {
		
		// Lookup user in database by e-mail
		User userExists = userService.findByEmail(user.getEmail());
			
		//If a user doesn't exists with this email
		if (userExists == null) {
			modelAndView.addObject("errorMessage", "Oops!  Wrong username or password.");
			bindingResult.reject("email");
		} else {
			//If a user exists with this email, but password doesn't match
			if(!userExists.getPassword().equals(user.getPassword())) {
				modelAndView.addObject("errorMessage", "Oops!  Wrong username or password.");
				bindingResult.reject("password");
			}		
			
			//If a user exists with this email and password
			if (!bindingResult.hasErrors()) { 				
				// existent user so we are logging him in.	
				request.getSession().setAttribute("user",userExists);
				modelAndView.setViewName("redirect:/welcome");
			}	
		}
		return modelAndView;
	}
	
	// Return welcome (search) form template
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public ModelAndView showWelcomePage(ModelAndView modelAndView, HttpServletRequest request, User user){
		User sessionUser = (User)request.getSession().getAttribute("user");
		//Send to front-end the email, for personalization purposes
		modelAndView.addObject("backendEmail", sessionUser.getEmail());
		modelAndView.setViewName("welcome");
		
		return modelAndView;
	}
	
	// Process search form input data
	@RequestMapping(value = "/welcome", method = RequestMethod.POST)
	public ModelAndView processTitlesForm(ModelAndView modelAndView, @RequestParam(value = "title") String justSavedTitle, User user, HttpServletRequest request) {
		User sessionUser = (User)request.getSession().getAttribute("user");
		
		// Lookup user in database by e-mail
		User databaseUser = userService.findByEmail(sessionUser.getEmail());
		
		String alreadySavedTitles = databaseUser.getTitles();
		justSavedTitle = justSavedTitle.replace("Save ","");
		if(alreadySavedTitles==null) {
			databaseUser.setTitles(justSavedTitle);
		} else {
			databaseUser.setTitles(alreadySavedTitles+","+justSavedTitle);
		}
		
		//Update the user saved in session with the new movie title
		request.getSession().removeAttribute("user");
		request.getSession().setAttribute("user",databaseUser);
		
		//Update the user in DB with the new movie title
		userService.saveUser(databaseUser);	
		
		//Send to front-end the email, for personalization purposes
		modelAndView.addObject("backendEmail", databaseUser.getEmail());
		
		return modelAndView;
	}
	
	// Return bookmarks form template
	@RequestMapping(value="/bookmarks", method = RequestMethod.GET)
	public ModelAndView showBookmarksPage(ModelAndView modelAndView, HttpServletRequest request, User user){
		User sessionUser = (User)request.getSession().getAttribute("user");
		
		//Send to front-end the movie titles
		modelAndView.addObject("backendTitles", sessionUser.getTitles());
		
		//Send to front-end the email, for personalization purposes
		modelAndView.addObject("backendEmail", sessionUser.getEmail());
		
		modelAndView.setViewName("my-bookmarks");
		return modelAndView;
	}
}