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

    public final StringPath cancelText = createString("cancelText");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final NumberPath<Long> deliveryId = createNumber("deliveryId", Long.class);

    public final StringPath deliveryprice = createString("deliveryprice");

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final NumberPath<Integer> invoiceCommission = createNumber("invoiceCommission", Integer.class);

    public final DatePath<java.sql.Date> invoiceDate = createDate("invoiceDate", java.sql.Date.class);

    public final NumberPath<Integer> invoicePrice = createNumber("invoicePrice", Integer.class);

    public final StringPath payType = createString("payType");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath product = createString("product");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Long> quotationId = createNumber("quotationId", Long.class);

    public final StringPath receiptId = createString("receiptId");

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public final EnumPath<com.kosta.farm.util.PaymentStatus> state = createEnum("state", com.kosta.farm.util.PaymentStatus.class);

    public final StringPath tCode = createString("tCode");

    public final StringPath tInvoice = createString("tInvoice");

    public final StringPath tName = createString("tName");

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

