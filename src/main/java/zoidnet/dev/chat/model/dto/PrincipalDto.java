package zoidnet.dev.chat.model.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


public class PrincipalDto {

    private final String username;

    private final Collection<? extends GrantedAuthority> authorities;


    public PrincipalDto(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getAuthorities() {
        return authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

}
