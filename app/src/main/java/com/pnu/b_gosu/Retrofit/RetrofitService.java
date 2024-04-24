package com.pnu.b_gosu.Retrofit;


import com.pnu.b_gosu.Map.ReceiveModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {


    @POST("daily/arcore")
    Call<Integer>  is_photo(@Query("characterid")int characterid);

    @GET("tag/restaurant")
    Call<List<ReceiveModel>>  callres(@Query("mapx")double mapx, @Query("mapy")double mapy);


    @GET("tag/shopping")
    Call<List<ReceiveModel>>  callshop(@Query("mapx")double mapx, @Query("mapy")double mapy);

    @GET("daily/tour")
    Call<TipModel>  calltip();


    @GET("tag/tour")
    Call<List<ReceiveModel>>  calltour(@Query("mapx")double mapx, @Query("mapy")double mapy);


    @GET("tag/enjoy")
    Call<List<ReceiveModel>>  callplay(@Query("mapx")double mapx, @Query("mapy")double mapy);


    @GET("knto/restaurant")
    Call<List<ReceiveModel>> call_res_quest(@Query("mapx")double mapx,
                                            @Query("mapy")double mapy,
                                            @Query("tag1") String tag1,
                                            @Query("tag2") String tag2,
                                            @Query("characterid")int characterid);

    @GET("knto/tour")
    Call<List<ReceiveModel>> call_tour_quest(
                                             @Query("mapx")double mapx,
                                             @Query("mapy")double mapy,
                                             @Query("characterid")int characterid);

    @GET("knto/cultural")
    Call<List<ReceiveModel>> call_cultural_quest(
                                                 @Query("mapx")double mapx,
                                                 @Query("mapy")double mapy,
                                                 @Query("characterid")int characterid);

    @GET("knto/shopping")
    Call<List<ReceiveModel>> call_shopping_quest(
                                                 @Query("mapx")double mapx,
                                                 @Query("mapy")double mapy,
                                                 @Query("characterid")int characterid);



    @GET("knto/enjoy")
    Call<List<ReceiveModel>> call_play_quest(
                                            @Query("mapx")double mapx,
                                            @Query("mapy")double mapy,
                                            @Query("tag1") String tag1,
                                            @Query("characterid")int characterid);

//반환값 물어보기

    @POST("challenge/accept")
    Call<String> challenge_accept(@Query("characterid")int characterid,
                                   @Query("title") String title );


    @POST("challenge/drop")
    Call<String> challenge_drop(@Query("characterid")int characterid,
                                   @Query("title") String title );


    @POST("challenge/success")
    Call<String> challenge_check(@Query("characterid") int characterid,
                                        @Query("mapx") double mapx,
                                        @Query("mapy")  double mapy);


    @POST("challenge/quiz")
    Call<QuizModel> callquiz(@Query("title") String title);


    @POST("challenge/reference")
    Call<ReceiveModel> challenge_is_exist(@Query("characterid") Integer charcterid);


    @GET("question/find")
    Call<String> login(@Query("android") String answer_id);






    @POST("question/save")
    Call<Integer> Surveysubmit(
            @Query("android") String android,
            @Query("ct_tag1") String ctTag1,
            @Query("ct_tag2") String ctTag2,
            @Query("ct_tag3") String ctTag3,
            @Query("ct_tag4") String ctTag4,
            @Query("r_tag3") String rTag3,
            @Query("r_tag4") String rTag4,
            @Query("r_tag5") String rTag5,
            @Query("r_tag3_1") String rTag3_1,
            @Query("s_tag1") String sTag1,
            @Query("user_name")String name,
            @Query("start_date") String start_date,
            @Query("end_date") String end_date
    );

    @GET("information")
    Call<InfoModel> callinformation(@Query("characterid") int characterid);

    @GET("push/{tokens}")
    Call<String> insertToken(@Path("tokens") String tokens);

}
