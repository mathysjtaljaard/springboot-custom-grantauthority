package dev.taljaard.authentication.config;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private static Function<String, SimpleGrantedAuthority> mapRolesFromStringToGrantAuth = (role) -> new SimpleGrantedAuthority(
      String.format(
          (StringUtils.startsWithIgnoreCase("ROLE_", role) ? "%s" : "ROLE_%s"), role).toUpperCase());


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity.oauth2Login().and()
        .authorizeRequests( auth -> {
          auth.mvcMatchers("/admin/joker").hasAnyRole("JOKER");
          auth.mvcMatchers("/admin/sudo").hasAnyRole("ADMIN");
          auth.mvcMatchers("/admin/wtf").hasAnyRole("WTF");
          auth.mvcMatchers("/admin/**").authenticated();
          auth.anyRequest().permitAll();
        })
        .logout(logout -> {
          logout.permitAll().logoutSuccessHandler(((request, response, authentication) -> {
            response.setStatus(HttpStatus.OK.value());
            response.sendRedirect("/admin/user");
          }));
        })
        .build();
  }

  @Bean
  public GrantedAuthoritiesMapper customAuthoritiesMapper() {
    return authorities ->

     authorities.stream().map(auth -> {

        if (auth instanceof OidcUserAuthority) {
          return (List<String>)((OidcUserAuthority) auth).getAttributes().get("dev.taljaard.roles");
        }
        return List.of(auth.getAuthority());
      }).flatMap(List::stream)
          .map(mapRolesFromStringToGrantAuth)
          .collect(Collectors.toSet());

  }

}
