/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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

import lombok.Getter;

public class SuggestionNote {

    @Getter
    private final Long staffID;
    private final String NoteText;
    @Getter
    private final int SuggestionNumber;
    @Getter
    private final Long CreationTime;

    public SuggestionNote(Long staffID, String noteText, int suggestionNumber, Long creationTime) {
        this.staffID = staffID;
        NoteText = noteText;
        SuggestionNumber = suggestionNumber;
        CreationTime = creationTime;
    }

    public String getText() {
        return NoteText;
    }


}
