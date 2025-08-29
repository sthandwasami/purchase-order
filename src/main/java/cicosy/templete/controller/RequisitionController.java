// Updated RequisitionController.java
package cicosy.templete.controller;

import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import cicosy.templete.repository.DepartmentRepository;
import cicosy.templete.service.NotificationService;
import cicosy.templete.service.RequisitionService;
import cicosy.templete.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/requisitions")
public class RequisitionController {

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String listRequisitions(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Requisition> requisitions;
        
        switch (user.getRole()) {
            case ADMIN:
                requisitions = requisitionService.findAll();
                break;
            case HOD:
                requisitions = requisitionService.findRequisitionsForHod(user);
                break;
            default:
                requisitions = requisitionService.findRequisitionsForUser(user);
        }
        
        model.addAttribute("requisitions", requisitions);
        model.addAttribute("user", user);
        return "requisitions/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('USER', 'HOD')")
    public String showRequisitionForm(Model model) {
        Requisition requisition = new Requisition();
        requisition.setItems(new java.util.ArrayList<>(java.util.Collections.singletonList(new cicosy.templete.domain.RequisitionItem())));
        model.addAttribute("requisition", requisition);
        model.addAttribute("departments", departmentRepository.findAll());
        return "requisitions/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'HOD')")
    public String createRequisition(@ModelAttribute Requisition requisition, 
                                  Principal principal, 
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            requisition.setUser(user);
            requisition.setStatus(Requisition.Status.PENDING_ADMIN_APPROVAL);
            
            // Set user's department if not specified and user has a department
            if (requisition.getDepartment() == null && user.getDepartment() != null) {
                requisition.setDepartment(user.getDepartment());
            }
            
            if (requisition.getItems() != null) {
                for (cicosy.templete.domain.RequisitionItem item : requisition.getItems()) {
                    item.setRequisition(requisition);
                }
            }
            
            Requisition savedRequisition = requisitionService.createRequisition(requisition);
            redirectAttributes.addFlashAttribute("success", 
                "Requisition #" + savedRequisition.getId() + " created successfully and sent for approval!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating requisition: " + e.getMessage());
        }
        
