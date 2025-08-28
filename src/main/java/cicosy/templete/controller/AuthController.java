package cicosy.templete.controller;

import cicosy.templete.domain.User;
import cicosy.templete.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) { 
        this.userService = userService; 
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) { 
            model.addAttribute("error", "Invalid username or password"); 
        }
        if (logout != null) { 
            model.addAttribute("message", "You have been logged out successfully"); 
        }
        return "login";
    }

    @PostMapping("/login-type-selection")
    public String selectLoginType(@RequestParam("loginType") String loginType,
                                  @RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                return "redirect:/login";
            }
            
            // Store login type preference in session
            session.setAttribute("selectedLoginType", loginType);
            
            if ("DEPARTMENT".equals(loginType) && user.getRole() != User.Role.HOD) {
                redirectAttributes.addFlashAttribute("error", "Only HODs can login as department representatives");
                return "redirect:/login";
            }
            
            // Redirect to Spring Security's login processing
            return "redirect:/login?username=" + username;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Login failed: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("user") User user,
                                BindingResult bindingResult,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) { 
            return "signup"; 
        }
        
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "signup";
        }
        
        try {
            userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }
}