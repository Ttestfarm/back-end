package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;

/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = -1464144204L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayment payment = new QPayment("payment");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> cancelledAmount = createNumber("cancelledAmount", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> cancelledAt = createDateTime("cancelledAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

<<<<<<< HEAD
    public final DateTimePath<java.time.LocalDateTime> failedAt = createDateTime("failedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final EnumPath<com.kosta.farm.util.PaymentMethod> method = createEnum("method", com.kosta.farm.util.PaymentMethod.class);

    public final StringPath name = createString("name");

    public final StringPath ordersId = createString("ordersId");

    public final DateTimePath<java.time.LocalDateTime> paidAt = createDateTime("paidAt", java.time.LocalDateTime.class);

    public final StringPath paymentBank = createString("paymentBank");
=======
    public final NumberPath<Long> deliveryId = createNumber("deliveryId", Long.class);

    public final StringPath deliveryprice = createString("deliveryprice");

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final NumberPath<Integer> invoiceCommission = createNumber("invoiceCommission", Integer.class);
>>>>>>> 3a995a0871826599ba61fd8bc93131c9c472d76e

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

    public final EnumPath<com.kosta.farm.util.PaymentStatus> status = createEnum("status", com.kosta.farm.util.PaymentStatus.class);

    public final QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPayment(String variable) {
        this(Payment.class, forVariable(variable), INITS);
    }

    public QPayment(Path<? extends Payment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayment(PathMetadata metadata, PathInits inits) {
        this(Payment.class, metadata, inits);
    }

    public QPayment(Class<? extends Payment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}
