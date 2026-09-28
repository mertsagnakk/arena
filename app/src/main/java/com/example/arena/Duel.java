package com.example.arena;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Duel {
    @SerializedName("id")
    public String id;

    @SerializedName("player1_id")
    public String player1Id;

    @SerializedName("player2_id")
    public String player2Id;

    @SerializedName("status")
    public String status;

    @SerializedName("player1_score")
    public int player1Score;

    @SerializedName("player2_score")
    public int player2Score;

    @SerializedName("question_ids")
    public List<Long> questionIds;

    @SerializedName("winner_id")
    public String winnerId;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("room_code")
    public String roomCode;

    @SerializedName("mode")
    public String mode; // "random" | "private"
}