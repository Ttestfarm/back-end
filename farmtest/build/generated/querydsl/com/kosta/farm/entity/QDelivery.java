package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDelivery is a Querydsl query type for Delivery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDelivery extends EntityPathBase<Delivery> {

    private static final long serialVersionUID = 1296954630L;

    public static final QDelivery delivery = new QDelivery("delivery");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final NumberPath<Long> deliveryId = createNumber("deliveryId", Long.class);

    public final NumberPath<Long> deliveryInfoId = createNumber("deliveryInfoId", Long.class);

    public final StringPath deliveryState = createString("deliveryState");

    public final NumberPath<Long> ordersId = createNumber("ordersId", Long.class);

    public final StringPath tCode = createString("tCode");

    public final StringPath tInvoice = createString("tInvoice");

    public final StringPath tName = createString("tName");

    public QDelivery(String variable) {
        super(Delivery.class, forVariable(variable));
    }

    public QDelivery(Path<? extends Delivery> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDelivery(PathMetadata metadata) {
        super(Delivery.class, metadata);
    }

}

