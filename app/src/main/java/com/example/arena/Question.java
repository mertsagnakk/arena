package com.example.arena;
import com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("id")
    public int id;

    @SerializedName("subject")
    public String subject;

    @SerializedName("question_text")
    public String questionText;

    @SerializedName("option_a")
    public String optionA;

    @SerializedName("option_b")
    public String optionB;

    @SerializedName("option_c")
    public String optionC;

    @SerializedName("option_d")
    public String optionD;

    @SerializedName("correct_option")
    public String correctOption;

    @SerializedName("explanation")
    public String explanation;
}