package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQuotation is a Querydsl query type for Quotation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuotation extends EntityPathBase<Quotation> {

    private static final long serialVersionUID = 307486394L;

    public static final QQuotation quotation = new QQuotation("quotation");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final StringPath quotationComment = createString("quotationComment");

    public final NumberPath<Long> quotationId = createNumber("quotationId", Long.class);

    public final StringPath quotationImages = createString("quotationImages");

    public final NumberPath<Integer> quotationPrice = createNumber("quotationPrice", Integer.class);

    public final StringPath quotationProduct = createString("quotationProduct");

    public final NumberPath<Integer> quotationQuantity = createNumber("quotationQuantity", Integer.class);

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public final EnumPath<com.kosta.farm.util.QuotationStatus> state = createEnum("state", com.kosta.farm.util.QuotationStatus.class);

    public QQuotation(String variable) {
        super(Quotation.class, forVariable(variable));
    }

    public QQuotation(Path<? extends Quotation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQuotation(PathMetadata metadata) {
        super(Quotation.class, metadata);
    }

}

