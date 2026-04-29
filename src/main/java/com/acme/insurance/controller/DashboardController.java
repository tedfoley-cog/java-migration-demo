package com.acme.insurance.controller;

import com.acme.insurance.service.ClaimService;
import com.acme.insurance.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClaimService claimService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long activePolicies = policyService.countByStatus("ACTIVE");
        long draftPolicies = policyService.countByStatus("DRAFT");
        long cancelledPolicies = policyService.countByStatus("CANCELLED");

        BigDecimal totalApproved = claimService.getTotalApprovedAmount();
        List<Map<String, Object>> claimStats = claimService.getClaimStatsByStatus();

        model.addAttribute("activePolicies", activePolicies);
        model.addAttribute("draftPolicies", draftPolicies);
        model.addAttribute("cancelledPolicies", cancelledPolicies);
        model.addAttribute("totalApproved", totalApproved);
        model.addAttribute("claimStats", claimStats);
        model.addAttribute("recentPolicies", policyService.getAllPolicies());

        return "dashboard";
    }
}
