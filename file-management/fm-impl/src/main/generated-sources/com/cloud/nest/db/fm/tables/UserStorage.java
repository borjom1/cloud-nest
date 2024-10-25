/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.fm.tables;


import com.cloud.nest.db.fm.Fm;
import com.cloud.nest.db.fm.Keys;
import com.cloud.nest.db.fm.tables.records.UserStorageRecord;

import java.time.LocalDateTime;
import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Field;
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
public class UserStorage extends TableImpl<UserStorageRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>fm.user_storage</code>
     */
    public static final UserStorage USER_STORAGE = new UserStorage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserStorageRecord> getRecordType() {
        return UserStorageRecord.class;
    }

    /**
     * The column <code>fm.user_storage.user_id</code>.
     */
    public final TableField<UserStorageRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>fm.user_storage.storage_space</code>.
     */
    public final TableField<UserStorageRecord, Long> STORAGE_SPACE = createField(DSL.name("storage_space"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>fm.user_storage.used_storage_space</code>.
     */
    public final TableField<UserStorageRecord, Long> USED_STORAGE_SPACE = createField(DSL.name("used_storage_space"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>fm.user_storage.total_downloaded_bytes</code>.
     */
    public final TableField<UserStorageRecord, Long> TOTAL_DOWNLOADED_BYTES = createField(DSL.name("total_downloaded_bytes"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>fm.user_storage.total_uploaded_bytes</code>.
     */
    public final TableField<UserStorageRecord, Long> TOTAL_UPLOADED_BYTES = createField(DSL.name("total_uploaded_bytes"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>fm.user_storage.disabled</code>.
     */
    public final TableField<UserStorageRecord, Boolean> DISABLED = createField(DSL.name("disabled"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>fm.user_storage.created</code>.
     */
    public final TableField<UserStorageRecord, LocalDateTime> CREATED = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    /**
     * The column <code>fm.user_storage.updated</code>.
     */
    public final TableField<UserStorageRecord, LocalDateTime> UPDATED = createField(DSL.name("updated"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    private UserStorage(Name alias, Table<UserStorageRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private UserStorage(Name alias, Table<UserStorageRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>fm.user_storage</code> table reference
     */
    public UserStorage(String alias) {
        this(DSL.name(alias), USER_STORAGE);
    }

    /**
     * Create an aliased <code>fm.user_storage</code> table reference
     */
    public UserStorage(Name alias) {
        this(alias, USER_STORAGE);
    }

    /**
     * Create a <code>fm.user_storage</code> table reference
     */
    public UserStorage() {
        this(DSL.name("user_storage"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Fm.FM;
    }

    @Override
    public UniqueKey<UserStorageRecord> getPrimaryKey() {
        return Keys.USER_RATING_USER_ID_PKEY;
    }

    @Override
    public UserStorage as(String alias) {
        return new UserStorage(DSL.name(alias), this);
    }

    @Override
    public UserStorage as(Name alias) {
        return new UserStorage(alias, this);
    }

    @Override
    public UserStorage as(Table<?> alias) {
        return new UserStorage(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserStorage rename(String name) {
        return new UserStorage(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserStorage rename(Name name) {
        return new UserStorage(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserStorage rename(Table<?> name) {
        return new UserStorage(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserStorage where(Condition condition) {
        return new UserStorage(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserStorage where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserStorage where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserStorage where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserStorage where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserStorage where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserStorage where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserStorage where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserStorage whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserStorage whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}