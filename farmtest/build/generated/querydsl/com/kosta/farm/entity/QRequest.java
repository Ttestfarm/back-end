package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRequest is a Querydsl query type for Request
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRequest extends EntityPathBase<Request> {

    private static final long serialVersionUID = 418230077L;

    public static final QRequest request = new QRequest("request");

    public final StringPath address1 = createString("address1");

    public final StringPath address2 = createString("address2");

    public final StringPath address3 = createString("address3");

    public final BooleanPath choiceState = createBoolean("choiceState");

    public final DatePath<java.sql.Date> createDate = createDate("createDate", java.sql.Date.class);

    public final StringPath name = createString("name");

    public final DatePath<java.sql.Date> requestDate = createDate("requestDate", java.sql.Date.class);

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public final StringPath requestMessage = createString("requestMessage");

    public final StringPath requestProduct = createString("requestProduct");

    public final StringPath requestQuantity = createString("requestQuantity");

    public final EnumPath<com.kosta.farm.util.RequestStatus> state = createEnum("state", com.kosta.farm.util.RequestStatus.class);

    public final StringPath tel = createString("tel");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRequest(String variable) {
        super(Request.class, forVariable(variable));
    }

    public QRequest(Path<? extends Request> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRequest(PathMetadata metadata) {
        super(Request.class, metadata);
    }

}

