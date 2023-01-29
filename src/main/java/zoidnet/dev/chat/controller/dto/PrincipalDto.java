package zoidnet.dev.chat.controller.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public record PrincipalDto(String username, Collection<? extends GrantedAuthority> authorities) {}
