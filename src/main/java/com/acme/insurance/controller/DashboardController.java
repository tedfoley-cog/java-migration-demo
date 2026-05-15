package com.acme.insurance.controller;

import com.acme.insurance.service.ClaimService;
import com.acme.insurance.service.PolicyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PolicyService policyService;
    private final ClaimService claimService;

    public DashboardController(PolicyService policyService, ClaimService claimService) {
        this.policyService = policyService;
        this.claimService = claimService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var activePolicies = policyService.countByStatus("ACTIVE");
        var draftPolicies = policyService.countByStatus("DRAFT");
        var cancelledPolicies = policyService.countByStatus("CANCELLED");

        var totalApproved = claimService.getTotalApprovedAmount();
        var claimStats = claimService.getClaimStatsByStatus();

        model.addAttribute("activePolicies", activePolicies);
        model.addAttribute("draftPolicies", draftPolicies);
        model.addAttribute("cancelledPolicies", cancelledPolicies);
        model.addAttribute("totalApproved", totalApproved);
        model.addAttribute("claimStats", claimStats);
        model.addAttribute("recentPolicies", policyService.getAllPolicies());

        return "dashboard";
    }
}
