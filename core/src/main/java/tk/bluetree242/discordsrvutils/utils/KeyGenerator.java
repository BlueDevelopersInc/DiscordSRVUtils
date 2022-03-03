/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

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
        for (int i = 0; i < length; i++) {
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
