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
package com.atlauncher.serversetup;

import com.atlauncher.serversetup.data.Download;
import com.atlauncher.serversetup.data.Pack;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Bootstrap {
    public static Path basePath = Paths.get("");
    public static Path jsonFile = Paths.get("libraries/pack.json");
    public static final Gson GSON = new Gson();
    public static Pack pack;

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH); // Set English as the default locale
        System.setProperty("java.net.preferIPv4Stack", "true");

        if (!Files.exists(jsonFile)) {
            System.err.println("Error setting up the server. The file " + jsonFile.toAbsolutePath() + " doesn't " +
                    "exist!");
        }

        BufferedReader reader = null;

        try {
            reader = Files.newBufferedReader(jsonFile, StandardCharsets.UTF_8);
            pack = GSON.fromJson(reader, Pack.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        System.out.println("Setting up the server!");

        for (Download download : pack.getDownloads()) {
            System.out.println("Downloading " + download.getURL() + " to " + download.getPath(basePath)
                    .toAbsolutePath());
            download.getDownloadable(basePath).download();
        }

        System.out.println("Server has now been setup! You can now run " + pack.getServerJar() + " to start the " +
                "server or alternatively use the LaunchServer script!");
    }
}