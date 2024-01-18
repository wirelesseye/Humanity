package io.github.wirelesseye.humanity.util;

import io.github.wirelesseye.humanity.Humanity;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class NameGenerator {
    static private final int NUM_FIRSTNAMES = 2000;
    static private final int NUM_LASTNAMES = 1000;

    private final Random random = new Random();

    public String generateFirstName() {
        int n = this.random.nextInt(NUM_FIRSTNAMES);
        InputStream is = NameGenerator.class.getResourceAsStream("/assets/humanity/data/firstnames.txt");
        return getRandomName(n, is);
    }

    public String generateLastName() {
        int n = this.random.nextInt(NUM_LASTNAMES);
        InputStream is = NameGenerator.class.getResourceAsStream("/assets/humanity/data/lastnames.txt");
        return getRandomName(n, is);
    }

    @Nullable
    private static String getRandomName(int n, InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String name;
            int i = 0;

            while ((name = reader.readLine()) != null) {
                if (i == n) {
                    return name;
                }
                i++;
            }

            return name;
        } catch (IOException e) {
            Humanity.LOGGER.error(e.toString());
        }

        return null;
    }
}
