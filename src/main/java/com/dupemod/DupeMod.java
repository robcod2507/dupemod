package com.dupemod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("dupemod")
public class DupeMod {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "dupemod";

    public DupeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CommandEvents());
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Dupe Mod Initialized!");
    }

    public static class CommandEvents {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
            
            dispatcher.register(
                Commands.literal("dupe")
                    .requires(source -> source.hasPermission(0))
                    .executes(context -> {
                        CommandSource source = context.getSource();
                        
                        if (!(source.getEntity() instanceof PlayerEntity)) {
                            source.sendFailure(new StringTextComponent("§cТолько игроки могут использовать эту команду!"));
                            return 0;
                        }
                        
                        PlayerEntity player = (PlayerEntity) source.getEntity();
                        ItemStack offhandItem = player.getItemInHand(Hand.OFF_HAND);
                        
                        if (offhandItem.isEmpty()) {
                            player.sendMessage(new StringTextComponent("§cПоложите предмет в левую руку!"), player.getUUID());
                            return 0;
                        }
                        
                        ItemStack copiedStack = offhandItem.copy();
                        if (player.inventory.add(copiedStack)) {
                            player.sendMessage(new StringTextComponent("§aПредмет продублирован!"), player.getUUID());
                        } else {
                            player.drop(copiedStack, false);
                            player.sendMessage(new StringTextComponent("§eИнвентарь полон! Предмет выброшен."), player.getUUID());
                        }
                        
                        return 1;
                    })
            );
        }
    }
}