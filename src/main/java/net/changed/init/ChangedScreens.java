package net.changed.init;

import net.changed.client.gui.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedScreens {
    @SubscribeEvent
    public static void clientLoad(RegisterMenuScreensEvent event) {
        event.register(ChangedMenus.COMPUTER.get(), ComputerTextScreen::new);
        event.register(ChangedMenus.INFUSER.get(), InfuserScreen::new);
        event.register(ChangedMenus.PURIFIER.get(), PurifierScreen::new);
        event.register(ChangedMenus.ABILITY_RADIAL.get(), AbilityRadialScreen::new);
        event.register(ChangedMenus.HAIRSTYLE_RADIAL.get(), HairStyleRadialScreen::new);
        event.register(ChangedMenus.KEYPAD.get(), KeypadScreen::new);
        event.register(ChangedMenus.CLIPBOARD.get(), ClipboardScreen::new);
        event.register(ChangedMenus.NOTE.get(), NoteScreen::new);
        event.register(ChangedMenus.STASIS_CHAMBER.get(), StasisChamberScreen::new);
        event.register(ChangedMenus.ACCESSORY_ACCESS.get(), AccessoryAccessScreen::new);
        event.register(ChangedMenus.TAMED_DARK_LATEX.get(), TamedDarkLatexScreen::new);
        event.register(ChangedMenus.TAMED_DARK_LATEX_INVENTORY.get(), TamedDarkLatexInventoryScreen::new);
    }
}
