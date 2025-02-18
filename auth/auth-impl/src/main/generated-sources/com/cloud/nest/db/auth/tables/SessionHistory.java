/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.auth.tables;


import com.cloud.nest.db.auth.Auth;
import com.cloud.nest.db.auth.Indexes;
import com.cloud.nest.db.auth.Keys;
import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SessionHistory extends TableImpl<SessionHistoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>auth.session_history</code>
     */
    public static final SessionHistory SESSION_HISTORY = new SessionHistory();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SessionHistoryRecord> getRecordType() {
        return SessionHistoryRecord.class;
    }

    /**
     * The column <code>auth.session_history.user_id</code>.
     */
    public final TableField<SessionHistoryRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>auth.session_history.client_ip</code>.
     */
    public final TableField<SessionHistoryRecord, String> CLIENT_IP = createField(DSL.name("client_ip"), SQLDataType.VARCHAR(15).nullable(false), this, "");

    /**
     * The column <code>auth.session_history.user_agent</code>.
     */
    public final TableField<SessionHistoryRecord, String> USER_AGENT = createField(DSL.name("user_agent"), SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>auth.session_history.created</code>.
     */
    public final TableField<SessionHistoryRecord, LocalDateTime> CREATED = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    /**
     * The column <code>auth.session_history.updated</code>.
     */
    public final TableField<SessionHistoryRecord, LocalDateTime> UPDATED = createField(DSL.name("updated"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    /**
     * The column <code>auth.session_history.session_id</code>.
     */
    public final TableField<SessionHistoryRecord, String> SESSION_ID = createField(DSL.name("session_id"), SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>auth.session_history.last_active</code>.
     */
    public final TableField<SessionHistoryRecord, LocalDateTime> LAST_ACTIVE = createField(DSL.name("last_active"), SQLDataType.LOCALDATETIME(6), this, "");

    private SessionHistory(Name alias, Table<SessionHistoryRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private SessionHistory(Name alias, Table<SessionHistoryRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>auth.session_history</code> table reference
     */
    public SessionHistory(String alias) {
        this(DSL.name(alias), SESSION_HISTORY);
    }

    /**
     * Create an aliased <code>auth.session_history</code> table reference
     */
    public SessionHistory(Name alias) {
        this(alias, SESSION_HISTORY);
    }

    /**
     * Create a <code>auth.session_history</code> table reference
     */
    public SessionHistory() {
        this(DSL.name("session_history"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Auth.AUTH;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.SESSION_HISTORY_USER_ID_IDX);
    }

    @Override
    public UniqueKey<SessionHistoryRecord> getPrimaryKey() {
        return Keys.SESSION_HISTORY_SESSION_ID_USER_ID_PKEY;
    }

    @Override
    public SessionHistory as(String alias) {
        return new SessionHistory(DSL.name(alias), this);
    }

    @Override
    public SessionHistory as(Name alias) {
        return new SessionHistory(alias, this);
    }

    @Override
    public SessionHistory as(Table<?> alias) {
        return new SessionHistory(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public SessionHistory rename(String name) {
        return new SessionHistory(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SessionHistory rename(Name name) {
        return new SessionHistory(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public SessionHistory rename(Table<?> name) {
        return new SessionHistory(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SessionHistory where(Condition condition) {
        return new SessionHistory(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SessionHistory where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SessionHistory where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SessionHistory where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SessionHistory where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SessionHistory where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SessionHistory where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public SessionHistory where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SessionHistory whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public SessionHistory whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
