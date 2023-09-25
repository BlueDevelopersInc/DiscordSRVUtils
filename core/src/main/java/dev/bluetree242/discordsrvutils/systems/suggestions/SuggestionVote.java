/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.systems.suggestions;

public class SuggestionVote {

    private final Long id;
    private final int SuggestionNumber;
    private final boolean agree;

    public SuggestionVote(Long id, int suggestionNumber, boolean agree) {
        this.id = id;
        SuggestionNumber = suggestionNumber;
        this.agree = agree;
    }

    public Long getId() {
        return id;
    }

    public int getSuggestionNumber() {
        return SuggestionNumber;
    }

    public boolean isAgree() {
        return agree;
    }
}
