package com.forgefit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GymController is disabled for now as it references models and repositories
 * that are not yet created (Program, Trainer, MembershipPlan, ContactMessage).
 * 
 * This will be re-enabled in Phase 4 when the full gym management system is implemented.
 * For the current login/authentication feature, gym info endpoints are not needed.
 */
@Component
@RequiredArgsConstructor
public class GymController {
    // Disabled: Gym management endpoints deferred to Phase 4
}