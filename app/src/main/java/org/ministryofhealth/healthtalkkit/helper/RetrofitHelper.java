package org.ministryofhealth.healthtalkkit.helper;

import org.ministryofhealth.healthtalkkit.config.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chriz on 9/12/2017.
 */

public class RetrofitHelper {

    private static RetrofitHelper mInstance;

    public static RetrofitHelper getInstance() {
        if (mInstance == null){
            mInstance = new RetrofitHelper();
        }
        return mInstance;
    }

    public Retrofit createHelper(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        return builder.build();
    }
}
