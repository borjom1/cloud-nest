/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.fm.tables.records;


import com.cloud.nest.db.fm.tables.SharedFile;

import java.time.LocalDateTime;
import java.util.UUID;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SharedFileRecord extends UpdatableRecordImpl<SharedFileRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>fm.shared_file.id</code>.
     */
    public void setId(UUID value) {
        set(0, value);
    }

    /**
     * Getter for <code>fm.shared_file.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>fm.shared_file.file_id</code>.
     */
    public void setFileId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>fm.shared_file.file_id</code>.
     */
    public Long getFileId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>fm.shared_file.downloads</code>.
     */
    public void setDownloads(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>fm.shared_file.downloads</code>.
     */
    public Integer getDownloads() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>fm.shared_file.password</code>.
     */
    public void setPassword(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>fm.shared_file.password</code>.
     */
    public String getPassword() {
        return (String) get(3);
    }

    /**
     * Setter for <code>fm.shared_file.expires_at</code>.
     */
    public void setExpiresAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>fm.shared_file.expires_at</code>.
     */
    public LocalDateTime getExpiresAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>fm.shared_file.created</code>.
     */
    public void setCreated(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>fm.shared_file.created</code>.
     */
    public LocalDateTime getCreated() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>fm.shared_file.updated</code>.
     */
    public void setUpdated(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>fm.shared_file.updated</code>.
     */
    public LocalDateTime getUpdated() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SharedFileRecord
     */
    public SharedFileRecord() {
        super(SharedFile.SHARED_FILE);
    }

    /**
     * Create a detached, initialised SharedFileRecord
     */
    public SharedFileRecord(UUID id, Long fileId, Integer downloads, String password, LocalDateTime expiresAt, LocalDateTime created, LocalDateTime updated) {
        super(SharedFile.SHARED_FILE);

        setId(id);
        setFileId(fileId);
        setDownloads(downloads);
        setPassword(password);
        setExpiresAt(expiresAt);
        setCreated(created);
        setUpdated(updated);
        resetChangedOnNotNull();
    }
}
