package com.example.arena;

import java.util.Random;

/**
 * Arkadaşla paylaşılacak, karışıklık yaratmayan (0/O, 1/I gibi harfler hariç)
 * 4 karakterlik oda kodu üretir. Örn: "7F3K"
 */
public final class RoomCodeGenerator {

    private static final String ALPHABET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 4;
    private static final Random random = new Random();

    private RoomCodeGenerator() {}

    public static String generate() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
