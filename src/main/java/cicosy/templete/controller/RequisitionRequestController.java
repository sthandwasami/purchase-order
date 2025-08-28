package cicosy.templete.controller;

import cicosy.templete.domain.RequisitionRequest;
import cicosy.templete.domain.User;
import cicosy.templete.repository.DepartmentRepository;
import cicosy.templete.service.RequisitionRequestService;
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
@RequestMapping("/requests")
public class RequisitionRequestController {

    @Autowired
    private RequisitionRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public String listRequests(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<RequisitionRequest> requests;
        
        if (user.getRole() == User.Role.HOD) {
            requests = requestService.findDepartmentRequests(user);
            model.addAttribute("isHod", true);
        } else {
            requests = requestService.findRequestsForUser(user);
            model.addAttribute("isHod", false);
        }
        
        model.addAttribute("requests", requests);
        return "requisitions/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('USER', 'HOD')")
    public String showRequestForm(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        RequisitionRequest request = new RequisitionRequest();
        
        // Pre-populate department for regular users
        if (user.getRole() == User.Role.USER && user.getDepartment() != null) {
            request.setDepartment(user.getDepartment());
        }
        
        model.addAttribute("request", request);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("priorities", RequisitionRequest.Priority.values());
        return "requests/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'HOD')")
    public String createRequest(@ModelAttribute RequisitionRequest request, 
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        request.setUser(user);
        
        // Set department if not already set
        if (request.getDepartment() == null && user.getDepartment() != null) {
            request.setDepartment(user.getDepartment());
        }
        
        try {
            requestService.createRequest(request);
            redirectAttributes.addFlashAttribute("success", 
                "Request submitted successfully and sent to HOD for review.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to create request: " + e.getMessage());
        }
        
        return "redirect:/requests";
    }

    @GetMapping("/new-walk-in")
    @PreAuthorize("hasRole('HOD')")
    public String showWalkInRequestForm(Model model) {
        model.addAttribute("request", new RequisitionRequest());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("priorities", RequisitionRequest.Priority.values());
        return "requests/walk-in-form";
    }

    @PostMapping("/walk-in")
    @PreAuthorize("hasRole('HOD')")
    public String createWalkInRequest(@ModelAttribute RequisitionRequest request, 
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        User hod = userService.findByUsername(principal.getName());
        request.setUser(hod); // HOD is creating on behalf of someone
        
        // Set to HOD's department if not specified
        if (request.getDepartment() == null) {
            request.setDepartment(hod.getDepartment());
        }
        
        try {
            requestService.createRequest(request);
            redirectAttributes.addFlashAttribute("success", 
                "Walk-in request created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to create walk-in request: " + e.getMessage());
        }
        
        return "redirect:/requests";
    }

    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        RequisitionRequest request = requestService.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        // Check if user can view this request
        if (user.getRole() == User.Role.USER && !request.getUser().equals(user)) {
            throw new RuntimeException("Access denied");
        }
        
        if (user.getRole() == User.Role.HOD && !request.getDepartment().equals(user.getDepartment())) {
            throw new RuntimeException("Access denied");
        }
        
        model.addAttribute("request", request);
        model.addAttribute("canApprove", 
            user.getRole() == User.Role.HOD && 
            request.getStatus() == RequisitionRequest.Status.PENDING_HOD_REVIEW);
        
        return "requisitions/view-request";
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('HOD')")
    public String approveRequest(@PathVariable Long id, 
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        User hod = userService.findByUsername(principal.getName());
        
        try {
            requestService.approveRequest(id, hod);
            redirectAttributes.addFlashAttribute("success", "Request approved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve request: " + e.getMessage());
        }
        
        return "redirect:/requests";
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('HOD')")
    public String rejectRequest(@PathVariable Long id, 
                              @RequestParam String reason,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        User hod = userService.findByUsername(principal.getName());
        
        if (reason == null || reason.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rejection reason is required.");
            return "redirect:/requests/" + id;
        }
        
        try {
            requestService.rejectRequest(id, reason.trim(), hod);
            redirectAttributes.addFlashAttribute("success", "Request rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject request: " + e.getMessage());
        }
        
        return "redirect:/requests";
    }

    @PostMapping("/consolidate")
    @PreAuthorize("hasRole('HOD')")
    public String consolidateRequests(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            requestService.consolidateApprovedRequests();
            redirectAttributes.addFlashAttribute("success", 
                "Approved requests have been consolidated and sent for budget approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Consolidation failed: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
}