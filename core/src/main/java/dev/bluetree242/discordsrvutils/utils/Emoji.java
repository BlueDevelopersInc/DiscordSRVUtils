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

package dev.bluetree242.discordsrvutils.utils;

import lombok.Getter;

public class Emoji {


    @Getter
    private final String nameInText;
    @Getter
    private final String name;
    @Getter
    private final String nameInReaction;
    private final boolean emote;
    private Long id;
    private boolean animated;

    public Emoji(String unicode) {
        this.emote = false;
        this.nameInText = unicode;
        this.name = unicode;
        this.nameInReaction = unicode;
    }

    Emoji(long id, String name, boolean animated) {
        this.id = id;
        this.animated = animated;
        this.emote = true;
        this.nameInText = "<" + (animated ? "a" : "") + ":" + name + ":" + id + ">";
        this.name = name;
        this.nameInReaction = (animated ? "a:" : "") + name + ":" + id;
    }

    @Override
    public String toString() {
        return nameInText;
    }

    public github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji toJDAEmoji() {
        if (emote) {
            return github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji.fromEmote(name, id, animated);
        } else {
            return github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji.fromUnicode(name);
        }
    }


}
