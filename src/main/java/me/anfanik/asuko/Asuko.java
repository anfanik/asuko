package me.anfanik.asuko;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.anfanik.asuko.build.BuildFile;
import me.anfanik.asuko.build.FileCredentials;
import me.anfanik.asuko.image.ImageWatcher;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Asuko {

    private static final File configFile = new File("asuko.conf");

    public static void main(String[] args) {
        AsukoLogger.info("🔥 Starting Asuko.");
        AsukoLogger.info("👋 Hi, cutie!");

        Config config = ConfigFactory.parseFile(configFile);
        checkConfigFields(config, "main");

        List<? extends Config> files;
        if (config.hasPath("files")) {
            files = config.getConfigList("files");
        } else {
            files = Collections.emptyList();
        }
        ImageWatcher watcher = ImageWatcher.fromFiles(files);
        watcher.start();

        String target = config.getString("main");
        Class<?> clazz;
        try {
            clazz = Class.forName(target);
        } catch (ClassNotFoundException exception) {
            AsukoLogger.error("Class %s is not found!", exception, target);
            return;
        }

        MethodHandle mainMethod;
        try {
            mainMethod = MethodHandles.publicLookup().findStatic(clazz, "main", MethodType.methodType(Void.TYPE, String[].class));
        } catch (NoSuchMethodException exception) {
            AsukoLogger.error("Method main method in class %s is not found!", exception, target);
            return;
        } catch (IllegalAccessException exception) {
            AsukoLogger.error("Unable to access to main method in class %s!", exception, target);
            return;
        }

        AsukoLogger.info("🛠 Launching application by %s class.", target);

        try {
            //noinspection ConfusingArgumentToVarargsMethod
            mainMethod.invokeExact(args);
        } catch (Throwable throwable) {
            AsukoLogger.error("Unable to launch application because of unknown exception!", throwable);
        }
    }

    private static Supplier<Boolean> isCanRestart = () -> true;
    private static boolean restartRequired = false;

    public static void setIsCanRestart(Supplier<Boolean> isCanRestart) {
        Asuko.isCanRestart = isCanRestart;
    }

    public static void setRestartRequired() {
        restartRequired = true;
        if (isCanRestart.get()) {
            performRestart();
        }
    }

    public static void tryToRestart() {
        if (restartRequired) {
            performRestart();
        }
    }

    public static void performRestart() {
        System.exit(0);
    }

    private static List<BuildFile> loadFiles(List<? extends Config> files) {
        List<BuildFile> result = new ArrayList<>();

        for (Config fileConfig : files) {
            checkConfigFields(fileConfig, "id", "url", "destination");

            String id = fileConfig.getString("id");
            String url = fileConfig.getString("url");
            String hashUrl = fileConfig.getString("hash");
            File destination = new File(fileConfig.getString("destination"));

            FileCredentials credentials = null;
            if (fileConfig.hasPath("credentials")) {
                Config credentialsConfig = fileConfig.getConfig("credentials");
                checkConfigFields(credentialsConfig, "username", "password");

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

    public static void checkConfigFields(Config config, String... fields) {
        for (String field : fields) {
            if (!config.hasPath(field)) {
                AsukoLogger.error("\"%s\" field is not set!", field);
                System.exit(0);
            }
        }
    }

}
