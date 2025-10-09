package app.app.config;

import app.app.user.Service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/css/**", "/script/**", "/images/**", "/static/**").permitAll() 
                        .requestMatchers("/users/signup", "/users/login", "/users/signup_complete").permitAll()
                        .requestMatchers("/touristSpot/**","/api/**","/api/tourist-accessible-info","/touristSpot/Json/**","/**").permitAll() 
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/users/login")
                        .loginProcessingUrl("/users/login")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // 로그인 성공 시 200 반환
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 로그인 실패 시 401 반환
                        })
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/users/login?error=true")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
