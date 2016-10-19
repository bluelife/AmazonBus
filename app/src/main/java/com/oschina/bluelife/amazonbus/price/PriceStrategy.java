package com.oschina.bluelife.amazonbus.price;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by slomka.jin on 2016/10/18.
 */
@IntDef({PriceStrategy.MATCH, PriceStrategy.HIGN, PriceStrategy.LOW})
@Retention(RetentionPolicy.SOURCE)
public @interface PriceStrategy {
    int MATCH = 0;
    int HIGN = 1;
    int LOW = 2;

}

