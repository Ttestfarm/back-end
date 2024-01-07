package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -986936931L;

    public static final QProduct product = new QProduct("product");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final StringPath productDescription = createString("productDescription");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final StringPath productName = createString("productName");

    public final StringPath productPrice = createString("productPrice");

    public final StringPath productQuantity = createString("productQuantity");

    public final NumberPath<Integer> productStock = createNumber("productStock", Integer.class);

    public final NumberPath<Integer> ShippingCost = createNumber("ShippingCost", Integer.class);

    public final EnumPath<com.kosta.farm.util.ProductStatus> state = createEnum("state", com.kosta.farm.util.ProductStatus.class);

    public final NumberPath<Long> thumbNail = createNumber("thumbNail", Long.class);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

