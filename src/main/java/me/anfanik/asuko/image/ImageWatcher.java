package me.anfanik.asuko.image;

import com.typesafe.config.Config;
import me.anfanik.asuko.Asuko;
import me.anfanik.asuko.AsukoLogger;
import me.anfanik.asuko.build.BuildFile;
import me.anfanik.asuko.build.FileCredentials;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ImageWatcher {

    private final List<BuildFile> files;

    public ImageWatcher(List<BuildFile> files) {
        this.files = files;
    }

    public void start() {
        downloadFiles(files);

        long period = TimeUnit.SECONDS.toMillis(10);
        new Timer("Asuko Timer", true).schedule(new TimerTask() {
            @Override
            public void run() {
                files.forEach(file -> {
                    if (file.isUpdated()) {
                        AsukoLogger.info("\"%s\" file is updated! Restarting.", file.getId());
                        System.exit(0);
                    }
                });
            }
        }, period, period);
    }

    public static ImageWatcher fromFiles(List<? extends Config> files) {
        List<BuildFile> imageFiles = loadFiles(files);
        return new ImageWatcher(imageFiles);
    }

    private static List<BuildFile> loadFiles(List<? extends Config> files) {
        List<BuildFile> result = new ArrayList<>();

        for (Config fileConfig : files) {
            Asuko.checkConfigFields(fileConfig, "id", "url", "hash", "destination");

            String id = fileConfig.getString("id");
            String url = fileConfig.getString("url");
            String hashUrl = fileConfig.getString("hash");
            File destination = new File(fileConfig.getString("destination"));

            FileCredentials credentials = null;
            if (fileConfig.hasPath("credentials")) {
                Config credentialsConfig = fileConfig.getConfig("credentials");
                Asuko.checkConfigFields(credentialsConfig, "username", "password");

                String username = credentialsConfig.getString("username");
                String password = credentialsConfig.getString("password");
                credentials = new FileCredentials(username, password);
            }

            BuildFile file = new BuildFile(id, url, hashUrl, destination, credentials);
            result.add(file);
        }

        return result;
    }

    private static void downloadFiles(List<BuildFile> files) {
        for (BuildFile file : files) {
            if (!file.download()) {
                AsukoLogger.error("Unable to download \"%s\" file!", file.getId());
                System.exit(0);
                return;
            }
        }
    }

}
