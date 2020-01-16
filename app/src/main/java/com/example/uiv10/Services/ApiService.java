package com.example.uiv10.Services;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {
    @POST("cgi-bin/tools/lmtool/run/")
    @Multipart
    Call<String> getStringResponse(@Part("formtype") String formtype, @Part MultipartBody.Part corpus);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileByUrl(@Url String fileUrl);
}