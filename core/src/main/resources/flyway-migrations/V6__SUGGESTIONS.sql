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

CREATE TABLE suggestions
(
    SuggestionNumber int PRIMARY KEY,
    SuggestionText   LONGTEXT,
    Submitter        BIGINT,
    MessageID        BIGINT,
    ChannelID        BIGINT,
    CreationTime     BIGINT,
    Approved         varchar(5),
    Approver         BIGINT
);

CREATE TABLE suggestion_notes
(
    StaffID          BIGINT,
    NoteText         LONGTEXT,
    SuggestionNumber int,
    CreationTime     BIGINT
)