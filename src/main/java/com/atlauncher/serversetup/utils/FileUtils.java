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
package com.atlauncher.serversetup.utils;

import com.atlauncher.serversetup.Bootstrap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class FileUtils {
    public static BufferedImage getImage(String img) {
        try {
            String name;
            if (!img.startsWith("/assets/image/")) {
                name = "/assets/image/" + img;
            } else {
                name = img;
            }

            if (!name.endsWith(".png")) {
                name = name + ".png";
            }

            InputStream stream = Bootstrap.class.getResourceAsStream(name);

            if (stream == null) {
                throw new NullPointerException("Stream == null");
            }

            return ImageIO.read(stream);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
