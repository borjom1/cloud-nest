/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.um;


import com.cloud.nest.db.um.tables.User;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in um.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index USER_USERNAME_IDX = Internal.createIndex(DSL.name("user_username_idx"), User.USER, new OrderField[] { User.USER.USERNAME }, true);
}