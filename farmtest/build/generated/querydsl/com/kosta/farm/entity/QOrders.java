package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrders is a Querydsl query type for Orders
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrders extends EntityPathBase<Orders> {

    private static final long serialVersionUID = 2017417335L;

    public static final QOrders orders = new QOrders("orders");

    public final StringPath cancelText = createString("cancelText");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final NumberPath<Integer> ordersCount = createNumber("ordersCount", Integer.class);

    public final NumberPath<Long> ordersId = createNumber("ordersId", Long.class);

    public final NumberPath<Integer> ordersPrice = createNumber("ordersPrice", Integer.class);

    public final StringPath ordersState = createString("ordersState");

    public final NumberPath<Long> paymentId = createNumber("paymentId", Long.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Long> quotationId = createNumber("quotationId", Long.class);

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QOrders(String variable) {
        super(Orders.class, forVariable(variable));
    }

    public QOrders(Path<? extends Orders> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrders(PathMetadata metadata) {
        super(Orders.class, metadata);
    }

}

