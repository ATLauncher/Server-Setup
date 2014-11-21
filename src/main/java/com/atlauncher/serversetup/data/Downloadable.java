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

import com.atlauncher.serversetup.utils.HashUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Downloadable {
    private HttpURLConnection connection;

    private String url;
    private Path saveTo;
    private String hash;

    private int size = -1;

    public Downloadable(String url, Path saveTo, String hash) {
        this.url = url;
        this.saveTo = saveTo;
        this.hash = hash;
    }

    public boolean isMD5() {
        return hash == null || hash.length() != 40;
    }

    public String getHashFromURL() throws IOException {
        String etag = null;
        etag = getConnection().getHeaderField("ETag");

        if (etag == null) {
            return "-";
        }

        if ((etag.startsWith("\"")) && (etag.endsWith("\""))) {
            etag = etag.substring(1, etag.length() - 1);
        }

        if (etag.matches("[A-Za-z0-9]{32}")) {
            return etag;
        } else {
            return "-";
        }
    }

    public int getFilesize() {
        if (this.size == -1) {
            int size = getConnection().getContentLength();
            if (size == -1) {
                this.size = 0;
            } else {
                this.size = size;
            }
        }
        return this.size;
    }

    public boolean needToDownload() {
        if (this.saveTo == null || this.hash.equalsIgnoreCase("-")) {
            return true;
        }

        if (Files.exists(this.saveTo)) {
            if (isMD5()) {
                if (HashUtils.getMD5(this.saveTo).equalsIgnoreCase(getHash())) {
                    return false;
                }
            } else {
                if (HashUtils.getSHA1(this.saveTo).equalsIgnoreCase(getHash())) {
                    return false;
                }
            }
        }

        return true;
    }

    public String getHash() {
        if (this.hash == null || this.hash.isEmpty()) {
            try {
                this.hash = getHashFromURL();
            } catch (IOException e) {
                e.printStackTrace();
                this.hash = "-";
                this.connection = null;
            }
        }
        return this.hash;
    }

    public HttpURLConnection getConnection() {
        if (this.connection == null) {
            try {
                this.connection = (HttpURLConnection) new URL(this.url).openConnection();
                this.connection.setUseCaches(false);
                this.connection.setDefaultUseCaches(false);
                this.connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) " +
                        "AppleWebKit/537" + ".36 (KHTML, like Gecko) Chrome/28.0.1500.72 Safari/537.36");
                this.connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
                this.connection.setRequestProperty("Expires", "0");
                this.connection.setRequestProperty("Pragma", "no-cache");
                this.connection.connect();

                if (this.connection.getResponseCode() / 100 != 2) {
                    throw new IOException(this.url + " returned response code " + this.connection.getResponseCode() +
                            (this.connection.getResponseMessage() != null ? " with message of " + this.connection
                                    .getResponseMessage() : ""));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.connection;
    }

    private void downloadFile() {
        InputStream in = null;
        OutputStream writer = null;
        try {
            in = getConnection().getInputStream();
            writer = Files.newOutputStream(this.saveTo);
            byte[] buffer = new byte[2048];
            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[2048];
            }
        } catch (SocketException e) {
            e.printStackTrace();
            // Connection reset. Close connection and try again
            this.connection.disconnect();
            this.connection = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public String getContents() {
        StringBuilder response;
        try {
            InputStream in = null;
            in = getConnection().getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            response = new StringBuilder();
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
                response.append('\n');
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        this.connection.disconnect();
        return response.toString();
    }

    public void download() {
        int attempts = 0;
        if (this.connection != null) {
            this.connection.disconnect();
            this.connection = null;
        }
        if (this.saveTo == null) {
            System.err.println("Can't download " + this.url + " as the passed in path was null!");
            return;
        }
        if (Files.exists(this.saveTo)) {
            try {
                Files.delete(this.saveTo);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            Files.createFile(this.saveTo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getHash().equalsIgnoreCase("-")) {
            // No hash so only download once
            downloadFile();
        } else {
            String fileHash = "0";
            boolean done = false;
            while (attempts <= 3) {
                attempts++;
                if (Files.exists(this.saveTo) && Files.isRegularFile(this.saveTo)) {
                    if (isMD5()) {
                        fileHash = HashUtils.getMD5(this.saveTo);
                    } else {
                        fileHash = HashUtils.getSHA1(this.saveTo);
                    }
                } else {
                    fileHash = "0";
                }
                if (fileHash.equalsIgnoreCase(getHash())) {
                    done = true;
                    break; // Hash matches, file is good
                }
                if (this.connection != null) {
                    this.connection.disconnect();
                    this.connection = null;
                }
                if (Files.exists(this.saveTo)) {
                    try {
                        Files.delete(this.saveTo);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                downloadFile();
            }
            if (!done) {
                System.err.println("Couldn't download " + this.url + " to " + this.saveTo.toAbsolutePath() + "!");
            }
        }

        if (this.connection != null) {
            this.connection.disconnect();
        }
    }

    public int getResponseCode() {
        try {
            return getConnection().getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean isDownloadable() {
        return getConnection().getContentType().equalsIgnoreCase("application/zip") || getConnection().getContentType
                ().equalsIgnoreCase("application/jar") || getConnection().getContentType().equalsIgnoreCase
                ("application/java-archive");
    }
}