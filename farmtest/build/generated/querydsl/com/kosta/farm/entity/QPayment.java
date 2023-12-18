package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = -1464144204L;

    public static final QPayment payment = new QPayment("payment");

    public final StringPath amount = createString("amount");

    public final StringPath buyerAddress = createString("buyerAddress");

    public final StringPath buyerName = createString("buyerName");

    public final StringPath buyerTel = createString("buyerTel");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final NumberPath<Long> deliveryId = createNumber("deliveryId", Long.class);

    public final StringPath deliveryprice = createString("deliveryprice");

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final StringPath payType = createString("payType");

    public final StringPath price = createString("price");

    public final StringPath product = createString("product");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Long> quotationId = createNumber("quotationId", Long.class);

    public final StringPath receiptId = createString("receiptId");

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public final StringPath state = createString("state");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}

