package dev.mrflyn.securelogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.mrflyn.securelogin.SecureLogin;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class UnregisterCommand {

    public UnregisterCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("unregister")
                .requires((commandSourceStack -> {
                    return commandSourceStack.hasPermission(4);
                }))
                .then(Commands.argument("name(case sensitive)", StringArgumentType.word()).executes(this::onCommand)));
    }

    private int onCommand(CommandContext<CommandSourceStack> command) {
        CommandSourceStack css = command.getSource();
        String userName = StringArgumentType.getString(command, "name(case sensitive)");
        if (!SecureLogin.mod.db.isPresent(userName)){
            css.sendSystemMessage(Component.literal(ChatFormatting.RED+"Could not find a registered player with that name."));
            return Command.SINGLE_SUCCESS;
        }
        SecureLogin.mod.db.deleteData(userName);
        css.sendSystemMessage(Component.literal(ChatFormatting.GREEN+"Player unregistered successfully!"));
        return Command.SINGLE_SUCCESS;
    }

}
