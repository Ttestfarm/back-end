package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInvoice is a Querydsl query type for Invoice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvoice extends EntityPathBase<Invoice> {

    private static final long serialVersionUID = 1282736091L;

    public static final QInvoice invoice = new QInvoice("invoice");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final NumberPath<Long> invocieCommission = createNumber("invocieCommission", Long.class);

    public final DatePath<java.sql.Date> invocieDate1 = createDate("invocieDate1", java.sql.Date.class);

    public final DatePath<java.sql.Date> invocieDate2 = createDate("invocieDate2", java.sql.Date.class);

    public final NumberPath<Long> invociePrice = createNumber("invociePrice", Long.class);

    public final NumberPath<Long> invoiceId = createNumber("invoiceId", Long.class);

    public final StringPath invoiceState = createString("invoiceState");

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public QInvoice(String variable) {
        super(Invoice.class, forVariable(variable));
    }

    public QInvoice(Path<? extends Invoice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInvoice(PathMetadata metadata) {
        super(Invoice.class, metadata);
    }

}

