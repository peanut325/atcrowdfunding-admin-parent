package org.fall.service.api;

import org.springframework.security.core.userdetails.UserDetails;

public interface CrowdUserDetailsService {
    public UserDetails loadUserByUsername(String username);
}
