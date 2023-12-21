package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

/**
 * QFileVo is a Querydsl query type for FileVo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileVo extends EntityPathBase<FileVo> {

    private static final long serialVersionUID = 1751680743L;

    public static final QFileVo fileVo = new QFileVo("fileVo");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final StringPath directory = createString("directory");

    public final NumberPath<Long> FileId = createNumber("FileId", Long.class);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> size = createNumber("size", Long.class);

    public QFileVo(String variable) {
        super(FileVo.class, forVariable(variable));
    }

    public QFileVo(Path<? extends FileVo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileVo(PathMetadata metadata) {
        super(FileVo.class, metadata);
    }

}
