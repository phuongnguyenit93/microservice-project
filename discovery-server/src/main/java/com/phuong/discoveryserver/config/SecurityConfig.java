package com.phuong.discoveryserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig{

    // Vì WebSecurityConfigurerAdapter đã deprecated nên ta sẽ làm theo cách khác
    // https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter

    // Get the username value from application.properties
    @Value("${eureka.username}")
    private String username;

    @Value("${eureka.password}")
    private String password;

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername(username)
                .password(password)
                .roles("USER")
                .build());
        manager.createUser(User.withUsername("admin")
                .password("admin")
                .roles("USER","MOD","ADMIN")
                .build());
        manager.createUser(User.withUsername("moderator")
                .password("moderator")
                .roles("MOD","USER")
                .build());

        return manager;
    }

    @Bean
    @Autowired
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent ev) {
                System.out.println("Session created");
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent ev) {
                System.out.println("Session destroyed");
            }
        };
    }

    // If you need to change type Crypt Password type, please change here
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoEncoderPassword.getInstance();
    }

    // permitAll() : Cho phép tất cả phía trước
    // authenticated() : Phải xác thực
    // antMatchers("/path") : Setup các đường dẫn
    // cors() , csrf() : Các thuộc tính bảo vệ website ("có thể dùng kèm . disable()")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/session/**").permitAll()
                                .anyRequest().authenticated()
                                //.antMatchers("/actuator/**").hasRole("ADMIN")
                )
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic()
                .and().formLogin()
                .and().logout().logoutUrl("/session/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/")
        ;
        return http.build();
    }
}
