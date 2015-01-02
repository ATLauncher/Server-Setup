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
import com.atlauncher.serversetup.data.Downloadable;
import com.atlauncher.serversetup.data.Pack;
import com.atlauncher.serversetup.gui.Browser;
import com.atlauncher.serversetup.utils.FileUtils;
import com.google.gson.Gson;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Bootstrap {
    public static Path basePath = Paths.get("");
    public static Path jsonFile = basePath.resolve("pack.json");
    public static final Gson GSON = new Gson();
    public static boolean isHeadless = true;
    public static Pack pack;
    public static JLabel doingLabel;
    public static JProgressBar progressBar;

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH); // Set English as the default locale
        System.setProperty("java.net.preferIPv4Stack", "true");

        try {
            isHeadless = GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance();
        } catch (Exception e) {
            e.printStackTrace();
            isHeadless = true;
        }

        if (!Files.exists(jsonFile)) {
            System.err.println("Error setting up the server. The file " + jsonFile.toAbsolutePath() + " doesn't " +
                    "exist!");
        }

        if (isHeadless) {
            System.out.println("No graphics environment detected, so running command line only!");
        } else {
            System.out.println("Graphics environment detected, so running with GUI!");
            final JFrame frame = new JFrame();
            frame.setIconImage(FileUtils.getImage("/assets/image/Icon.png"));
            progressBar = new JProgressBar();
            progressBar.setMaximum(100);
            doingLabel = new JLabel("Setting up the server!", SwingConstants.CENTER);

            frame.setTitle("ATLauncher Server Setup");
            frame.setSize(500, 100);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(progressBar, BorderLayout.SOUTH);
            frame.getContentPane().add(doingLabel, BorderLayout.NORTH);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setVisible(true);
                }
            });
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
            if (isHeadless && download.isBrowserDownload()) {
                System.err.println("In headless environment so cannot download " + download.getURL() + " to " +
                        download.getPath(basePath).toAbsolutePath() + "! Please do this manually!");
                continue;
            }

            if (!isHeadless) {
                doingLabel.setText("Downloading " + download.getFilename());
                progressBar.setValue(30);
            }

            if (download.isBrowserDownload()) {
                Browser browser = new Browser(download.getPath(basePath));
                browser.setVisible(true);
                browser.loadURL(download.getURL());
                while (!browser.waiting) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Downloading " + download.getURL() + " to " + download.getPath(basePath)
                        .toAbsolutePath());
                download.getDownloadable(basePath).download();
            }
        }

        if (!isHeadless) {
            doingLabel.setText("Creating LaunchServer scripts!");
            progressBar.setValue(60);
        }

        Path launchServerBatPath = basePath.resolve("LaunchServer.bat");
        String launchServerBat = new Downloadable("http://download.nodecdn" +
                ".net/containers/atl/serversetup/LaunchServer" + ".bat", null, null).getContents();
        BufferedWriter bw = null;
        try {
            bw = Files.newBufferedWriter(launchServerBatPath, StandardCharsets.UTF_8);
            bw.write(launchServerBat.replace("%%SERVERJAR%%", pack.getServerJar()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Path launchServerShPath = basePath.resolve("LaunchServer.sh");
        String launchServerSh = new Downloadable("http://download.nodecdn" +
                ".net/containers/atl/serversetup/LaunchServer" + ".sh", null, null).getContents();
        bw = null;
        try {
            bw = Files.newBufferedWriter(launchServerShPath, StandardCharsets.UTF_8);
            bw.write(launchServerSh.replace("%%SERVERJAR%%", pack.getServerJar()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        System.out.println("Server has now been setup! You can now run " + pack.getServerJar() + " to start the " +
                "server or alternatively use the LaunchServer script!");

        if (!isHeadless) {
            doingLabel.setText("Server finished setup!");
            progressBar.setValue(100);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.exit(0);
    }
}