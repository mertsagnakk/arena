package com.example.arena; // KENDİ PAKET ADINI YAZMAYI UNUTMA!

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private boolean isTestMode = true;
    private String myPlayerId = "BURAYA_AKTIF_ID_YAZILACAK";

    // Arayüz Değişkenleri
    private TextView tvPlayer1Score, tvPlayer2Score, tvTimer, tvQuestion, tvQuestionCount;
    private MaterialCardView cardTimer;
    private LinearLayout optionsContainer;

    // Buğulu Ekran Değişkenleri
    private RelativeLayout overlayMatchmaking, overlayMatchResult;
    private TextView tvOverlayStatus, tvOverlayTimer;
    private MaterialCardView overlayTimerCard;

    // Maç Sonu Ekranı Değişkenleri
    private TextView tvResultTitle, tvResultWinnerText, tvResultMyScore, tvResultOpponentScore;
    private MaterialCardView btnReturnHome;

    // Şıklar
    private MaterialCardView[] optionCards = new MaterialCardView[4];
    private TextView[] optionTexts = new TextView[4];
    private MaterialCardView[] p1Avatars = new MaterialCardView[4];
    private MaterialCardView[] p2Avatars = new MaterialCardView[4];
    private View[] splitBgs = new View[4];

    private CountDownTimer countDownTimer;
    private int myScore = 0;
    private int opponentScore = 0;
    private String correctAnswer = "C)  300.000 km/s";
    private boolean isAnswered = false;

    // OYUN DÖNGÜSÜ DEĞİŞKENLERİ
    private int currentQuestionIndex = 1;
    private final int TOTAL_QUESTIONS = 1; // Test için 1 soruda maç biter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Bağlantıları
        tvPlayer1Score = findViewById(R.id.tvPlayer1Score);
        tvPlayer2Score = findViewById(R.id.tvPlayer2Score);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        cardTimer = findViewById(R.id.cardTimer);
        optionsContainer = findViewById(R.id.optionsContainer);

        overlayMatchmaking = findViewById(R.id.overlayMatchmaking);
        tvOverlayStatus = findViewById(R.id.tvOverlayStatus);
        tvOverlayTimer = findViewById(R.id.tvOverlayTimer);
        overlayTimerCard = findViewById(R.id.overlayTimerCard);

        overlayMatchResult = findViewById(R.id.overlayMatchResult);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultWinnerText = findViewById(R.id.tvResultWinnerText);
        tvResultMyScore = findViewById(R.id.tvResultMyScore);
        tvResultOpponentScore = findViewById(R.id.tvResultOpponentScore);
        btnReturnHome = findViewById(R.id.btnReturnHome);

        optionCards[0] = findViewById(R.id.cardOptionA); optionCards[1] = findViewById(R.id.cardOptionB); optionCards[2] = findViewById(R.id.cardOptionC); optionCards[3] = findViewById(R.id.cardOptionD);
        optionTexts[0] = findViewById(R.id.tvOptionA); optionTexts[1] = findViewById(R.id.tvOptionB); optionTexts[2] = findViewById(R.id.tvOptionC); optionTexts[3] = findViewById(R.id.tvOptionD);
        p1Avatars[0] = findViewById(R.id.avatarP1A); p1Avatars[1] = findViewById(R.id.avatarP1B); p1Avatars[2] = findViewById(R.id.avatarP1C); p1Avatars[3] = findViewById(R.id.avatarP1D);
        p2Avatars[0] = findViewById(R.id.avatarP2A); p2Avatars[1] = findViewById(R.id.avatarP2B); p2Avatars[2] = findViewById(R.id.avatarP2C); p2Avatars[3] = findViewById(R.id.avatarP2D);
        splitBgs[0] = findViewById(R.id.bgSplitA); splitBgs[1] = findViewById(R.id.bgSplitB); splitBgs[2] = findViewById(R.id.bgSplitC); splitBgs[3] = findViewById(R.id.bgSplitD);

        // Ana Menüye Dön
        btnReturnHome.setOnClickListener(v -> finish());

        setupClickListeners();

        if (isTestMode) {
            startMatchCountdown();
        }
    }

    private void startMatchCountdown() {
        overlayMatchmaking.setVisibility(View.VISIBLE);
        tvOverlayStatus.setText("Rakip Bulundu!");
        overlayTimerCard.setVisibility(View.VISIBLE);

        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);
                if (sec > 0) tvOverlayTimer.setText(String.valueOf(sec));
            }
            @Override
            public void onFinish() {
                overlayMatchmaking.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                    overlayMatchmaking.setVisibility(View.GONE);
                    overlayMatchmaking.setAlpha(1f);
                    startArenaUI();
                });
            }
        }.start();
    }

    private void startArenaUI() {
        resetOptionsUI();
        tvPlayer1Score.setText(String.valueOf(myScore));
        tvPlayer2Score.setText(String.valueOf(opponentScore));
        tvQuestionCount.setText("Soru " + currentQuestionIndex + "/" + TOTAL_QUESTIONS);
        startTimer();
    }

    private void setupClickListeners() {
        View.OnClickListener listener = view -> {
            if (isAnswered) return;
            isAnswered = true;
            if (countDownTimer != null) countDownTimer.cancel();

            int selectedIndex = (int) view.getTag();
            p1Avatars[selectedIndex].setVisibility(View.VISIBLE);

            checkAnswer(selectedIndex);

            new Handler().postDelayed(() -> simulateOpponentAnswer(selectedIndex), 1000);
        };

        for (int i = 0; i < 4; i++) {
            optionCards[i].setTag(i);
            optionCards[i].setOnClickListener(listener);
        }
    }

    private void checkAnswer(int index) {
        MaterialCardView clickedCard = optionCards[index];
        TextView clickedText = optionTexts[index];

        clickedCard.setStrokeWidth(6);
        clickedCard.setStrokeColor(Color.parseColor("#4F46E5"));

        if (clickedText.getText().toString().equals(correctAnswer)) {
            clickedCard.setCardBackgroundColor(Color.parseColor("#D1FAE5"));
            clickedText.setTextColor(Color.parseColor("#065F46"));
            myScore += 10;

            // SKOR BOUNCE (BÜYÜ-KÜÇÜL) ANİMASYONU
            tvPlayer1Score.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150).withEndAction(() ->
                    tvPlayer1Score.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
            ).start();
        } else {
            clickedCard.setCardBackgroundColor(Color.parseColor("#FEE2E2"));
            clickedText.setTextColor(Color.parseColor("#991B1B"));
            highlightCorrectOption();
        }
        tvPlayer1Score.setText(String.valueOf(myScore));
    }

    private void simulateOpponentAnswer(int userIndex) {
        int opponentIndex = new Random().nextInt(4);
        p2Avatars[opponentIndex].setVisibility(View.VISIBLE);

        MaterialCardView oppCard = optionCards[opponentIndex];
        TextView oppText = optionTexts[opponentIndex];

        if (userIndex == opponentIndex) {
            oppCard.setStrokeWidth(8);
            oppCard.setStrokeColor(Color.parseColor("#1E293B"));
            splitBgs[opponentIndex].setVisibility(View.VISIBLE);
            splitBgs[opponentIndex].setBackground(new DiagonalSplitDrawable());
            oppText.setTextColor(Color.WHITE);
        } else {
            oppCard.setStrokeWidth(6);
            oppCard.setStrokeColor(Color.parseColor("#64748B"));
            if (oppText.getText().toString().equals(correctAnswer)) {
                oppCard.setCardBackgroundColor(Color.parseColor("#D1FAE5"));
                oppText.setTextColor(Color.parseColor("#065F46"));
                opponentScore += 10;
            } else {
                oppCard.setCardBackgroundColor(Color.parseColor("#FEE2E2"));
                oppText.setTextColor(Color.parseColor("#991B1B"));
            }
            tvPlayer2Score.setText(String.valueOf(opponentScore));
        }

        // İŞTE UNUTTUĞUMUZ YER BURASIYDI! (Eskiden startMatchCountdown yazıyordu)
        new Handler().postDelayed(this::loadNextQuestion, 2500);
    }

    private void highlightCorrectOption() {
        for (int i = 0; i < 4; i++) {
            if (optionTexts[i].getText().toString().equals(correctAnswer)) {
                optionCards[i].setCardBackgroundColor(Color.parseColor("#D1FAE5"));
                optionTexts[i].setTextColor(Color.parseColor("#065F46"));
                optionCards[i].setStrokeWidth(6);
                optionCards[i].setStrokeColor(Color.parseColor("#10B981"));
            }
        }
    }

    private void resetOptionsUI() {
        isAnswered = false;
        for (int i = 0; i < 4; i++) {
            optionCards[i].setCardBackgroundColor(Color.WHITE);
            optionCards[i].setStrokeColor(Color.parseColor("#E2E8F0"));
            optionCards[i].setStrokeWidth(2);
            optionTexts[i].setTextColor(Color.parseColor("#334155"));
            p1Avatars[i].setVisibility(View.INVISIBLE);
            p2Avatars[i].setVisibility(View.INVISIBLE);
            splitBgs[i].setVisibility(View.GONE);
        }
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(12000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.valueOf(sec));
                if (sec <= 5) {
                    tvTimer.setTextColor(Color.parseColor("#EF4444"));
                    cardTimer.setStrokeColor(Color.parseColor("#EF4444"));
                } else {
                    tvTimer.setTextColor(Color.parseColor("#10B981"));
                    cardTimer.setStrokeColor(Color.parseColor("#10B981"));
                }
            }
            @Override
            public void onFinish() {
                tvTimer.setText("0");
                if (!isAnswered) {
                    isAnswered = true;
                    highlightCorrectOption();
                    new Handler().postDelayed(() -> simulateOpponentAnswer(-1), 1000);
                }
            }
        }.start();
    }

    private void loadNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex > TOTAL_QUESTIONS) {
            showMatchEndScreen(); // OYUN BİTTİ
        } else {
            tvQuestionCount.setText("Soru " + currentQuestionIndex + "/" + TOTAL_QUESTIONS);
            startMatchCountdown();
        }
    }

    private void showMatchEndScreen() {
        overlayMatchResult.setVisibility(View.VISIBLE);
        tvResultMyScore.setText(String.valueOf(myScore));
        tvResultOpponentScore.setText(String.valueOf(opponentScore));

        if (myScore > opponentScore) {
            tvResultTitle.setText("ZAFER!");
            tvResultTitle.setTextColor(Color.parseColor("#10B981"));
            tvResultWinnerText.setText("Kazanan: Mert");
        } else if (opponentScore > myScore) {
            tvResultTitle.setText("MAĞLUBİYET!");
            tvResultTitle.setTextColor(Color.parseColor("#EF4444"));
            tvResultWinnerText.setText("Kazanan: Rakip");
        } else {
            tvResultTitle.setText("BERABERLİK!");
            tvResultTitle.setTextColor(Color.parseColor("#F59E0B"));
            tvResultWinnerText.setText("Dostluk Kazandı");
        }
    }

    class DiagonalSplitDrawable extends Drawable {
        private Paint paintP1, paintP2, paintLine;
        public DiagonalSplitDrawable() {
            paintP1 = new Paint(Paint.ANTI_ALIAS_FLAG); paintP1.setColor(Color.parseColor("#E0E7FF"));
            paintP2 = new Paint(Paint.ANTI_ALIAS_FLAG); paintP2.setColor(Color.parseColor("#F1F5F9"));
            paintLine = new Paint(Paint.ANTI_ALIAS_FLAG); paintLine.setColor(Color.parseColor("#1E293B")); paintLine.setStrokeWidth(8f); paintLine.setStyle(Paint.Style.STROKE);
        }
        @Override
        public void draw(@NonNull Canvas canvas) {
            float width = getBounds().width(); float height = getBounds().height();
            Path path1 = new Path(); path1.moveTo(0, 0); path1.lineTo(width * 0.55f, 0); path1.lineTo(width * 0.45f, height); path1.lineTo(0, height); path1.close();
            Path path2 = new Path(); path2.moveTo(width * 0.55f, 0); path2.lineTo(width, 0); path2.lineTo(width, height); path2.lineTo(width * 0.45f, height); path2.close();
            canvas.drawPath(path1, paintP1); canvas.drawPath(path2, paintP2); canvas.drawLine(width * 0.55f, 0, width * 0.45f, height, paintLine);
        }
        @Override public void setAlpha(int i) {}
        @Override public void setColorFilter(@Nullable ColorFilter colorFilter) {}
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
    }
}