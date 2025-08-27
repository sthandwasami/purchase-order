package cicosy.templete.controller;

import cicosy.templete.domain.User;
import cicosy.templete.service.PurchaseOrderService;
import cicosy.templete.service.RequisitionService;
import cicosy.templete.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        switch (user.getRole()) {
            case HOD:
                model.addAttribute("departmentRequisitions", requisitionService.findRequisitionsForHod(user));
                model.addAttribute("myRequisitions", requisitionService.findRequisitionsForUser(user));
                break;
            case APPROVER:
            case BUYER:
                model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
                break;
            default:
                model.addAttribute("requisitions", requisitionService.findRequisitionsForUser(user));
                break;
        }

        return "dashboard";
    }
}


