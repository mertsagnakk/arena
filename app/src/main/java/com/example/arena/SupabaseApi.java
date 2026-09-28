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
    // Arkadaşla oda kurmak için de aynı endpoint kullanılır (mode="private", room_code dolu gönderilir)
    @POST("duels")
    @Headers("Prefer: return=representation")
    Call<List<Duel>> createDuel(@Body Duel newDuel);

    // 4. Girilen kodla eşleşen, hâlâ bekleyen bir özel oda var mı diye bakar
    @GET("duels")
    Call<List<Duel>> findRoomByCode(
            @Query("room_code") String eqRoomCode,   // "eq.AB12" formatında
            @Query("status") String eqStatus,        // "eq.waiting"
            @Query("mode") String eqMode              // "eq.private"
    );

    // 5. Kodla bulunan odaya player2 olarak katılır (id ile filtreleyip günceller)
    @PATCH("duels")
    @Headers("Prefer: return=representation")
    Call<List<Duel>> joinRoomById(
            @Query("id") String eqId, // "eq.uuid_gelecek"
            @Body Duel updateBody
    );

    // 6. Oda kurucusu, rakip katılana kadar bu satırı yoklar (polling)
    @GET("duels")
    Call<List<Duel>> getDuelById(@Query("id") String eqId); // "eq.uuid_gelecek"

}