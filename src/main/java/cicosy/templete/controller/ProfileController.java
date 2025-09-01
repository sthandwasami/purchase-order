package cicosy.templete.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("info", "Profile management is coming soon!");
        return "redirect:/dashboard";
    }

    @GetMapping("/settings")
    public String settings(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("info", "Settings page is coming soon!");
        return "redirect:/dashboard";
    }


}