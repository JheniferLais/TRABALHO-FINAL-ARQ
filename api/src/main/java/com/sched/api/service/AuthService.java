package com.sched.api.service;

import com.sched.api.domain.*;
import com.sched.api.dto.request.CompanyRequest;
import com.sched.api.dto.request.UserRequest;
import com.sched.api.dto.response.CompanyResponse;
import com.sched.api.dto.response.UserResponse;
import com.sched.api.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CompanyResponse registerCompany(CompanyRequest dto) {
        Company company = Company.builder()
                .name(dto.name())
                .cnpj(dto.cnpj())
                .deleted(false)
                .build();
        company = companyRepository.save(company);

        User admin = User.builder()
                .name("ADMIN " + company.getName())
                .email(dto.emailADMIN())
                .password(passwordEncoder.encode(dto.passwordADMIN()))
                .role(Role.ADMIN)
                .company(company)
                .deleted(false)
                .build();
        userRepository.save(admin);

        return new CompanyResponse(company.getId(), company.getName(), company.getCnpj(), company.getCreatedAt());
    }

    @Transactional
    public UserResponse registerUser(UserRequest dto, User authenticatedAdmin) {
        User newUser = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.USER)
                .company(authenticatedAdmin.getCompany())
                .deleted(false)
                .build();

        userRepository.save(newUser);
        return new UserResponse(
                newUser.getId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getRole(),
                newUser.getCompany().getId(),
                newUser.getCreatedAt()
        );
    }
}
