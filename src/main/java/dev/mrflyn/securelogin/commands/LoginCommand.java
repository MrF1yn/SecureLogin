package dev.mrflyn.securelogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.mrflyn.securelogin.LoginData;
import dev.mrflyn.securelogin.SecureLogin;
import dev.mrflyn.securelogin.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class LoginCommand {

    public LoginCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("login")
                .requires(CommandSourceStack::isPlayer)
                .then(Commands.argument("password", StringArgumentType.word()).executes(this::onCommand)));
    }

    private int onCommand(CommandContext<CommandSourceStack> command) {
        ServerPlayer player = command.getSource().getPlayer();

        if(!SecureLogin.unAuthPlayers.containsKey(player)){
            player.sendSystemMessage(Component.literal(ChatFormatting.RED +
                    "You are already logged in!"));
            return Command.SINGLE_SUCCESS;
        }
        if (!SecureLogin.mod.db.isPresent(player.getUUID())) {
            player.sendSystemMessage(Component.literal(ChatFormatting.RED +
                    "You are not already registered. Register yourself with /register <password> <password>"));
            return Command.SINGLE_SUCCESS;
        }
        //verification logic
        String pass = StringArgumentType.getString(command, "password");
        LoginData data = SecureLogin.mod.db.getData(player.getUUID());
        if(!data.getPassword().equals(pass)){
            player.sendSystemMessage(Component.literal(ChatFormatting.RED +
                    "You entered the wrong password!"));
            return Command.SINGLE_SUCCESS;
        }
        data.setIp(Utils.getStrippedIP(player));
        Utils.authPlayer(player);
        SecureLogin.mod.db.setData(data);
        return Command.SINGLE_SUCCESS;
    }

}
