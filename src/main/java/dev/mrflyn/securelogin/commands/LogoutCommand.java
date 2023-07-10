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

public class LogoutCommand {

    public LogoutCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("logout")
                .requires(CommandSourceStack::isPlayer).executes(this::onCommand));
    }

    private int onCommand(CommandContext<CommandSourceStack> command) {
        ServerPlayer player = command.getSource().getPlayer();
        LoginData data = SecureLogin.mod.db.getData(player.getUUID());
        if(data!=null) {
            data.setIp("-1");
            SecureLogin.mod.db.setData(data);
        }
        player.connection.disconnect(Component.literal(ChatFormatting.RED+"Logged Out!"));
        return Command.SINGLE_SUCCESS;
    }

}
