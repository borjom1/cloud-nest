package com.cloud.nest.um.service;

import com.cloud.nest.db.um.tables.records.UserRecord;
import com.cloud.nest.um.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void save() {
        final long value = new Random().nextLong(0, 10000);
        final UserRecord record = new UserRecord();
        record.setId(1L);
        record.setCreated(LocalDateTime.now());
        record.setUpdated(LocalDateTime.now());
        record.setUsername("U-" + value);
        userRepository.save(record);
    }

}
