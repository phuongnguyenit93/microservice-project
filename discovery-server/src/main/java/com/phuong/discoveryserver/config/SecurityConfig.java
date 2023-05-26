package com.phuong.discoveryserver.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig{

    // Vì WebSecurityConfigurerAdapter đã deprecated nên ta sẽ làm theo cách khác
    // https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter


    @Bean
    @Autowired
    public UserDetailsService userDetailsService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                .password(bCryptPasswordEncoder.encode("user"))
                .roles("USER")
                .build());
        manager.createUser(User.withUsername("admin")
                .password(bCryptPasswordEncoder.encode("admin"))
                .roles("USER","MOD","ADMIN")
                .build());
        manager.createUser(User.withUsername("moderator")
                .password(bCryptPasswordEncoder.encode("moderator"))
                .roles("MOD","USER")
                .build());

        // Khi muốn dùng từ database , phải tạo 2 bảng là users và authorities . Chi tiết xem tại doc của class tại spring
        // thay manager = users khi muốn lấy list bằng database
        return manager;
    }

    @Bean
    @Autowired
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
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
                                .anyRequest().authenticated()
                                //.antMatchers("/actuator/**").hasRole("ADMIN")
                )
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic().and().logout();

        return http.build();
    }
}
