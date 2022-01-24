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

package tk.bluetree242.discordsrvutils.exceptions;

import space.arim.dazzleconf.error.ConfigFormatSyntaxException;

public class ConfigurationLoadException extends RuntimeException {

    private final Throwable cause;
    private final String confname;

    public ConfigurationLoadException(Throwable ex, String confname) {
        this.cause = ex;
        this.confname = confname;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {

        return "Error Loading/Reloading the " + confname + "." + (cause instanceof ConfigFormatSyntaxException ? " Check the syntax at https://yaml-online-parser.appspot.com/" : " Please make sure all options have right types");
    }

    public String getConfigName() {
        return confname;
    }
}
