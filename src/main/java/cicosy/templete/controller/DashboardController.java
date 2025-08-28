package cicosy.templete.controller;

import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import cicosy.templete.service.BudgetService;
import cicosy.templete.service.PurchaseOrderService;
import cicosy.templete.service.RequisitionRequestService;
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
    private RequisitionRequestService requestService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    @Autowired
    private BudgetService budgetService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        switch (user.getRole()) {
            case ADMIN:
                // Admin sees all system overview
                model.addAttribute("allRequisitions", requisitionService.findAll());
                model.addAttribute("budgetSummary", budgetService.getDepartmentalBudgetSummary());
                break;
                
            case HOD:
                return handleHodDashboard(model, user);
                
            case APPROVER:
                // Approver sees requisitions pending budget approval with budget info
                model.addAttribute("pendingRequisitions", requisitionService.findRequisitionsAwaitingBudgetApproval());
                model.addAttribute("budgetSummary", budgetService.getAllCurrentBudgetStatus());
                model.addAttribute("totalBudgetUtilization", budgetService.getDepartmentalBudgetSummary());
                break;
                
            case BUYER:
                // Buyer sees approved requisitions ready for procurement
                model.addAttribute("approvedRequisitions", requisitionService.findApprovedRequisitions());
                model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
                break;
                
            default: // USER
                // Regular users see their own requests and requisitions
                model.addAttribute("myRequests", requestService.findRequestsForUser(user));
                model.addAttribute("myRequisitions", requisitionService.findRequisitionsForUser(user));
                break;
        }

        return "dashboard";
    }
    
    private String handleHodDashboard(Model model, User hod) {
        // Pending Reviews (User Requests)
        var pendingRequests = requestService.findPendingRequestsForHod(hod);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingRequestCount", pendingRequests.size());
        
        // My Requisitions (HOD Created)
        var myRequests = requestService.findRequestsForUser(hod);
        model.addAttribute("myRequests", myRequests);
        model.addAttribute("myRequestCount", myRequests.size());
        
        // Approved/Waiting (For PO Module) - These are approved by HOD, now waiting for budget approval
        var approvedRequests = requestService.findDepartmentRequests(hod).stream()
                .filter(r -> r.getStatus() == cicosy.templete.domain.RequisitionRequest.Status.APPROVED_BY_HOD)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("approvedRequests", approvedRequests);
        model.addAttribute("approvedRequestCount", approvedRequests.size());
        
        // Recent Activity
        model.addAttribute("recentActivity", requestService.findRecentDepartmentActivity(hod));
        
        // Department Stats
        var departmentRequests = requestService.findDepartmentRequests(hod);
        model.addAttribute("totalDepartmentRequests", departmentRequests.size());
        model.addAttribute("departmentName", hod.getDepartment().getName());
        
        // Feedback from Purchase Order approvals/rejections
        var departmentRequisitions = requisitionService.findRequisitionsForDepartment(hod.getDepartment());
        var recentFeedback = departmentRequisitions.stream()
                .filter(r -> r.getStatus() == Requisition.Status.PO_APPROVED ||
                           r.getStatus() == Requisition.Status.PO_REJECTED)
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("recentFeedback", recentFeedback);
        
        return "dashboards/hod";
    }
}