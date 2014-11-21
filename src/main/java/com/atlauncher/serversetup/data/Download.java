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

import java.nio.file.Path;

public class Download {
    private String filename;
    private String url;
    private String path;
    private String hash;

    public String getURL() {
        return url;
    }

    public Path getPath(Path basePath) {
        return basePath.resolve(this.path + this.filename);
    }

    public Downloadable getDownloadable(Path basePath) {
        return new Downloadable(this.url, this.getPath(basePath), this.hash);
    }

    public String getFilename() {
        return this.filename;
    }
}
