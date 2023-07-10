package dev.mrflyn.securelogin;

import com.mojang.brigadier.ParseResults;
import com.mojang.logging.LogUtils;
import dev.mrflyn.securelogin.commands.*;
import dev.mrflyn.securelogin.databases.IDatabase;
import dev.mrflyn.securelogin.databases.SQLite;
import dev.mrflyn.securelogin.scheduler.SchedulerTaskHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.command.ConfigCommand;
import org.slf4j.Logger;

import java.util.*;

@Mod(SecureLogin.MOD_ID)
public class SecureLogin {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "secure_login";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public IDatabase db;

    public static HashMap<ServerPlayer, Vec3> unAuthPlayers;
    public static SecureLogin mod;

    public SchedulerTaskHandler scheduler;

    public SecureLogin() {
        unAuthPlayers = new HashMap<>();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        db = new SQLite();
        db.connect();
        db.init();
        scheduler = new SchedulerTaskHandler();
        mod = this;

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }


    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        new RegisterCommand(event.getDispatcher());
        new LoginCommand(event.getDispatcher());
        new UnregisterCommand(event.getDispatcher());
        new ChangePasswordCommand(event.getDispatcher());
        new ForceChangePasswordCommand(event.getDispatcher());
        new LogoutCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        scheduler.tick();
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        ServerPlayer player = event.getParseResults().getContext().getSource().getPlayer();
        if (player == null) return;
        if (!unAuthPlayers.containsKey(player)) return;
        String cmd = event.getParseResults().getContext().getNodes().get(0).getNode().getName();
        if (cmd.equals("login") || cmd.equals("register") || cmd.equals("logout")) return;
        event.setCanceled(true);
        player.sendSystemMessage(Component.literal(
                ChatFormatting.RED + "You can't execute any other commands until you are authenticated!"));

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Do something when the server starts
        LOGGER.info("LOGIN " + event.getEntity().getName());
        ServerPlayer player = (ServerPlayer) event.getEntity();
        Utils.unAuthPlayer(player);
        //auto ip login
        LoginData data = db.getData(player.getUUID());
        if (data == null) return;
        if (data.getIp().equals(Utils.getStrippedIP(player))) Utils.authPlayer(player);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Utils.clearPlayer((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event){
        if(!unAuthPlayers.containsKey(event.getPlayer()))return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!unAuthPlayers.containsKey(player)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!unAuthPlayers.containsKey(player)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!unAuthPlayers.containsKey(player)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!unAuthPlayers.containsKey(player)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!unAuthPlayers.containsKey(player)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onMove(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        ServerPlayer player = (ServerPlayer) event.player;
        if (!unAuthPlayers.containsKey(player)) return;
        Vec3 pos = unAuthPlayers.get(player);
        player.teleportTo(pos.x, pos.y, pos.z);
    }

}
