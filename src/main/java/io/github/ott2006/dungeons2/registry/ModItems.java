package io.github.ott2006.dungeons2.registry;

import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.item.ArtifactItem;
import io.github.ott2006.dungeons2.item.MonarchLureItem;
import io.github.ott2006.dungeons2.item.SiftArmorItem;
import io.github.ott2006.dungeons2.item.SiftWeaponItem;
import io.github.ott2006.dungeons2.item.SingersHornItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Dungeons2.MOD_ID);

    /** Items in creative tab order (block items are added separately). */
    public static final List<DeferredItem<? extends Item>> TAB_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<DeferredSpawnEggItem>> SPAWN_EGGS = new ArrayList<>();

    // ------------------------------------------------------------------ misc
    public static final DeferredItem<BucketItem> ICHOR_BUCKET = tab("ichor_bucket",
            () -> new BucketItem(ModFluids.ICHOR.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<SingersHornItem> SINGERS_HORN = tab("singers_horn",
            () -> new SingersHornItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<MonarchLureItem> MONARCH_LURE = tab("monarch_lure",
            () -> new MonarchLureItem(new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));

    // ------------------------------------------------------------------ materials
    public static final DeferredItem<Item> SIFTER_CHITIN = tab("sifter_chitin", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SCULKER_CLAW = tab("sculker_claw", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SOUL_FRAGMENT = tab("soul_fragment", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENRAGED_HEART = tab("enraged_heart",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> MONARCH_FEATHER = tab("monarch_feather",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    /** Items that only exist so that projectiles can be rendered. */
    public static final DeferredItem<Item> SIFT_DART = ITEMS.registerSimpleItem("sift_dart");
    public static final DeferredItem<Item> GOO_GLOB = ITEMS.registerSimpleItem("goo_glob");
    public static final DeferredItem<Item> SOUL_WISP = ITEMS.registerSimpleItem("soul_wisp");

    // ------------------------------------------------------------------ weapons
    public static final DeferredItem<SiftWeaponItem> BATTLESTAFF = tab("battlestaff",
            () -> new SiftWeaponItem(SiftWeaponItem.Kind.BATTLESTAFF, Rarity.COMMON));
    public static final DeferredItem<SiftWeaponItem> RIFTSLASHER = tab("riftslasher",
            () -> new SiftWeaponItem(SiftWeaponItem.Kind.RIFTSLASHER, Rarity.UNCOMMON));
    public static final DeferredItem<SiftWeaponItem> SPECTRAL_SPEAR = tab("spectral_spear",
            () -> new SiftWeaponItem(SiftWeaponItem.Kind.SPECTRAL_SPEAR, Rarity.RARE));
    public static final DeferredItem<SiftWeaponItem> SCULKERS_BANE = tab("sculkers_bane",
            () -> new SiftWeaponItem(SiftWeaponItem.Kind.SCULKERS_BANE, Rarity.RARE));
    public static final DeferredItem<SiftWeaponItem> SCULKER_CLAWS = tab("sculker_claws",
            () -> new SiftWeaponItem(SiftWeaponItem.Kind.SCULKER_CLAWS, Rarity.RARE));
    public static final DeferredItem<SiftWeaponItem> CACOPHONOUS_CLEAVER = tab("cacophonous_cleaver",
            () -> new SiftWeaponItem(SiftWeaponItem.Kind.CACOPHONOUS_CLEAVER, Rarity.RARE));
    public static final DeferredItem<SiftWeaponItem> SOUL_REAPER = tab("soul_reaper",
            () -> new SiftWeaponItem(SiftWeaponItem.Kind.SOUL_REAPER, Rarity.RARE));

    // ------------------------------------------------------------------ artifacts
    public static final DeferredItem<ArtifactItem> ECHO_OCARINA = tab("echo_ocarina",
            () -> new ArtifactItem(ArtifactItem.Kind.ECHO_OCARINA));
    public static final DeferredItem<ArtifactItem> WARDING_CHIMES = tab("warding_chimes",
            () -> new ArtifactItem(ArtifactItem.Kind.WARDING_CHIMES));
    public static final DeferredItem<ArtifactItem> HUMBLING_HORN = tab("humbling_horn",
            () -> new ArtifactItem(ArtifactItem.Kind.HUMBLING_HORN));
    public static final DeferredItem<ArtifactItem> CORRUPTED_SEEDS = tab("corrupted_seeds",
            () -> new ArtifactItem(ArtifactItem.Kind.CORRUPTED_SEEDS));
    public static final DeferredItem<ArtifactItem> SOUL_HARVESTER = tab("soul_harvester",
            () -> new ArtifactItem(ArtifactItem.Kind.SOUL_HARVESTER));
    public static final DeferredItem<ArtifactItem> CORRUPTED_BEACON = tab("corrupted_beacon",
            () -> new ArtifactItem(ArtifactItem.Kind.CORRUPTED_BEACON));

    // ------------------------------------------------------------------ armor
    public static final DeferredItem<SiftArmorItem> SIFTER_HELMET = armor("sifter_helmet", ModArmorMaterials.SIFTER, ArmorItem.Type.HELMET, 22);
    public static final DeferredItem<SiftArmorItem> SIFTER_CHESTPLATE = armor("sifter_chestplate", ModArmorMaterials.SIFTER, ArmorItem.Type.CHESTPLATE, 22);
    public static final DeferredItem<SiftArmorItem> SIFTER_LEGGINGS = armor("sifter_leggings", ModArmorMaterials.SIFTER, ArmorItem.Type.LEGGINGS, 22);
    public static final DeferredItem<SiftArmorItem> SIFTER_BOOTS = armor("sifter_boots", ModArmorMaterials.SIFTER, ArmorItem.Type.BOOTS, 22);
    public static final DeferredItem<SiftArmorItem> MAD_SIFTER_HELMET = armor("mad_sifter_helmet", ModArmorMaterials.MAD_SIFTER, ArmorItem.Type.HELMET, 33);
    public static final DeferredItem<SiftArmorItem> MAD_SIFTER_CHESTPLATE = armor("mad_sifter_chestplate", ModArmorMaterials.MAD_SIFTER, ArmorItem.Type.CHESTPLATE, 33);
    public static final DeferredItem<SiftArmorItem> MAD_SIFTER_LEGGINGS = armor("mad_sifter_leggings", ModArmorMaterials.MAD_SIFTER, ArmorItem.Type.LEGGINGS, 33);
    public static final DeferredItem<SiftArmorItem> MAD_SIFTER_BOOTS = armor("mad_sifter_boots", ModArmorMaterials.MAD_SIFTER, ArmorItem.Type.BOOTS, 33);

    // ------------------------------------------------------------------ spawn eggs
    public static final DeferredItem<DeferredSpawnEggItem> BLUB_SPAWN_EGG = egg("blub", ModEntities.BLUB, 0x4FD6E8, 0xC8F8FF);
    public static final DeferredItem<DeferredSpawnEggItem> SINGER_SPAWN_EGG = egg("singer", ModEntities.SINGER, 0xA9C79A, 0xE8E2CC);
    public static final DeferredItem<DeferredSpawnEggItem> ECHO_GOLEM_SPAWN_EGG = egg("echo_golem", ModEntities.ECHO_GOLEM, 0x1E3A40, 0x49E6F2);
    public static final DeferredItem<DeferredSpawnEggItem> SEEDLING_SPAWN_EGG = egg("seedling", ModEntities.SEEDLING, 0x5E9E4A, 0xF2D14B);
    public static final DeferredItem<DeferredSpawnEggItem> SENTINEL_SPAWN_EGG = egg("sentinel", ModEntities.SENTINEL, 0x3F7A6A, 0xE07B39);
    public static final DeferredItem<DeferredSpawnEggItem> POLLINATOR_SPAWN_EGG = egg("pollinator", ModEntities.POLLINATOR, 0x6E8F3A, 0xF0D040);
    public static final DeferredItem<DeferredSpawnEggItem> NESTER_SPAWN_EGG = egg("nester", ModEntities.NESTER, 0xB5562E, 0xF1DDB8);
    public static final DeferredItem<DeferredSpawnEggItem> SPROUT_SPAWN_EGG = egg("sprout", ModEntities.SPROUT, 0x5FA36A, 0x3FF0D8);
    public static final DeferredItem<DeferredSpawnEggItem> HARMONIZER_SPAWN_EGG = egg("harmonizer", ModEntities.HARMONIZER, 0x2F6B4A, 0xF2C94C);
    public static final DeferredItem<DeferredSpawnEggItem> DARTBACK_SPAWN_EGG = egg("dartback", ModEntities.DARTBACK, 0x6B2B3A, 0x5FD3FF);
    public static final DeferredItem<DeferredSpawnEggItem> HUNTER_SPAWN_EGG = egg("hunter", ModEntities.HUNTER, 0x10242B, 0xD9772E);
    public static final DeferredItem<DeferredSpawnEggItem> SCAVENGER_SPAWN_EGG = egg("scavenger", ModEntities.SCAVENGER, 0x10242B, 0xE6C24A);
    public static final DeferredItem<DeferredSpawnEggItem> STALKER_SPAWN_EGG = egg("stalker", ModEntities.STALKER, 0x10242B, 0x3E7CC9);
    public static final DeferredItem<DeferredSpawnEggItem> TRAPPER_SPAWN_EGG = egg("trapper", ModEntities.TRAPPER, 0x10242B, 0xE07AB8);
    public static final DeferredItem<DeferredSpawnEggItem> MONARCH_SPAWN_EGG = egg("monarch", ModEntities.MONARCH, 0x1A1A2E, 0xE8731A);

    private ModItems() {
    }

    private static <I extends Item> DeferredItem<I> tab(String name, Supplier<I> item) {
        DeferredItem<I> holder = ITEMS.register(name, item);
        TAB_ITEMS.add(holder);
        return holder;
    }

    private static DeferredItem<SiftArmorItem> armor(String name, Holder<ArmorMaterial> material, ArmorItem.Type type, int durability) {
        return tab(name, () -> new SiftArmorItem(material, type,
                new Item.Properties().durability(type.getDurability(durability))));
    }

    private static <T extends Mob> DeferredItem<DeferredSpawnEggItem> egg(String name, Supplier<EntityType<T>> type, int bg, int fg) {
        DeferredItem<DeferredSpawnEggItem> holder = ITEMS.register(name + "_spawn_egg",
                () -> new DeferredSpawnEggItem(type, bg, fg, new Item.Properties()));
        SPAWN_EGGS.add(holder);
        return holder;
    }
}
