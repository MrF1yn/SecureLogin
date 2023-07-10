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

public class RegisterCommand {

    public RegisterCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("register")
                .requires(CommandSourceStack::isPlayer)
                .then(Commands.argument("password1", StringArgumentType.word())
                        .then(Commands.argument("password2", StringArgumentType.word()).executes(this::onCommand))));
    }

    private int onCommand(CommandContext<CommandSourceStack> command) {
        ServerPlayer player = command.getSource().getPlayer();
        if (SecureLogin.mod.db.isPresent(player.getUUID())) {
            player.sendSystemMessage(Component.literal(ChatFormatting.RED + "You are already registered!"));
            return Command.SINGLE_SUCCESS;
        }
        //verification logic
        String pass1 = StringArgumentType.getString(command, "password1");
        String pass2 = StringArgumentType.getString(command, "password2");
        if (!pass2.equals(pass1)) {
            player.sendSystemMessage(Component.literal(ChatFormatting.RED + "Your passwords do not match!"));
            return Command.SINGLE_SUCCESS;
        }
        //registration logic
        SecureLogin.mod.db.setData(new LoginData(player.getUUID(), player.getName().getString(), pass1, Utils.getStrippedIP(player)));
        Utils.authPlayer(player);
        player.sendSystemMessage(Component.literal(ChatFormatting.GREEN + "You are registered successfully!"));
        return Command.SINGLE_SUCCESS;
    }

}
