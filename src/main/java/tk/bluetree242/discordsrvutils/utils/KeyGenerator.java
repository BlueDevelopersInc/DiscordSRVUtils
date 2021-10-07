package tk.bluetree242.discordsrvutils.utils;

import java.security.SecureRandom;

public class KeyGenerator {
    private boolean includeUniqueCharacters = false;
    private boolean includeUpperCasedCharacters = false;
    private int length = 10;

    public KeyGenerator includeUniqueCharacters(boolean b) {
        this.includeUniqueCharacters = b;
        return this;
    }
    public KeyGenerator includeUpperCasedCharacters(boolean b) {
        includeUpperCasedCharacters = b;
        return this;
    }

    public KeyGenerator length(int length) {
        if (length > 32) throw new IllegalArgumentException("Length may not be more than 32!");
        this.length = length;
        return this;
    }


    public String create() {
        String charsUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        String charsLower = charsUpper.toLowerCase();
        String unique = "!@#$%^&*()_+";
        String use = charsLower;
        if (includeUpperCasedCharacters) use = use + charsUpper;
        if (includeUniqueCharacters) use = use + unique;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int randomIndex = random.nextInt(use.length());
            sb.append(use.charAt(randomIndex));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return create();
    }
}
