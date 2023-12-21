package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayInfo is a Querydsl query type for PayInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayInfo extends EntityPathBase<PayInfo> {

    private static final long serialVersionUID = -1465208284L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayInfo payInfo = new QPayInfo("payInfo");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> cancelledAmount = createNumber("cancelledAmount", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> cancelledAt = createDateTime("cancelledAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final DateTimePath<java.time.LocalDateTime> failedAt = createDateTime("failedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final StringPath merchantUid = createString("merchantUid");

    public final EnumPath<com.kosta.farm.util.PaymentMethod> method = createEnum("method", com.kosta.farm.util.PaymentMethod.class);

    public final StringPath name = createString("name");

    public final StringPath ordersId = createString("ordersId");

    public final NumberPath<java.math.BigDecimal> paidAmount = createNumber("paidAmount", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> paidAt = createDateTime("paidAt", java.time.LocalDateTime.class);

    public final StringPath paymentBank = createString("paymentBank");

    public final NumberPath<Integer> paymentDelivery = createNumber("paymentDelivery", Integer.class);

    public final NumberPath<Integer> paymentPrice = createNumber("paymentPrice", Integer.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Integer> productPrice = createNumber("productPrice", Integer.class);

    public final NumberPath<Long> quotationId = createNumber("quotationId", Long.class);

    public final StringPath receiptId = createString("receiptId");

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public final StringPath state = createString("state");

    public final EnumPath<com.kosta.farm.util.PaymentStatus> status = createEnum("status", com.kosta.farm.util.PaymentStatus.class);

    public final QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPayInfo(String variable) {
        this(PayInfo.class, forVariable(variable), INITS);
    }

    public QPayInfo(Path<? extends PayInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayInfo(PathMetadata metadata, PathInits inits) {
        this(PayInfo.class, metadata, inits);
    }

    public QPayInfo(Class<? extends PayInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

