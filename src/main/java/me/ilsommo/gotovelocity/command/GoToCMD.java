package me.ilsommo.gotovelocity.command;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.ilsommo.gotovelocity.GoToVelocity;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import java.util.Optional;

public record GoToCMD(ProxyServer proxyServer, GoToVelocity GoToVelocity, Logger logger, Toml configData) implements SimpleCommand {

    public GoToCMD(ProxyServer proxyServer, GoToVelocity GoToVelocity, Logger logger, Toml configData) {
        this.proxyServer = proxyServer;
        this.GoToVelocity = GoToVelocity;
        this.logger = logger;
        this.configData = configData;
        CommandManager manager = proxyServer.getCommandManager();
        manager.register(manager.metaBuilder("goto").build(), this);
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component.text(configData.getString("messages.run-from-player")));
            return;
        }
        Player player = (Player) invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 1) {
            if (player.hasPermission("gotovelocity.goto")) {
                Optional<RegisteredServer> optionalServer = proxyServer.getServer(args[0]);
                if (optionalServer.isPresent()) {
                    RegisteredServer registeredServer = optionalServer.get();
                    player.createConnectionRequest(registeredServer).fireAndForget();
                } else {
                    player.sendMessage(Component.text(configData.getString("messages.server-not-found")));
                }
            } else {
                player.sendMessage(Component.text(configData.getString("messages.no-permission")));
            }
        } else {
            player.sendMessage(Component.text(configData.getString("messages.must-specify-server")));
        }
    }
}