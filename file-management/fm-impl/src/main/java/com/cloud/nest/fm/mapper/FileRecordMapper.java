package com.cloud.nest.fm.mapper;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.inout.response.FileBinOut;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.inout.response.UploadedFileOut;
import com.cloud.nest.fm.util.FileUtils.Filename2Ext;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FileRecordMapper {

    @Mapping(target = "uploadedBy", source = "userId")
    @Mapping(target = "filename", source = "filename2Ext.filename")
    @Mapping(target = "ext", source = "filename2Ext.ext")
    @Mapping(target = "created", source = "dateTime")
    @Mapping(target = "updated", source = "dateTime")
    FileRecord toRecord(
            String s3ObjectKey,
            Long userId,
            Filename2Ext filename2Ext,
            long size,
            String contentType,
            boolean deleted,
            LocalDateTime dateTime
    );

    UploadedFileOut toUploadedOut(FileRecord record);

    FileOut toOut(FileRecord record);

    FileBinOut toBinOut(FileRecord record);

}
