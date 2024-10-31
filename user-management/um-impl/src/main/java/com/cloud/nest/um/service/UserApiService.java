package com.cloud.nest.um.service;

import com.cloud.nest.auth.AuthApiInternal;
import com.cloud.nest.auth.inout.NewAuthUserIn;
import com.cloud.nest.db.um.tables.records.UserRecord;
import com.cloud.nest.um.inout.UserIn;
import com.cloud.nest.um.inout.UserOut;
import com.cloud.nest.um.inout.UserUpdateIn;
import com.cloud.nest.um.mapper.UmMapper;
import com.cloud.nest.um.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserApiService {

    public static final String USER_NOT_FOUND_ERROR = "User [%s] not found";

    private final UserRepository userRepository;
    private final AuthApiInternal authApiInternal;
    private final UmMapper umMapper;

    @Transactional
    @NotNull
    public UserOut createUser(@NotNull UserIn in) {
        if (userRepository.existsByUsername(in.username())) {
            throw new IllegalArgumentException("User with username [%s] exists".formatted(in.username()));
        }

        if (userRepository.existsByEmail(in.email())) {
            throw new IllegalArgumentException("User with email [%s] exists".formatted(in.email()));
        }

        final UserRecord record = umMapper.toRecord(in, LocalDateTime.now());
        userRepository.save(record);

        authApiInternal.createUser(
                NewAuthUserIn.builder()
                        .userId(record.getId())
                        .username(record.getUsername())
                        .password(in.password())
                        .build()
        ).join();

        return umMapper.toOut(record);
    }

    @Transactional(readOnly = true)
    @NotNull
    public UserOut findById(Long id) {
        return userRepository.findById(id)
                .map(umMapper::toOut)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_ERROR.formatted(id)));
    }

    @Transactional
    public void updateUser(@NotNull Long id, @NotNull UserUpdateIn in) {
        final UserRecord record = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_ERROR.formatted(id)));

        record.setFirstName(in.firstName());
        record.setLastName(in.lastName());
        record.setUpdated(LocalDateTime.now());

        userRepository.save(record);
    }

}
