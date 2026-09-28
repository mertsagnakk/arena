package com.example.arena; // KENDİ PAKET ADINI YAZMAYI UNUTMA!

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MaterialCardView cardHistory, cardGeo, cardMixed, btnFindMatch, btnSoloPractice;
    private MaterialCardView btnPrevCategory, btnNextCategory;
    private MaterialCardView cardCreateRoom, cardJoinRoom;
    private TextView tvCatHistory, tvCatGeo, tvCatMixed;

    // HTML'deki gibi varsayılan seçili alan Coğrafya
    private String selectedCategory = "Coğrafya";
    private int currentCategoryIndex = 1; // 0: Tarih, 1: Coğrafya, 2: Karışık

    // TODO: Gerçek auth akışına bağlayın (örn. Supabase session kullanıcı id'si).
    // MainActivity'deki myPlayerId ile aynı mantık - şimdilik sabit/placeholder.
    private String myPlayerId = "75190bf3-db9f-4e79-9eb7-444213f29dce";

    private SupabaseApi api;

    // Oda kurucusu bekleme ekranındayken kullanılan polling altyapısı
    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private Runnable pollRunnable;
    private static final long POLL_INTERVAL_MS = 2500;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        api = SupabaseClient.getClient().create(SupabaseApi.class);

        cardHistory = view.findViewById(R.id.cardCategoryHistory);
        cardGeo = view.findViewById(R.id.cardCategoryGeo);
        cardMixed = view.findViewById(R.id.cardCategoryMixed);

        tvCatHistory = view.findViewById(R.id.tvCatHistory);
        tvCatGeo = view.findViewById(R.id.tvCatGeo);
        tvCatMixed = view.findViewById(R.id.tvCatMixed);

        btnPrevCategory = view.findViewById(R.id.btnPrevCategory);
        btnNextCategory = view.findViewById(R.id.btnNextCategory);

        btnFindMatch = view.findViewById(R.id.btnFindMatch);
        btnSoloPractice = view.findViewById(R.id.btnSoloPractice);

        cardCreateRoom = view.findViewById(R.id.cardCreateRoom);
        cardJoinRoom = view.findViewById(R.id.cardJoinRoom);

        // Tıklamalar
        cardHistory.setOnClickListener(v -> setCategory(0));
        cardGeo.setOnClickListener(v -> setCategory(1));
        cardMixed.setOnClickListener(v -> setCategory(2));

        btnPrevCategory.setOnClickListener(v -> {
            if (currentCategoryIndex > 0) setCategory(currentCategoryIndex - 1);
            else setCategory(2);
        });

        btnNextCategory.setOnClickListener(v -> {
            if (currentCategoryIndex < 2) setCategory(currentCategoryIndex + 1);
            else setCategory(0);
        });

        // 1v1 Maç Arama
        btnFindMatch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("SELECTED_CATEGORY", selectedCategory);
            startActivity(intent);
        });

        // Solo Pratik (Şimdilik Sadece Toast Mesajı)
        btnSoloPractice.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Solo Pratik Modu Yakında!", Toast.LENGTH_SHORT).show();
        });

        // Arkadaşla oda kur / katıl
        cardCreateRoom.setOnClickListener(v -> createRoom());
        cardJoinRoom.setOnClickListener(v -> showJoinRoomDialog());

        return view;
    }

    private void setCategory(int index) {
        currentCategoryIndex = index;

        // Hepsini varsayılan soluk duruma getir
        resetCard(cardHistory, tvCatHistory);
        resetCard(cardGeo, tvCatGeo);
        resetCard(cardMixed, tvCatMixed);

        // Seçilen kartı vurgula
        if (index == 0) {
            selectedCategory = "Tarih";
            highlightCard(cardHistory, tvCatHistory);
        } else if (index == 1) {
            selectedCategory = "Coğrafya";
            highlightCard(cardGeo, tvCatGeo);
        } else {
            selectedCategory = "Karışık";
            highlightCard(cardMixed, tvCatMixed);
        }
    }

    private void resetCard(MaterialCardView card, TextView text) {
        card.setStrokeWidth(0);
        card.setCardBackgroundColor(Color.parseColor("#1A1D29"));
        card.setAlpha(0.55f);
        text.setTextColor(Color.parseColor("#9A9CAB"));
        text.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void highlightCard(MaterialCardView card, TextView text) {
        card.setStrokeWidth(4); // 2dp XML karşılığı
        card.setStrokeColor(Color.parseColor("#4FC3F7"));
        card.setCardBackgroundColor(Color.parseColor("#1E2A3A"));
        card.setAlpha(1.0f);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    // ==========================================================
    //  ODA KUR
    // ==========================================================

    private void createRoom() {
        String roomCode = RoomCodeGenerator.generate();

        Duel newDuel = new Duel();
        newDuel.player1Id = myPlayerId;
        newDuel.status = "waiting";
        newDuel.mode = "private";
        newDuel.roomCode = roomCode;

        api.createDuel(newDuel).enqueue(new Callback<List<Duel>>() {
            @Override
            public void onResponse(Call<List<Duel>> call, Response<List<Duel>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    Toast.makeText(getContext(), "Oda oluşturulamadı, tekrar deneyin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Duel created = response.body().get(0);
                showWaitingDialog(created.id, roomCode);
            }

            @Override
            public void onFailure(Call<List<Duel>> call, Throwable t) {
                Toast.makeText(getContext(), "Bağlantı hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Oda kodu gösterilen ve rakip katılana kadar arka planda yoklama (polling) yapan diyalog. */
    private void showWaitingDialog(String duelId, String roomCode) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_room_waiting, null);
        TextView tvRoomCode = dialogView.findViewById(R.id.tvRoomCode);
        TextView tvWaitingStatus = dialogView.findViewById(R.id.tvWaitingStatus);
        MaterialCardView btnCancel = dialogView.findViewById(R.id.btnCancelRoom);

        tvRoomCode.setText(roomCode);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> {
            stopPolling();
            dialog.dismiss();
            // Not: burada isterseniz duels satırını status="cancelled" yaparak da temizleyebilirsiniz.
        });

        pollRunnable = new Runnable() {
            @Override
            public void run() {
                api.getDuelById("eq." + duelId).enqueue(new Callback<List<Duel>>() {
                    @Override
                    public void onResponse(Call<List<Duel>> call, Response<List<Duel>> response) {
                        if (!dialog.isShowing()) return; // kullanıcı iptal etti, artık ilgilenme

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Duel duel = response.body().get(0);
                            if ("active".equals(duel.status) && duel.player2Id != null) {
                                stopPolling();
                                dialog.dismiss();
                                goToMatch(duel);
                                return;
                            }
                        }
                        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                    }

                    @Override
                    public void onFailure(Call<List<Duel>> call, Throwable t) {
                        if (!dialog.isShowing()) return;
                        tvWaitingStatus.setText("bağlantı sorunu, yeniden deneniyor...");
                        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                    }
                });
            }
        };
        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);

        dialog.show();
    }

    private void stopPolling() {
        if (pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
            pollRunnable = null;
        }
    }

    // ==========================================================
    //  ODAYA KATIL
    // ==========================================================

    private void showJoinRoomDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_join_room, null);
        EditText etRoomCode = dialogView.findViewById(R.id.etRoomCode);
        TextView tvJoinError = dialogView.findViewById(R.id.tvJoinError);
        MaterialCardView btnConfirm = dialogView.findViewById(R.id.btnConfirmJoin);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnConfirm.setOnClickListener(v -> {
            String code = etRoomCode.getText().toString().trim().toUpperCase();
            if (code.length() != 4) {
                tvJoinError.setText("kod 4 karakter olmalı");
                tvJoinError.setVisibility(View.VISIBLE);
                return;
            }
            tvJoinError.setVisibility(View.GONE);
            joinRoom(code, dialog, tvJoinError);
        });

        dialog.show();
    }

    private void joinRoom(String code, AlertDialog dialog, TextView tvJoinError) {
        api.findRoomByCode("eq." + code, "eq.waiting", "eq.private").enqueue(new Callback<List<Duel>>() {
            @Override
            public void onResponse(Call<List<Duel>> call, Response<List<Duel>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    tvJoinError.setText("kod bulunamadı ya da oda dolu");
                    tvJoinError.setVisibility(View.VISIBLE);
                    return;
                }

                Duel found = response.body().get(0);

                Duel updateBody = new Duel();
                updateBody.player2Id = myPlayerId;
                updateBody.status = "active";

                api.joinRoomById("eq." + found.id, updateBody).enqueue(new Callback<List<Duel>>() {
                    @Override
                    public void onResponse(Call<List<Duel>> call, Response<List<Duel>> response) {
                        if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                            tvJoinError.setText("odaya katılırken bir sorun oluştu");
                            tvJoinError.setVisibility(View.VISIBLE);
                            return;
                        }
                        dialog.dismiss();
                        goToMatch(response.body().get(0));
                    }

                    @Override
                    public void onFailure(Call<List<Duel>> call, Throwable t) {
                        tvJoinError.setText("bağlantı hatası: " + t.getMessage());
                        tvJoinError.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Duel>> call, Throwable t) {
                tvJoinError.setText("bağlantı hatası: " + t.getMessage());
                tvJoinError.setVisibility(View.VISIBLE);
            }
        });
    }

    // ==========================================================

    /**
     * Not: MainActivity şu an "isTestMode" ile simüle edilmiş bir maç akışı çalıştırıyor
     * (bkz. MainActivity.java). Bu yüzden burada duel id/rakip bilgisini extra olarak
     * gönderiyoruz ama MainActivity henüz bunları okuyup gerçek Supabase eşleşmesine
     * bağlanmıyor — bu ayrı bir sonraki adım.
     */
    private void goToMatch(Duel duel) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("DUEL_ID", duel.id);
        intent.putExtra("SELECTED_CATEGORY", selectedCategory);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling(); // fragment kapanırken polling'i durdur, sızıntı olmasın
    }
}
