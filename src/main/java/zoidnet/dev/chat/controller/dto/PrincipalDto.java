package zoidnet.dev.chat.controller.dto;

import org.springframework.security.core.GrantedAuthority;
import zoidnet.dev.chat.model.Authority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class PrincipalDto {

    private final String username;

    private final Set<Authority> authorities;


    public PrincipalDto(String username, Collection<? extends GrantedAuthority> grantedAuthorities) {
        this.username = username;
        this.authorities = new HashSet<>();

        grantedAuthorities.forEach(grantedAuthority -> {
            Authority authority = Authority.valueOf(grantedAuthority.getAuthority());
            this.authorities.add(authority);
        });
    }

    public String getUsername() {
        return username;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }
}
