/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.fm.tables.records;


import com.cloud.nest.db.fm.tables.Report;

import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ReportRecord extends UpdatableRecordImpl<ReportRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>fm.report.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>fm.report.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>fm.report.user_id</code>.
     */
    public void setUserId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>fm.report.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>fm.report.period_start</code>.
     */
    public void setPeriodStart(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>fm.report.period_start</code>.
     */
    public LocalDateTime getPeriodStart() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>fm.report.period_end</code>.
     */
    public void setPeriodEnd(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>fm.report.period_end</code>.
     */
    public LocalDateTime getPeriodEnd() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>fm.report.downloaded_bytes</code>.
     */
    public void setDownloadedBytes(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>fm.report.downloaded_bytes</code>.
     */
    public Long getDownloadedBytes() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>fm.report.uploaded_bytes</code>.
     */
    public void setUploadedBytes(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>fm.report.uploaded_bytes</code>.
     */
    public Long getUploadedBytes() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>fm.report.created</code>.
     */
    public void setCreated(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>fm.report.created</code>.
     */
    public LocalDateTime getCreated() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ReportRecord
     */
    public ReportRecord() {
        super(Report.REPORT);
    }

    /**
     * Create a detached, initialised ReportRecord
     */
    public ReportRecord(Long id, Long userId, LocalDateTime periodStart, LocalDateTime periodEnd, Long downloadedBytes, Long uploadedBytes, LocalDateTime created) {
        super(Report.REPORT);

        setId(id);
        setUserId(userId);
        setPeriodStart(periodStart);
        setPeriodEnd(periodEnd);
        setDownloadedBytes(downloadedBytes);
        setUploadedBytes(uploadedBytes);
        setCreated(created);
        resetChangedOnNotNull();
    }
}