/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.auth;


import com.cloud.nest.db.DefaultCatalog;
import com.cloud.nest.db.auth.tables.Session;
import com.cloud.nest.db.auth.tables.SessionHistory;
import com.cloud.nest.db.auth.tables.User;
import com.cloud.nest.db.auth.tables.UserRole;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Auth extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>auth</code>
     */
    public static final Auth AUTH = new Auth();

    /**
     * The table <code>auth.session</code>.
     */
    public final Session SESSION = Session.SESSION;

    /**
     * The table <code>auth.session_history</code>.
     */
    public final SessionHistory SESSION_HISTORY = SessionHistory.SESSION_HISTORY;

    /**
     * The table <code>auth.user</code>.
     */
    public final User USER = User.USER;

    /**
     * The table <code>auth.user_role</code>.
     */
    public final UserRole USER_ROLE = UserRole.USER_ROLE;

    /**
     * No further instances allowed
     */
    private Auth() {
        super("auth", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Session.SESSION,
            SessionHistory.SESSION_HISTORY,
            User.USER,
            UserRole.USER_ROLE
        );
    }
}
