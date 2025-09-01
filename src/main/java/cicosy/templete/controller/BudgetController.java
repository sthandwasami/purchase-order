package cicosy.templete.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @GetMapping("/overview")
    public String budgetOverview() {
        return "budget/overview";
    }

    @GetMapping("/reports")
    public String budgetReports() {
        return "budget/reports";
    }
}