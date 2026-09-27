package com.example.arena; // KENDİ PAKET ADINI YAZMAYI UNUTMA!

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private MaterialCardView cardHistory, cardGeo, cardMixed, btnFindMatch, btnSoloPractice;
    private MaterialCardView btnPrevCategory, btnNextCategory;
    private TextView tvCatHistory, tvCatGeo, tvCatMixed;

    // HTML'deki gibi varsayılan seçili alan Coğrafya
    private String selectedCategory = "Coğrafya";
    private int currentCategoryIndex = 1; // 0: Tarih, 1: Coğrafya, 2: Karışık

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
}