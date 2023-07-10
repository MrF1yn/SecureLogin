package dev.mrflyn.securelogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.mrflyn.securelogin.LoginData;
import dev.mrflyn.securelogin.SecureLogin;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ChangePasswordCommand {

    public ChangePasswordCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("changepassword")
                .requires(CommandSourceStack::isPlayer)
                .then(Commands.argument("old_password", StringArgumentType.word())
                        .then(Commands.argument("new_password", StringArgumentType.word()).executes(this::onCommand))));
    }

    private int onCommand(CommandContext<CommandSourceStack> command) {
        ServerPlayer player = command.getSource().getPlayer();
        if(SecureLogin.unAuthPlayers.containsKey(player)){
            player.sendSystemMessage(Component.literal(ChatFormatting.RED +
                    "You are not logged in! Please login with /login <password>."));
            return Command.SINGLE_SUCCESS;
        }
        LoginData data = SecureLogin.mod.db.getData(player.getUUID());
        if (data==null) {
            player.sendSystemMessage(Component.literal(ChatFormatting.RED +
                    "You are not already registered. Register yourself with /register <password> <password>"));
            return Command.SINGLE_SUCCESS;
        }
        String old_pass = StringArgumentType.getString(command, "old_password");
        String new_pass = StringArgumentType.getString(command, "new_password");
        if(!data.getPassword().equals(old_pass)){
            player.sendSystemMessage(Component.literal(ChatFormatting.RED +
                    "Your old password did not match. Please contact an admin to change your password if you don't remember your old password."));
            return Command.SINGLE_SUCCESS;
        }
        data.setPassword(new_pass);
        SecureLogin.mod.db.setData(data);
        player.sendSystemMessage(Component.literal(ChatFormatting.GREEN+"Successfully changed your password."));
        return Command.SINGLE_SUCCESS;
    }

}
