/*
 * ATLauncher Server Setup - https://github.com/ATLauncher/Server-Setup
 * Copyright (C) 2014 ATLauncher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.atlauncher.serversetup.data;

import java.util.List;

public class Pack {
    private List<Download> toDownload;
    private int permGen;
    private int minimumRam;
    private String extraArguments;
    private String serverJar;

    public List<Download> getDownloads() {
        return this.toDownload;
    }

    public int getPermGen() {
        return permGen;
    }

    public int getMinimumRam() {
        return minimumRam;
    }

    public String getExtraArguments() {
        return extraArguments;
    }

    public String getServerJar() {
        return serverJar;
    }
}
