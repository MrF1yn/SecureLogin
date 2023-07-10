package dev.mrflyn.securelogin;

import dev.mrflyn.securelogin.scheduler.ScheduledTask;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;


public class Utils {

    public static void unAuthPlayer(ServerPlayer player){
        SecureLogin.unAuthPlayers.put(player, player.position());
        SecureLogin.mod.scheduler.scheduleTask(new ScheduledTask(20,20, 10) {
            @Override
            public void run() {
                
                if(!ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().contains(player)){
                    cancel();
                    return;
                }
                if(!SecureLogin.unAuthPlayers.containsKey(player)){
                    cancel();
                    return;
                }
                player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW+"/register <password> <password> OR /login <password>."));
            }

            @Override
            public void onEnd() {
                if(!ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().contains(player)){
                    return;
                }
                if(!SecureLogin.unAuthPlayers.containsKey(player)){
                    return;
                }
                player.connection.disconnect(Component.literal(ChatFormatting.RED+"Maximum Time Exceeded!"));
            }
        });
        player.setInvulnerable(true);
    }

    public static void authPlayer(ServerPlayer player) {
        clearPlayer(player);
        player.sendSystemMessage(Component.literal(ChatFormatting.GREEN+"Successfully authenticated!"));
    }

    public static void clearPlayer(ServerPlayer player){
        SecureLogin.unAuthPlayers.remove(player);
        player.setInvulnerable(false);
    }

    public static String getStrippedIP(ServerPlayer player){
        return player.connection.connection.getRemoteAddress().toString().split("]:")[0].substring(2);
    }



}
