package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFarmerfollow is a Querydsl query type for Farmerfollow
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFarmerfollow extends EntityPathBase<Farmerfollow> {

    private static final long serialVersionUID = -685533978L;

    public static final QFarmerfollow farmerfollow = new QFarmerfollow("farmerfollow");

    public final NumberPath<Long> farmerFollowId = createNumber("farmerFollowId", Long.class);

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QFarmerfollow(String variable) {
        super(Farmerfollow.class, forVariable(variable));
    }

    public QFarmerfollow(Path<? extends Farmerfollow> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFarmerfollow(PathMetadata metadata) {
        super(Farmerfollow.class, metadata);
    }

}

