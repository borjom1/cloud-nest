package com.cloud.nest.auth.service;

import com.cloud.nest.auth.inout.request.NewAuthUserIn;
import com.cloud.nest.auth.mapper.AuthMapper;
import com.cloud.nest.auth.repository.UserRepository;
import com.cloud.nest.auth.repository.UserRoleRepository;
import com.cloud.nest.db.auth.tables.records.UserRecord;
import com.cloud.nest.db.auth.tables.records.UserRoleRecord;
import com.cloud.nest.platform.model.auth.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    @Transactional
    public void saveUser(@NotNull NewAuthUserIn in) {
        if (userRepository.existsById(in.userId())) {
            throw new IllegalArgumentException("User with sessionId [%d] already exists".formatted(in.userId()));
        }

        final LocalDateTime now = LocalDateTime.now();

        final UserRecord userRecord = authMapper.toRecord(in, now);
        userRecord.setPassword(passwordEncoder.encode(in.password()));
        userRepository.insert(userRecord);

        final UserRoleRecord userRoleRecord = new UserRoleRecord();
        userRoleRecord.setUserId(in.userId());
        userRoleRecord.setRole(UserRole.USER.name());
        userRoleRecord.setCreated(now);
        userRoleRepository.insert(userRoleRecord);
    }

    @Transactional(readOnly = true)
    @NotNull
    public Optional<UserRecord> getByUsername(@NotBlank String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Transactional(readOnly = true)
    @NotNull
    public Optional<UserRecord> getById(Long userId) {
        return Optional.ofNullable(userRepository.findById(userId));
    }

}
