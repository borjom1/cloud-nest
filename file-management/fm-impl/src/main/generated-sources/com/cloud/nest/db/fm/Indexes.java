/*
 * This file is generated by jOOQ.
 */
package com.cloud.nest.db.fm;


import com.cloud.nest.db.fm.tables.File;
import com.cloud.nest.db.fm.tables.SharedFile;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in fm.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index FILE_S3_OBJECT_KEY_IDX = Internal.createIndex(DSL.name("file_s3_object_key_idx"), File.FILE, new OrderField[] { File.FILE.S3_OBJECT_KEY }, true);
    public static final Index SHARED_FILE_FILE_ID_IDX = Internal.createIndex(DSL.name("shared_file_file_id_idx"), SharedFile.SHARED_FILE, new OrderField[] { SharedFile.SHARED_FILE.FILE_ID }, false);
}
