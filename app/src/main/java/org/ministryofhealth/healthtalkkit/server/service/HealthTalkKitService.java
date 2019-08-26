package org.ministryofhealth.healthtalkkit.server.service;

import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HealthTalkKitService {
    @GET("/api/health-kit-talk/get")
    Call<List<HealthTalkKit>> getKits();

    @GET("/api/health-kit-talk/subcontent/get")
    Call<List<HealthTalkKit>> getSubcontent();

    @GET("/api/health-kit-talk/grandchildren/get")
    Call<List<HealthTalkKit>> getGrandchildren();
}
