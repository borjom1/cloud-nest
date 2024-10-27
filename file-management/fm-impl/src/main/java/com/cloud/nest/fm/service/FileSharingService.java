package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.db.fm.tables.records.SharedFileRecord;
import com.cloud.nest.fm.exception.OperationNotAllowedException;
import com.cloud.nest.fm.inout.request.SharedFileDownloadIn;
import com.cloud.nest.fm.inout.request.SharedFileIn;
import com.cloud.nest.fm.inout.response.SharedFileOut;
import com.cloud.nest.fm.persistence.repository.SharedFileRepository;
import com.cloud.nest.platform.model.exception.DataNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.cloud.nest.fm.FileApiExternal.URL_FILES;
import static com.cloud.nest.fm.FileApiExternal.URL_SHARES;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class FileSharingService {

    public static final int MAX_LINK_COUNT_PER_FILE = 10;
    public static final String SHARED_FILE_NOT_FOUND = "Shared file with id [%s] not found";

    private final SharedFileRepository sharedFileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SharedFileOut shareFile(@NotNull FileRecord fileRecord, @NotNull SharedFileIn in) {
        final long fileShareCount = sharedFileRepository.countNotExpiredSharesByFileId(fileRecord.getId());
        if (fileShareCount >= MAX_LINK_COUNT_PER_FILE) {
            throw new OperationNotAllowedException(
                    "It is only allowed %s shares per file".formatted(MAX_LINK_COUNT_PER_FILE)
            );
        }

        final var sharedFileRecord = createSharedFileRecord(fileRecord, in);
        sharedFileRepository.insert(sharedFileRecord);

        return getSharedFileOut(sharedFileRecord);
    }

    /**
     * @return {@code file id} if not expired shared file was found and credentials matched
     * @throws DataNotFoundException    when shared file was not found
     * @throws IllegalArgumentException when shared file was found and credentials not matched
     */
    @Transactional(readOnly = true)
    @NotNull
    public Long getSharedFile(@NotNull UUID shareId, @NotNull SharedFileDownloadIn in) {
        return sharedFileRepository.findNotExpiredById(shareId)
                .map(foundRecord -> {
                    final String storedPassword = foundRecord.getPassword();
                    if (storedPassword == null) {
                        return foundRecord.getFileId();
                    }

                    if (in.password() == null || !passwordEncoder.matches(in.password(), storedPassword)) {
                        throw new IllegalArgumentException("Invalid access credentials");
                    }

                    return foundRecord.getFileId();
                })
                .orElseThrow(() -> new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId)));
    }

    /**
     * @return {@code file id} if not expired shared file was found and credentials matched
     */
    @Transactional(readOnly = true)
    @NotNull
    public SharedFileRecord getSharedFileByShareId(@NotNull UUID shareId) {
        return sharedFileRepository.findNotExpiredById(shareId)
                .orElseThrow(() -> new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId)));
    }

    @Transactional
    public void deactivateSharedFile(@NotNull SharedFileRecord record) {
        record.setExpiresAt(LocalDateTime.now().minusYears(10L));
        sharedFileRepository.update(record);
    }

    @Transactional
    public void incrementShareDownload(UUID shareId) {
        sharedFileRepository.findByIdForUpdate(shareId)
                .ifPresentOrElse(
                        foundRecord -> {
                            foundRecord.setDownloads(foundRecord.getDownloads() + 1);
                            sharedFileRepository.update(foundRecord);
                        },
                        () -> {
                            throw new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId));
                        }
                );
    }

    @Transactional(readOnly = true)
    public List<SharedFileOut> getAllSharesByFileId(Long fileId) {
        return sharedFileRepository.findAllNotExpiredByFileId(fileId)
                .stream()
                .map(this::getSharedFileOut)
                .sorted(comparing(SharedFileOut::created).reversed())
                .toList();
    }

    @NotNull
    private SharedFileRecord createSharedFileRecord(@NotNull FileRecord fileRecord, @NotNull SharedFileIn in) {
        final var now = LocalDateTime.now();
        final var sharedFileRecord = new SharedFileRecord();

        sharedFileRecord.setId(UUID.randomUUID());
        sharedFileRecord.setFileId(fileRecord.getId());
        sharedFileRecord.setDownloads(0);

        sharedFileRecord.setExpiresAt(
                in.expiresAt() == null ? null : in.expiresAt().toLocalDateTime()
        );
        sharedFileRecord.setPassword(
                in.password() == null ? null : passwordEncoder.encode(in.password())
        );

        sharedFileRecord.setCreated(now);
        sharedFileRecord.setUpdated(now);
        return sharedFileRecord;
    }

    @NotNull
    private SharedFileOut getSharedFileOut(@NotNull SharedFileRecord sharedFileRecord) {
        return SharedFileOut.builder()
                .password(sharedFileRecord.getPassword() != null)
                .created(sharedFileRecord.getCreated())
                .expiresAt(sharedFileRecord.getExpiresAt())
                .link(URL_FILES + URL_SHARES + "/" + sharedFileRecord.getId())
                .build();
    }

}