        return "redirect:/requisitions";
    }

    @GetMapping("/new-walk-in")
    @PreAuthorize("hasRole('HOD')")
    public String showWalkInRequisitionForm(Model model) {
        Requisition requisition = new Requisition();
        requisition.setItems(new java.util.ArrayList<>(java.util.Collections.singletonList(new cicosy.templete.domain.RequisitionItem())));
        model.addAttribute("requisition", requisition);
        model.addAttribute("departments", departmentRepository.findAll());
        return "requisitions/walk-in-form";
    }

    @PostMapping("/walk-in")
    @PreAuthorize("hasRole('HOD')")
    public String createWalkInRequisition(@ModelAttribute Requisition requisition, 
                                        Principal principal, 
                                        RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            requisition.setUser(user);
            requisition.setStatus(Requisition.Status.PENDING_ADMIN_APPROVAL);
            requisition.setWalkIn(true);
            
            if (requisition.getItems() != null) {
                for (cicosy.templete.domain.RequisitionItem item : requisition.getItems()) {
                    item.setRequisition(requisition);
                }
            }
            
            Requisition savedRequisition = requisitionService.createRequisition(requisition);
            redirectAttributes.addFlashAttribute("success", 
                "Walk-in requisition #" + savedRequisition.getId() + " created successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating walk-in requisition: " + e.getMessage());
        }
        
        return "redirect:/requisitions";
    }

    @GetMapping("/{id}")
    public String viewRequisition(@PathVariable Long id, Model model) {
        model.addAttribute("requisition", requisitionService.findById(id).orElse(null));
        return "requisitions/view";
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveRequisition(@PathVariable Long id, 
                                   Principal principal, 
                                   RedirectAttributes redirectAttributes) {
        try {
            User approver = userService.findByUsername(principal.getName());
            Requisition approved = requisitionService.approveRequisition(id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Requisition #" + approved.getId() + " approved successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error approving requisition: " + e.getMessage());
        }
        
        return "redirect:/requisitions";
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectRequisition(@PathVariable Long id, 
                                  @RequestParam String reason, 
                                  Principal principal, 
                                  RedirectAttributes redirectAttributes) {
        try {
            User rejector = userService.findByUsername(principal.getName());
            Requisition rejected = requisitionService.rejectRequisition(id, reason);
            
            redirectAttributes.addFlashAttribute("success", 
                "Requisition #" + rejected.getId() + " rejected successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error rejecting requisition: " + e.getMessage());
        }
        
        return "redirect:/requisitions";
    }

    @PostMapping("/consolidate")
    @PreAuthorize("hasRole('HOD')")
    public String consolidate(RedirectAttributes redirectAttributes) {
        try {
            requisitionService.consolidateRequisitions();
            redirectAttributes.addFlashAttribute("success", "Requisitions consolidated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error consolidating requisitions: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }

    @GetMapping("/approved")
    @PreAuthorize("hasRole('ADMIN')")
    public String listApprovedRequisitions(Model model) {
        model.addAttribute("requisitions", requisitionService.findAll().stream()
                .filter(r -> r.getStatus() == Requisition.Status.APPROVED_BY_ADMIN)
                .collect(java.util.stream.Collectors.toList()));
        return "requisitions/list";
    }

    @GetMapping("/rejected")
    @PreAuthorize("hasRole('ADMIN')")
    public String listRejectedRequisitions(Model model) {
        model.addAttribute("requisitions", requisitionService.findAll().stream()
                .filter(r -> r.getStatus() == Requisition.Status.REJECTED_BY_ADMIN)
                .collect(java.util.stream.Collectors.toList()));
        return "requisitions/list";
    }
    
    @GetMapping("/budget-review/{id}")
    @PreAuthorize("hasRole('APPROVER')")
    public String showBudgetReview(@PathVariable Long id, Model model, Principal principal) {
        User approver = userService.findByUsername(principal.getName());
        Requisition requisition = requisitionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));
        
        // Verify requisition is awaiting budget approval
        if (requisition.getStatus() != Requisition.Status.AWAITING_PO_APPROVAL) {
            throw new RuntimeException("Requisition is not awaiting budget approval");
        }
        
        model.addAttribute("requisition", requisition);
        // Add budget information for the department
        // model.addAttribute("budgetInfo", budgetService.getBudgetInfoForDepartment(requisition.getDepartment()));
        
        return "requisitions/budget-review";
    }
    
    @PostMapping("/budget-review/{id}")
    @PreAuthorize("hasRole('APPROVER')")
    public String processBudgetReview(@PathVariable Long id,
                                     @RequestParam String decision,
                                     @RequestParam(required = false) String reason,
                                     @RequestParam(required = false) String comments,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        User approver = userService.findByUsername(principal.getName());
        
        try {
            if ("APPROVE".equals(decision)) {
                Requisition approved = requisitionService.approveBudget(id, approver, comments);
                redirectAttributes.addFlashAttribute("success", 
                    "Budget approved for requisition #" + approved.getId() + ". HOD and requester have been notified.");
            } else if ("REJECT".equals(decision)) {
                if (reason == null || reason.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Rejection reason is required.");
                    return "redirect:/requisitions/budget-review/" + id;
                }
                Requisition rejected = requisitionService.rejectBudget(id, reason.trim(), approver, comments);
                redirectAttributes.addFlashAttribute("success", 
                    "Budget rejected for requisition #" + rejected.getId() + ". HOD and requester have been notified.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to process budget review: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('APPROVER')")
    public String listPendingRequisitions(Model model, Principal principal) {
        User approver = userService.findByUsername(principal.getName());
        List<Requisition> pendingRequisitions = requisitionService.findRequisitionsAwaitingBudgetApproval();
        
        model.addAttribute("pendingRequisitions", pendingRequisitions);
        model.addAttribute("user", approver);
        
        return "requisitions/pending-list";
    }
    
    @GetMapping("/approved-list")
    @PreAuthorize("hasRole('APPROVER')")
    public String listApprovedRequisitions(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("info", "Approved requisitions view is coming soon!");
        return "redirect:/dashboard";
    }
    
    @GetMapping("/send-to-procurement/{id}")
    @PreAuthorize("hasRole('APPROVER')")
    public String sendToProcurement(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            User approver = userService.findByUsername(principal.getName());
            Requisition requisition = requisitionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));
            
            // Update status to indicate it's been sent to procurement
            requisition.setStatus(Requisition.Status.SENT_TO_PROCUREMENT);
            requisitionService.save(requisition);
            
            redirectAttributes.addFlashAttribute("success", 
                "Requisition #" + id + " has been forwarded to Procurement Department for purchase order creation.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send to procurement: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}