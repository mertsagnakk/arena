package com.example.arena;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {

    // Eski komut: Veritabanındaki 'questions' tablosundan tüm soruları çeker
    @GET("questions")
    Call<List<Question>> getQuestions();

    // 1. Bekleyen (waiting) bir oda var mı diye sorar
    @GET("duels?status=eq.waiting&limit=1")
    Call<List<Duel>> findWaitingDuel();

    // 2. Bekleyen oda bulursak, kendimizi player2_id olarak odaya ekler ve status'ü active yapar
    @PATCH("duels")
    @Headers("Prefer: return=representation") // Güncellenen odayı bize geri döndürür
    Call<List<Duel>> joinDuel(
            @Query("id") String eqId, // "eq.uuid_gelecek" formatında gönderilecek
            @Body Duel updateBody
    );

    // 3. Bekleyen oda yoksa, player1_id olarak yeni bir oda (waiting) kurar
    @POST("duels")
    @Headers("Prefer: return=representation")
    Call<List<Duel>> createDuel(@Body Duel newDuel);

}