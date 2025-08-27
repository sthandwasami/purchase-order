package cicosy.templete.controller;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.User;
import cicosy.templete.service.PurchaseOrderService;
import cicosy.templete.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPROVER', 'BUYER')")
    public String listPurchaseOrders(Model model) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.findAll();
        model.addAttribute("purchaseOrders", purchaseOrders);
        return "purchase-orders/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPROVER', 'BUYER')")
    public String viewPurchaseOrder(@PathVariable Long id, Model model) {
        model.addAttribute("purchaseOrder", purchaseOrderService.findById(id).orElse(null));
        return "purchase-orders/view";
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('APPROVER')")
    public String approvePurchaseOrder(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        purchaseOrderService.approvePurchaseOrder(id, user);
        return "redirect:/purchase-orders";
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('APPROVER')")
    public String rejectPurchaseOrder(@PathVariable Long id, @RequestParam String reason, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        purchaseOrderService.rejectPurchaseOrder(id, reason, user);
        return "redirect:/purchase-orders";
    }
}
