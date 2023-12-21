package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayInfo is a Querydsl query type for PayInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayInfo extends EntityPathBase<PayInfo> {

    private static final long serialVersionUID = -1465208284L;

    public static final QPayInfo payInfo = new QPayInfo("payInfo");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final StringPath buyerAddress = createString("buyerAddress");

    public final StringPath buyerName = createString("buyerName");

    public final StringPath buyerTel = createString("buyerTel");

    public final NumberPath<java.math.BigDecimal> cancelledAmount = createNumber("cancelledAmount", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> cancelledAt = createDateTime("cancelledAt", java.time.LocalDateTime.class);

    public final StringPath cancelText = createString("cancelText");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> failedAt = createDateTime("failedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final NumberPath<Integer> invoiceCommission = createNumber("invoiceCommission", Integer.class);

    public final DatePath<java.sql.Date> invoiceDate = createDate("invoiceDate", java.sql.Date.class);

    public final NumberPath<Integer> invoicePrice = createNumber("invoicePrice", Integer.class);

    public final StringPath ordersId = createString("ordersId");

    public final DatePath<java.sql.Date> paidAt = createDate("paidAt", java.sql.Date.class);

    public final NumberPath<Integer> paymentDelivery = createNumber("paymentDelivery", Integer.class);

    public final StringPath paymentMethod = createString("paymentMethod");

    public final StringPath pgTid = createString("pgTid");

    public final StringPath pgType = createString("pgType");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Integer> productPrice = createNumber("productPrice", Integer.class);

    public final NumberPath<Long> quotationId = createNumber("quotationId", Long.class);

    public final StringPath receiptId = createString("receiptId");

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public final EnumPath<com.kosta.farm.util.PaymentStatus> state = createEnum("state", com.kosta.farm.util.PaymentStatus.class);

    public final StringPath status = createString("status");

    public final StringPath tCode = createString("tCode");

    public final StringPath tInvoice = createString("tInvoice");

    public final StringPath tName = createString("tName");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPayInfo(String variable) {
        super(PayInfo.class, forVariable(variable));
    }

    public QPayInfo(Path<? extends PayInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayInfo(PathMetadata metadata) {
        super(PayInfo.class, metadata);
    }

}

