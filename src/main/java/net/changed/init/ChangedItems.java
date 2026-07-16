package net.changed.init;

import net.changed.Changed;
import net.changed.entity.robot.Exoskeleton;
import net.changed.entity.robot.Roomba;
import net.changed.item.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.Supplier;

public class ChangedItems {
    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    private static final Component ABDOMEN_CONVERSION = Component.translatable(Util.makeDescriptionId("conversion", Changed.modResource("abdomen_conversion"))).withStyle(TITLE_FORMAT);
    private static final Component ABDOMEN_CONVERSION_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.abdomen_conversion.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component ABDOMEN_CONVERSION_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.abdomen_conversion.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component ABDOMEN_CONVERSION_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.abdomen_conversion.base_slot_description")));
    private static final Component ABDOMEN_CONVERSION_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.abdomen_conversion.additions_slot_description")));
    private static final Component QUADRUPEDAL_CONVERSION = Component.translatable(Util.makeDescriptionId("conversion", Changed.modResource("quadrupedal_conversion"))).withStyle(TITLE_FORMAT);
    private static final Component QUADRUPEDAL_CONVERSION_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.quadrupedal_conversion.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component QUADRUPEDAL_CONVERSION_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.quadrupedal_conversion.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component QUADRUPEDAL_CONVERSION_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.quadrupedal_conversion.base_slot_description")));
    private static final Component QUADRUPEDAL_CONVERSION_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Changed.modResource("smithing_template.quadrupedal_conversion.additions_slot_description")));
    private static final ResourceLocation EMPTY_SLOT_LEGGINGS = ResourceLocation.parse("item/empty_armor_slot_leggings");
    private static final ResourceLocation EMPTY_SLOT_BOOTS = ResourceLocation.parse("item/empty_armor_slot_boots");
    private static final ResourceLocation EMPTY_SLOT_INGOT = ResourceLocation.parse("item/empty_slot_ingot");
    private static final ResourceLocation EMPTY_SLOT_DIAMOND = ResourceLocation.parse("item/empty_slot_diamond");

    private static List<ResourceLocation> createAbdomenConversionIconList() {
        return List.of(EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS);
    }

    private static List<ResourceLocation> createQuadrupedalConversionIconList() {
        return List.of(EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS);
    }

    private static List<ResourceLocation> createConversionMaterialList() {
        return List.of(EMPTY_SLOT_INGOT, EMPTY_SLOT_DIAMOND);
    }

    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Changed.MODID);
    public static final DeferredHolder<Item, BenignShorts> BENIGN_SHORTS = register("benign_shorts", BenignShorts::new);
    public static final DeferredHolder<Item, PinkShorts> PINK_SHORTS = register("pink_shorts", PinkShorts::new);
    public static final DeferredHolder<Item, BraItem> SPORTS_BRA = register("sports_bra", BraItem::new);
    public static final DeferredHolder<Item, ShirtItem> BLACK_TSHIRT = register("black_tshirt", ShirtItem::new);
    public static final DeferredHolder<Item, ShirtItem> WHITE_TSHIRT = register("white_tshirt", ShirtItem::new);
    public static final DeferredHolder<Item, LabCoatItem> LAB_COAT = register("lab_coat", LabCoatItem::new);
    public static final DeferredHolder<Item, WetsuitItem> WETSUIT = register("wetsuit", WetsuitItem::new);
    public static final DeferredHolder<Item, NitrileGloves> NITRILE_GLOVES = register("nitrile_gloves", NitrileGloves::new);
    public static final DeferredHolder<Item, NeckTieItem> ORANGE_NECK_TIE = register("orange_neck_tie", NeckTieItem::new);
    public static final DeferredHolder<Item, NeckTieItem> RED_NECK_TIE = register("red_neck_tie", NeckTieItem::new);
    public static final DeferredHolder<Item, NeckTieItem> BLUE_NECK_TIE = register("blue_neck_tie", NeckTieItem::new);
    public static final DeferredHolder<Item, CollarItem> DOG_COLLAR = register("dog_collar", CollarItem::new);
    public static final DeferredHolder<Item, GasMaskItem> GAS_MASK = register("gas_mask", GasMaskItem::new);
    public static final DeferredHolder<Item, FaceMaskItem> FACE_MASK = register("face_mask", FaceMaskItem::new);
    public static final DeferredHolder<Item, Pants> BLACK_PANTS = register("black_pants", Pants::new);
    public static final DeferredHolder<Item, Pants> NAVY_PANTS = register("navy_pants", Pants::new);
    public static final DeferredHolder<Item, TscVestItem> TSC_VEST = register("tsc_vest", TscVestItem::new);
    public static final DeferredHolder<Item, AbstractChangedItem> DARK_LATEX_CRYSTAL_FRAGMENT = register("dark_latex_crystal_fragment", AbstractChangedItem::new);
    public static final DeferredHolder<Item, AbstractLatexItem> DARK_LATEX_GOO = register("dark_latex_goo", () -> new AbstractLatexItem(ChangedBlocks.DARK_LATEX_WALL_SPLOTCH.get(), ChangedLatexTypes.DARK_LATEX));
    public static final DeferredHolder<Item, DarkLatexMask> DARK_LATEX_MASK = register("dark_latex_mask", DarkLatexMask::new);
    public static final DeferredHolder<Item, AbstractLatexBucket> DARK_LATEX_BUCKET = register("dark_latex_bucket", AbstractLatexBucket.from(ChangedFluids.DARK_LATEX, ChangedLatexTypes.DARK_LATEX));
    public static final DeferredHolder<Item, CompactDisc> COMPACT_DISC = register("compact_disc", CompactDisc::new);
    public static final DeferredHolder<Item, LabBook> LAB_BOOK = register("lab_book", LabBook::new);
    public static final DeferredHolder<Item, Item> LATEX_BASE = register("latex_base", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ORANGE = register("orange", () -> new ItemNameBlockItem(ChangedBlocks.DROPPED_ORANGE.get(), (new Item.Properties()).food(ChangedFoods.ORANGE)) {
        @Override
        public InteractionResult useOn(UseOnContext context) {
            if (context.getLevel().getBlockState(context.getClickedPos()).is(ChangedBlocks.DROPPED_ORANGE.get()))
                return super.useOn(context);
            if (context.getPlayer() != null && context.getPlayer().isCrouching())
                return super.useOn(context);
            return InteractionResult.PASS;
        }
    });
    public static final DeferredHolder<Item, Syringe> SYRINGE = register("syringe", () -> new Syringe(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, BloodSyringe> BLOOD_SYRINGE = register("blood_syringe", () -> new BloodSyringe(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, TransfurCrystalItem> BEIFENG_CRYSTAL_FRAGMENT = register("beifeng_crystal_fragment",
            () -> new TransfurCrystalItem(ChangedTransfurVariants.BEIFENG));
    public static final DeferredHolder<Item, LatexFlask> LATEX_FLASK = register("latex_flask", () -> new LatexFlask(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, LatexInkballItem> LATEX_INKBALL = register("latex_inkball", LatexInkballItem::new);
    public static final DeferredHolder<Item, LatexSyringe> LATEX_SYRINGE = register("latex_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, LatexTippedArrowItem> LATEX_TIPPED_ARROW = register("latex_tipped_arrow", LatexTippedArrowItem::new);
    public static final DeferredHolder<Item, FilledMug> MUG_WITH_WATER = register("mug_with_water", () -> new FilledMug(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, FilledMug> MUG_WITH_MILK = register("mug_with_milk", () -> new FilledMug(new Item.Properties().stacksTo(16)) {
        @Override
        protected void onDrink(ItemStack stack, Level level, LivingEntity user) {
            user.removeAllEffects();
        }
    });
    public static final DeferredHolder<Item, FilledMug> MUG_WITH_COFFEE = register("mug_with_coffee", () -> new FilledMug(new Item.Properties().stacksTo(16)) {
        @Override
        protected void onDrink(ItemStack stack, Level level, LivingEntity user) {
            ChangedEffects.CAFFEINATED.get().stackEffect(user, 5 * 60 * 20, (5 * 60 * 20) / 2);
        }
    });
    public static final DeferredHolder<Item, FilledMug> MUG_WITH_DARK_LATEX = register("mug_with_dark_latex", () -> new LatexFilledMug(ChangedLatexTypes.DARK_LATEX, new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, FilledMug> MUG_WITH_WHITE_LATEX = register("mug_with_white_latex", () -> new LatexFilledMug(ChangedLatexTypes.WHITE_LATEX, new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, TransfurCrystalItem> WOLF_CRYSTAL_FRAGMENT = register("wolf_crystal_fragment",
            () -> new TransfurCrystalItem(ChangedTransfurVariants.CRYSTAL_WOLF));
    public static final DeferredHolder<Item, TransfurCrystalItem> DARK_DRAGON_CRYSTAL_FRAGMENT = register("dark_dragon_crystal_fragment",
            () -> new TransfurCrystalItem(ChangedTransfurVariants.DARK_DRAGON));
    public static final DeferredHolder<Item, AbstractLatexItem> WHITE_LATEX_GOO = register("white_latex_goo", () -> new AbstractLatexItem(ChangedBlocks.WHITE_LATEX_WALL_SPLOTCH.get(), ChangedLatexTypes.WHITE_LATEX));
    public static final DeferredHolder<Item, AbstractLatexBucket> WHITE_LATEX_BUCKET = register("white_latex_bucket", AbstractLatexBucket.from(ChangedFluids.WHITE_LATEX, ChangedLatexTypes.WHITE_LATEX));

    public static final DeferredHolder<Item, GameMasterBlockItem> GLU = register("glu", () -> new GameMasterBlockItem(ChangedBlocks.GLU_BLOCK.get(), (new Item.Properties()).rarity(Rarity.EPIC)));

    public static final DeferredHolder<Item, TscStaff> TSC_STAFF = register("tsc_staff", TscStaff::new);
    public static final DeferredHolder<Item, TscBaton> TSC_BATON = register("tsc_baton", TscBaton::new);
    public static final DeferredHolder<Item, TscShield> TSC_SHIELD = register("tsc_shield", TscShield::new);

    public static final DeferredHolder<Item, AbstractArmorStandItem> BIPED_ARMOR_STAND = register("biped_armor_stand", () -> new AbstractArmorStandItem((new Item.Properties()).stacksTo(16),
            ChangedEntities.BIPED_ARMOR_STAND));
    public static final DeferredHolder<Item, AbstractArmorStandItem> CENTAUR_ARMOR_STAND = register("centaur_armor_stand", () -> new AbstractArmorStandItem((new Item.Properties()).stacksTo(16),
            ChangedEntities.CENTAUR_ARMOR_STAND));
    public static final DeferredHolder<Item, AbstractArmorStandItem> LEGLESS_ARMOR_STAND = register("legless_armor_stand", () -> new AbstractArmorStandItem((new Item.Properties()).stacksTo(16),
            ChangedEntities.LEGLESS_ARMOR_STAND));

    public static final DeferredHolder<Item, AbdomenArmor> LEATHER_UPPER_ABDOMEN_ARMOR = register("leather_upper_abdomen_armor",
            () -> new DyeableAbdomenArmor(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, AbdomenArmor> LEATHER_LOWER_ABDOMEN_ARMOR = register("leather_lower_abdomen_armor",
            () -> new DyeableAbdomenArmor(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, AbdomenArmor> CHAINMAIL_UPPER_ABDOMEN_ARMOR = register("chainmail_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.CHAIN, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, AbdomenArmor> CHAINMAIL_LOWER_ABDOMEN_ARMOR = register("chainmail_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.CHAIN, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, AbdomenArmor> IRON_UPPER_ABDOMEN_ARMOR = register("iron_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, AbdomenArmor> IRON_LOWER_ABDOMEN_ARMOR = register("iron_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.IRON, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, AbdomenArmor> GOLDEN_UPPER_ABDOMEN_ARMOR = register("golden_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.GOLD, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, AbdomenArmor> GOLDEN_LOWER_ABDOMEN_ARMOR = register("golden_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.GOLD, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, AbdomenArmor> DIAMOND_UPPER_ABDOMEN_ARMOR = register("diamond_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, AbdomenArmor> DIAMOND_LOWER_ABDOMEN_ARMOR = register("diamond_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, AbdomenArmor> NETHERITE_UPPER_ABDOMEN_ARMOR = register("netherite_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, AbdomenArmor> NETHERITE_LOWER_ABDOMEN_ARMOR = register("netherite_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, SmithingTemplateItem> ABDOMEN_ARMOR_CONVERSION = register("abdomen_conversion_smithing_template",
            () -> new SmithingTemplateItem(
                    ABDOMEN_CONVERSION_APPLIES_TO,
                    ABDOMEN_CONVERSION_INGREDIENTS,
                    ABDOMEN_CONVERSION,
                    ABDOMEN_CONVERSION_BASE_SLOT_DESCRIPTION,
                    ABDOMEN_CONVERSION_ADDITIONS_SLOT_DESCRIPTION,
                    createAbdomenConversionIconList(),
                    createConversionMaterialList()));

    public static final DeferredHolder<Item, QuadrupedalArmor> LEATHER_QUADRUPEDAL_LEGGINGS = register("leather_quadrupedal_leggings",
            () -> new DyeableQuadrupedalArmor(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, QuadrupedalArmor> LEATHER_QUADRUPEDAL_BOOTS = register("leather_quadrupedal_boots",
            () -> new DyeableQuadrupedalArmor(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, QuadrupedalArmor> CHAINMAIL_QUADRUPEDAL_LEGGINGS = register("chainmail_quadrupedal_leggings",
            () -> new QuadrupedalArmor(ArmorMaterials.CHAIN, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, QuadrupedalArmor> CHAINMAIL_QUADRUPEDAL_BOOTS = register("chainmail_quadrupedal_boots",
            () -> new QuadrupedalArmor(ArmorMaterials.CHAIN, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, QuadrupedalArmor> IRON_QUADRUPEDAL_LEGGINGS = register("iron_quadrupedal_leggings",
            () -> new QuadrupedalArmor(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, QuadrupedalArmor> IRON_QUADRUPEDAL_BOOTS = register("iron_quadrupedal_boots",
            () -> new QuadrupedalArmor(ArmorMaterials.IRON, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, QuadrupedalArmor> GOLDEN_QUADRUPEDAL_LEGGINGS = register("golden_quadrupedal_leggings",
            () -> new QuadrupedalArmor(ArmorMaterials.GOLD, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, QuadrupedalArmor> GOLDEN_QUADRUPEDAL_BOOTS = register("golden_quadrupedal_boots",
            () -> new QuadrupedalArmor(ArmorMaterials.GOLD, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, QuadrupedalArmor> DIAMOND_QUADRUPEDAL_LEGGINGS = register("diamond_quadrupedal_leggings",
            () -> new QuadrupedalArmor(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, QuadrupedalArmor> DIAMOND_QUADRUPEDAL_BOOTS = register("diamond_quadrupedal_boots",
            () -> new QuadrupedalArmor(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, QuadrupedalArmor> NETHERITE_QUADRUPEDAL_LEGGINGS = register("netherite_quadrupedal_leggings",
            () -> new QuadrupedalArmor(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, QuadrupedalArmor> NETHERITE_QUADRUPEDAL_BOOTS = register("netherite_quadrupedal_boots",
            () -> new QuadrupedalArmor(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, SmithingTemplateItem> QUADRUPEDAL_ARMOR_CONVERSION = register("quadrupedal_conversion_smithing_template",
            () -> new SmithingTemplateItem(
                    QUADRUPEDAL_CONVERSION_APPLIES_TO,
                    QUADRUPEDAL_CONVERSION_INGREDIENTS,
                    QUADRUPEDAL_CONVERSION,
                    QUADRUPEDAL_CONVERSION_BASE_SLOT_DESCRIPTION,
                    QUADRUPEDAL_CONVERSION_ADDITIONS_SLOT_DESCRIPTION,
                    createQuadrupedalConversionIconList(),
                    createConversionMaterialList()));

    public static final DeferredHolder<Item, PlaceableEntity<Roomba>> ROOMBA = register("roomba",
            () -> new RoombaItem<>(new Item.Properties().stacksTo(4), ChangedEntities.ROOMBA));
    public static final DeferredHolder<Item, ExoskeletonItem<Exoskeleton>> EXOSKELETON = register("exoskeleton",
            () -> new ExoskeletonItem<>(new Item.Properties().stacksTo(1), ChangedEntities.EXOSKELETON));

    public static final DeferredHolder<Item, LatexRecordItem> BLACK_GOO_ZONE_RECORD = registerLatexRecord("black_goo_zone_record", ChangedSounds.MUSIC_BLACK_GOO_ZONE);
    public static final DeferredHolder<Item, LatexRecordItem> CRYSTAL_ZONE_RECORD = registerLatexRecord("crystal_zone_record", ChangedSounds.MUSIC_CRYSTAL_ZONE);
    public static final DeferredHolder<Item, LatexRecordItem> GAS_ROOM_RECORD = registerLatexRecord("gas_room_record", ChangedSounds.MUSIC_GAS_ROOM);
    public static final DeferredHolder<Item, LatexRecordItem> LABORATORY_RECORD = registerLatexRecord("laboratory_record", ChangedSounds.MUSIC_LABORATORY);
    public static final DeferredHolder<Item, LatexRecordItem> OUTSIDE_THE_TOWER_RECORD = registerLatexRecord("outside_the_tower_record", ChangedSounds.MUSIC_OUTSIDE_THE_TOWER);
    public static final DeferredHolder<Item, LatexRecordItem> PURO_THE_BLACK_GOO_RECORD = registerLatexRecord("puro_the_black_goo_record", ChangedSounds.MUSIC_PURO_THE_BLACK_GOO);
    public static final DeferredHolder<Item, LatexRecordItem> PUROS_HOME_RECORD = registerLatexRecord("puros_home_record", ChangedSounds.MUSIC_PUROS_HOME);
    public static final DeferredHolder<Item, LatexRecordItem> THE_LIBRARY_RECORD = registerLatexRecord("the_library_record", ChangedSounds.MUSIC_THE_LIBRARY);
    public static final DeferredHolder<Item, LatexRecordItem> THE_LION_CHASE_RECORD = registerLatexRecord("the_lion_chase_record", ChangedSounds.MUSIC_THE_LION_CHASE);
    public static final DeferredHolder<Item, LatexRecordItem> THE_SCARLET_CRYSTAL_MINE_RECORD = registerLatexRecord("the_scarlet_crystal_mine_record", ChangedSounds.MUSIC_THE_SCARLET_CRYSTAL_MINE);
    public static final DeferredHolder<Item, LatexRecordItem> THE_SHARK_RECORD = registerLatexRecord("the_shark_record", ChangedSounds.MUSIC_THE_SHARK);
    public static final DeferredHolder<Item, LatexRecordItem> THE_SQUID_DOG_RECORD = registerLatexRecord("the_squid_dog_record", ChangedSounds.MUSIC_THE_SQUID_DOG);
    public static final DeferredHolder<Item, LatexRecordItem> THE_WHITE_GOO_JUNGLE_RECORD = registerLatexRecord("the_white_goo_jungle_record", ChangedSounds.MUSIC_THE_WHITE_GOO_JUNGLE);
    public static final DeferredHolder<Item, LatexRecordItem> THE_WHITE_TAIL_CHASE_PART_1 = registerLatexRecord("the_white_tail_chase_part_1_record", ChangedSounds.MUSIC_THE_WHITE_TAIL_CHASE_PART_1);
    public static final DeferredHolder<Item, LatexRecordItem> THE_WHITE_TAIL_CHASE_PART_2 = registerLatexRecord("the_white_tail_chase_part_2_record", ChangedSounds.MUSIC_THE_WHITE_TAIL_CHASE_PART_2);
    public static final DeferredHolder<Item, LatexRecordItem> VENT_PIPE_RECORD = registerLatexRecord("vent_pipe_record", ChangedSounds.MUSIC_VENT_PIPE);

    private static DeferredHolder<Item, LatexRecordItem> registerLatexRecord(String name, Supplier<SoundEvent> soundEventSupplier) {
        return register(name, () -> new LatexRecordItem(8, soundEventSupplier, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE)));
    }

    private static <T extends Item> DeferredHolder<Item, T> register(String name, Supplier<T> item) {
        return REGISTRY.register(name, item);
    }
}
