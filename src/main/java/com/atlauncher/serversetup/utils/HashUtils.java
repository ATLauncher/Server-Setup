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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static String getMD5(Path path) {
        if (!Files.exists(path) && Files.isRegularFile(path)) {
            System.err.println("File at " + path.toAbsolutePath() + " doesn't exist!");
            return "0";
        }

        StringBuffer sb = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream fis = Files.newInputStream(path);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            sb = new StringBuffer();

            for (byte mdbyte : mdbytes) {
                sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
            }

            fis.close();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return sb != null ? sb.toString() : "0";
    }

    public static String getSHA1(Path path) {
        if (!Files.exists(path) && Files.isRegularFile(path)) {
            System.err.println("File at " + path.toAbsolutePath() + " doesn't exist!");
            return "0";
        }

        StringBuffer sb = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            InputStream fis = Files.newInputStream(path);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            sb = new StringBuffer();
            for (byte mdbyte : mdbytes) {
                sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
            }

            fis.close();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return sb != null ? sb.toString() : "0";
    }

    public static String getMD5(String string) {
        StringBuffer sb = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytesOfMessage = string.getBytes("UTF-8");
            byte[] mdbytes = md.digest(bytesOfMessage);

            // convert the byte to hex format method 1
            sb = new StringBuffer();
            for (byte mdbyte : mdbytes) {
                sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return sb != null ? sb.toString() : "0";
    }
}
