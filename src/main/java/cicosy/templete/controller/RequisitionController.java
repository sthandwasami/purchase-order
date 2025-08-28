package cicosy.templete.controller;

import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import cicosy.templete.repository.DepartmentRepository;
import cicosy.templete.service.RequisitionService;
import cicosy.templete.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public String listRequisitions(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Requisition> requisitions;
        if (user.getRole() == User.Role.HOD) {
            requisitions = requisitionService.findRequisitionsForHod(user);
        } else {
            requisitions = requisitionService.findRequisitionsForUser(user);
        }
        model.addAttribute("requisitions", requisitions);
        return "requisitions/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('USER', 'HOD')")
    public String showRequisitionForm(Model model) {
        model.addAttribute("requisition", new Requisition());
        model.addAttribute("departments", departmentRepository.findAll());
        return "requisitions/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'HOD')")
    public String createRequisition(@ModelAttribute Requisition requisition, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        requisition.setUser(user);
        requisition.setStatus(Requisition.Status.PENDING_ADMIN_APPROVAL);
        requisitionService.createRequisition(requisition);
        return "redirect:/requisitions";
    }

    @GetMapping("/new-walk-in")
    @PreAuthorize("hasRole('HOD')")
    public String showWalkInRequisitionForm(Model model) {
        model.addAttribute("requisition", new Requisition());
        model.addAttribute("departments", departmentRepository.findAll());
        return "requisitions/walk-in-form";
    }

    @PostMapping("/walk-in")
    @PreAuthorize("hasRole('HOD')")
    public String createWalkInRequisition(@ModelAttribute Requisition requisition, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        requisition.setUser(user);
        requisition.setStatus(Requisition.Status.PENDING_ADMIN_APPROVAL);
        requisition.setWalkIn(true); // Set the walk-in flag
        requisitionService.createRequisition(requisition);
        return "redirect:/requisitions";
    }

    @GetMapping("/{id}")
    public String viewRequisition(@PathVariable Long id, Model model) {
        model.addAttribute("requisition", requisitionService.findById(id).orElse(null));
        return "requisitions/view";
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveRequisition(@PathVariable Long id) {
        requisitionService.approveRequisition(id);
        return "redirect:/requisitions";
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectRequisition(@PathVariable Long id, @RequestParam String reason) {
        requisitionService.rejectRequisition(id, reason);
        return "redirect:/requisitions";
    }

    @PostMapping("/consolidate")
    @PreAuthorize("hasRole('HOD')")
    public String consolidate() {
        requisitionService.consolidateRequisitions();
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
}
