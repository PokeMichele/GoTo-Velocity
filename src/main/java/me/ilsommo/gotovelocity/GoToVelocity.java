package me.ilsommo.gotovelocity;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.ilsommo.gotovelocity.command.GoToCMD;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Plugin(id = "gotovelocity", name = "GoToVelocity", version = "1.0", authors = "Il Sommo")
public class GoToVelocity {

    private static GoToVelocity instance;

    private Logger logger;

    @Inject
    private ProxyServer proxyServer;
    private ProxyServer server;

    private Toml configData;

    @Inject @DataDirectory
    private Path dataDirectory;
    @Inject
    public GoToVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        loadConfig();
        printAsciiArt();
        registerCommands();
    }

    private void registerCommands() {
        new GoToCMD(server, this, logger, configData);
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
    private void printAsciiArt() {
        String asciiArt =
                "________  ________  _________  ________ \n" +
                "|\\   ____\\|\\   __  \\|\\___   ___\\\\   __  \\  \n" +
                "\\ \\  \\___|\\ \\  \\|\\  \\|___ \\  \\_\\ \\  \\|\\  \\    \n" +
                " \\ \\  \\  __\\ \\  \\\\\\  \\   \\ \\  \\ \\ \\  \\\\\\  \\   \n" +
                "  \\ \\  \\|\\  \\ \\  \\\\\\  \\   \\ \\  \\ \\ \\  \\\\\\  \\  \n" +
                "   \\ \\_______\\ \\_______\\   \\ \\__\\ \\ \\_______\\ \\n" +
                "    \\|_______|\\|_______|    \\|__|  \\|_______| \n" +
                " ___      ___ _______   ___       ________  ________  ___  _________    ___    ___ \n" +
                "|\\  \\    /  /|\\  ___ \\ |\\  \\     |\\   __  \\|\\   ____\\|\\  \\|\\___   ___\\ |\\  \\  /  /| \n" +
                "\\ \\  \\  /  / | \\   __/|\\ \\  \\    \\ \\  \\|\\  \\ \\  \\___|\\ \\  \\|___ \\  \\_| \\ \\  \\/  / / \n" +
                " \\ \\  \\/  / / \\ \\  \\_|/_\\ \\  \\    \\ \\  \\\\\\  \\ \\  \\    \\ \\  \\   \\ \\  \\   \\ \\    / / \n" +
                "  \\ \\    / /   \\ \\  \\_|\\ \\ \\  \\____\\ \\  \\\\\\  \\ \\  \\____\\ \\  \\   \\ \\  \\   \\/  /  / \n" +
                "   \\ \\__/ /     \\ \\_______\\ \\_______\\ \\_______\\ \\_______\\ \\__\\   \\ \\__\\__/  / / \n" +
                "    \\|__|/       \\|_______|\\|_______|\\|_______|\\|_______|\\|__|    \\|__|\\___/ / \n" +
                "                                                                      \\|___|/";
        logger.info("\n" + asciiArt + "\n");
    }

    private void loadConfig() {
        File configFile = dataDirectory.resolve("config.toml").toFile();
        if (!configFile.exists()) {
            File configDirectory = configFile.getParentFile();
            if (!configDirectory.exists()) {
                configDirectory.mkdirs();
            }
            try (InputStream inputStream = getClass().getResourceAsStream("/config.toml");
                 FileOutputStream outputStream = new FileOutputStream(configFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                logger.error("Error creating or copying config file: ", e);
            }
        }

        configData = new Toml().read(configFile);
    }
}

