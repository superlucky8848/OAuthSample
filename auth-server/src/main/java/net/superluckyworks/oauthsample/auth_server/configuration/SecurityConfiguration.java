package net.superluckyworks.oauthsample.auth_server.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration 
{
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize ->authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()
                .requestMatchers("/", "/index.html").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .formLogin(formLogin -> formLogin
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    } 

    @Bean
    GrantedAuthoritiesMapper OAuth2UserAuthoritiesMapper() 
    {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority)
                {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    String email = oidcUserAuthority.getUserInfo().getEmail();
                    Set<GrantedAuthority> userAuthorities = load3rdPartyUserAuthorities(email);
                    mappedAuthorities.addAll(userAuthorities);
                }
                else if(authority instanceof OAuth2UserAuthority)
                {
                    OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
                    String email = (String) oauth2UserAuthority.getAttributes().get("email");
                    Set<GrantedAuthority> userAuthorities = load3rdPartyUserAuthorities(email);
                    mappedAuthorities.addAll(userAuthorities);
                }
                else
                {
                    mappedAuthorities.add(authority);
                }
            });

            System.out.println("Mapped Authorities: " + mappedAuthorities.toString());

            return mappedAuthorities;
        };
    }

    private Set<GrantedAuthority> load3rdPartyUserAuthorities(String email)
    {
        Set<GrantedAuthority> result = new HashSet<>();
        if(email.compareTo("mail.superlucky@gmail.com") == 0)
        {
            result.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            result.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        else
        {
            result.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return result;
    }
}