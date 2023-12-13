package com.kosta.farm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFarmer is a Querydsl query type for Farmer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFarmer extends EntityPathBase<Farmer> {

    private static final long serialVersionUID = 1744479477L;

    public static final QFarmer farmer = new QFarmer("farmer");

    public final DateTimePath<java.sql.Timestamp> createDate = createDateTime("createDate", java.sql.Timestamp.class);

    public final StringPath farmAccountNum = createString("farmAccountNum");

    public final StringPath farmAddress = createString("farmAddress");

    public final StringPath farmAddressDetail = createString("farmAddressDetail");

    public final StringPath farmBank = createString("farmBank");

    public final NumberPath<Long> farmerId = createNumber("farmerId", Long.class);

    public final BooleanPath farmerState = createBoolean("farmerState");

    public final StringPath farmInterest1 = createString("farmInterest1");

    public final StringPath farmInterest2 = createString("farmInterest2");

    public final StringPath farmInterest3 = createString("farmInterest3");

    public final StringPath farmInterest4 = createString("farmInterest4");

    public final StringPath farmInterest5 = createString("farmInterest5");

    public final StringPath farmName = createString("farmName");

    public final StringPath farmPixurl = createString("farmPixurl");

    public final StringPath farmTel = createString("farmTel");

    public final NumberPath<Integer> followCount = createNumber("followCount", Integer.class);

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final StringPath registrationNum = createString("registrationNum");

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public QFarmer(String variable) {
        super(Farmer.class, forVariable(variable));
    }

    public QFarmer(Path<? extends Farmer> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFarmer(PathMetadata metadata) {
        super(Farmer.class, metadata);
    }

}

