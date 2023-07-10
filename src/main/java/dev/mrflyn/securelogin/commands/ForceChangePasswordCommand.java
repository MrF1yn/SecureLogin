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


public class ForceChangePasswordCommand {

    public ForceChangePasswordCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("forcechangepassword")
                .requires((cmd)->{
                    return cmd.hasPermission(4);
                })
                .then(Commands.argument("name(case sensitive)", StringArgumentType.word())
                        .then(Commands.argument("new_password", StringArgumentType.word()).executes(this::onCommand))));
    }

    private int onCommand(CommandContext<CommandSourceStack> command) {
        CommandSourceStack css = command.getSource();
        String userName = StringArgumentType.getString(command, "name(case sensitive)");
        LoginData data = SecureLogin.mod.db.getData(userName);
        if (data==null){
            css.sendSystemMessage(Component.literal(ChatFormatting.RED+"Could not find a registered player with that name."));
            return Command.SINGLE_SUCCESS;
        }
        String pass = StringArgumentType.getString(command, "new_password");
        data.setPassword(pass);
        SecureLogin.mod.db.setData(data);
        css.sendSystemMessage(Component.literal(ChatFormatting.GREEN+"Player's password changed successfully!"));
        return Command.SINGLE_SUCCESS;
    }

}
