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

package dev.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

@ConfHeader("# Database config. Only MySQL & MariaDB are supported.")
public interface SQLConfig {


    @ConfDefault.DefaultBoolean(false)
    @ConfComments("# Should we use a database?")
    @AnnotationBasedSorter.Order(1)
    boolean isEnabled();

    @ConfDefault.DefaultString("localhost")
    @ConfComments("# Host for your database, usually localhost.")
    @AnnotationBasedSorter.Order(2)
    String Host();

    @ConfDefault.DefaultInteger(3306)
    @ConfComments("# Port for your Database, usually 3306.")
    @AnnotationBasedSorter.Order(3)
    int Port();

    @ConfDefault.DefaultString("root")
    @ConfComments("# Username used to login to database.")
    @AnnotationBasedSorter.Order(4)
    String UserName();

    @ConfDefault.DefaultString("password")
    @ConfComments("# Password used to login to database.")
    @AnnotationBasedSorter.Order(5)
    String Password();

    @ConfDefault.DefaultString("DiscordSRVUtilsData")
    @ConfComments("# Database name. The host should tell you the name normally.")
    @AnnotationBasedSorter.Order(6)
    String DatabaseName();
}
