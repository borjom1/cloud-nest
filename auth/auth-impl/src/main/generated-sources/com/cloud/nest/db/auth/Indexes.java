/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.auth;


import com.cloud.nest.db.auth.tables.Session;
import com.cloud.nest.db.auth.tables.User;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in auth.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index SESSION_CLIENT_IP_IDX = Internal.createIndex(DSL.name("session_client_ip_idx"), Session.SESSION, new OrderField[] { Session.SESSION.CLIENT_IP }, false);
    public static final Index SESSION_USERNAME_IDX = Internal.createIndex(DSL.name("session_username_idx"), Session.SESSION, new OrderField[] { Session.SESSION.USERNAME }, false);
    public static final Index USER_USERNAME_IDX = Internal.createIndex(DSL.name("user_username_idx"), User.USER, new OrderField[] { User.USER.USERNAME }, true);
}