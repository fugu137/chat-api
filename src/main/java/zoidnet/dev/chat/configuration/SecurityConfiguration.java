package zoidnet.dev.chat.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import zoidnet.dev.chat.controller.dto.PrincipalDto;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final String allowedOrigins;

    private final UserDetailsService userDetailsService;


    public SecurityConfiguration(@Value("${cors.allowed.origins}") String allowedOrigins, UserDetailsService userDetailsService) {
        this.allowedOrigins = allowedOrigins;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(singletonList("*"));
        configuration.setAllowedHeaders(singletonList("*"));
        configuration.setExposedHeaders(singletonList("Location"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler())
                )
                .cors(withDefaults())
                .authorizeHttpRequests(authorise -> authorise
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/principal").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                );

        return httpSecurity.build();
    }

    private CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler() {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);
        return requestHandler;
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            PrincipalDto principalDto = new PrincipalDto(authentication.getName(), authentication.getAuthorities());
            String content = new ObjectMapper().writeValueAsString(principalDto);
            response.getWriter().write(content);
        };
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

}