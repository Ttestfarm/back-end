package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductFile is a Querydsl query type for ProductFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductFile extends EntityPathBase<ProductFile> {

    private static final long serialVersionUID = -494543687L;

    public static final QProductFile productFile = new QProductFile("productFile");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final StringPath directory = createString("directory");

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> productFileId = createNumber("productFileId", Long.class);

    public final StringPath productState = createString("productState");

    public final NumberPath<Long> size = createNumber("size", Long.class);

    public QProductFile(String variable) {
        super(ProductFile.class, forVariable(variable));
    }

    public QProductFile(Path<? extends ProductFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductFile(PathMetadata metadata) {
        super(ProductFile.class, metadata);
    }

}

