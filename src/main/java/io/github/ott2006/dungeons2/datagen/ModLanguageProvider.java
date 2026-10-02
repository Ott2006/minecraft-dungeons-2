package io.github.ott2006.dungeons2.datagen;

import io.github.ott2006.dungeons2.Dungeons2;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/** English and German translations. Every entry is written once with both languages side by side. */
public abstract class ModLanguageProvider extends LanguageProvider {
    private final boolean german;

    protected ModLanguageProvider(PackOutput output, String locale, boolean german) {
        super(output, Dungeons2.MOD_ID, locale);
        this.german = german;
    }

    public static class English extends ModLanguageProvider {
        public English(PackOutput output) {
            super(output, "en_us", false);
        }
    }

    public static class German extends ModLanguageProvider {
        public German(PackOutput output) {
            super(output, "de_de", true);
        }
    }

    private void t(String key, String en, String de) {
        this.add(key, this.german ? de : en);
    }

    private void block(String name, String en, String de) {
        this.t("block.dungeons2." + name, en, de);
    }

    private void item(String name, String en, String de) {
        this.t("item.dungeons2." + name, en, de);
    }

    private void desc(String name, String en, String de) {
        this.t("item.dungeons2." + name + ".desc", en, de);
    }

    private void entity(String name, String en, String de) {
        this.t("entity.dungeons2." + name, en, de);
        if (!name.equals("sift_dart") && !name.equals("goo_glob") && !name.equals("soul_wisp") && !name.equals("monarch_feather")) {
            this.item(name + "_spawn_egg", en + " Spawn Egg", de + "-Spawn-Ei");
        }
    }

    @Override
    protected void addTranslations() {
        this.t("itemGroup.dungeons2.the_sift", "Dungeons II: The Sift", "Dungeons II: Die Sift");

        // ---------------------------------------------------------------- blocks
        this.block("siftstone", "Siftstone", "Siftstein");
        this.block("cobbled_siftstone", "Cobbled Siftstone", "Bruch-Siftstein");
        this.block("cobbled_siftstone_stairs", "Cobbled Siftstone Stairs", "Bruch-Siftsteintreppe");
        this.block("cobbled_siftstone_slab", "Cobbled Siftstone Slab", "Bruch-Siftsteinstufe");
        this.block("cobbled_siftstone_wall", "Cobbled Siftstone Wall", "Bruch-Siftsteinmauer");
        this.block("polished_siftstone", "Polished Siftstone", "Polierter Siftstein");
        this.block("polished_siftstone_stairs", "Polished Siftstone Stairs", "Polierte Siftsteintreppe");
        this.block("polished_siftstone_slab", "Polished Siftstone Slab", "Polierte Siftsteinstufe");
        this.block("polished_siftstone_wall", "Polished Siftstone Wall", "Polierte Siftsteinmauer");
        this.block("siftstone_bricks", "Siftstone Bricks", "Siftsteinziegel");
        this.block("siftstone_brick_stairs", "Siftstone Brick Stairs", "Siftsteinziegeltreppe");
        this.block("siftstone_brick_slab", "Siftstone Brick Slab", "Siftsteinziegelstufe");
        this.block("siftstone_brick_wall", "Siftstone Brick Wall", "Siftsteinziegelmauer");
        this.block("chiseled_siftstone", "Chiseled Siftstone", "Gemeißelter Siftstein");
        this.block("siftstone_coal_ore", "Siftstone Coal Ore", "Siftstein-Steinkohle");
        this.block("siftstone_iron_ore", "Siftstone Iron Ore", "Siftstein-Eisenerz");
        this.block("siftstone_copper_ore", "Siftstone Copper Ore", "Siftstein-Kupfererz");
        this.block("siftstone_gold_ore", "Siftstone Gold Ore", "Siftstein-Golderz");
        this.block("siftstone_diamond_ore", "Siftstone Diamond Ore", "Siftstein-Diamanterz");
        this.block("carapace_sand", "Carapace Sand", "Panzersand");
        this.block("carapace_sandstone", "Carapace Sandstone", "Panzersandstein");
        this.block("green_healthy_sculk", "Green Healthy Sculk", "Grünes gesundes Sculk");
        this.block("orange_healthy_sculk", "Orange Healthy Sculk", "Oranges gesundes Sculk");
        this.block("orange_sculk_grass_block", "Orange Sculk Grass Block", "Orangefarbener Sculk-Grasblock");
        this.block("light_orange_sculk_grass_block", "Light Orange Sculk Grass Block", "Hellorangefarbener Sculk-Grasblock");
        this.block("green_sculk_grass_block", "Green Sculk Grass Block", "Grüner Sculk-Grasblock");
        this.block("orange_short_sculk_grass", "Orange Short Sculk Grass", "Kurzes orangefarbenes Sculk-Gras");
        this.block("light_orange_short_sculk_grass", "Light Orange Short Sculk Grass", "Kurzes hellorangefarbenes Sculk-Gras");
        this.block("green_short_sculk_grass", "Green Short Sculk Grass", "Kurzes grünes Sculk-Gras");
        this.block("tall_sculk_grass", "Tall Sculk Grass", "Hohes Sculk-Gras");
        this.block("green_tall_sculk_grass", "Green Tall Sculk Grass", "Hohes grünes Sculk-Gras");
        this.block("white_willow_log", "White Willow Log", "Weißweidenstamm");
        this.block("white_willow_wood", "White Willow Wood", "Weißweidenholz");
        this.block("stripped_white_willow_log", "Stripped White Willow Log", "Entrindeter Weißweidenstamm");
        this.block("stripped_white_willow_wood", "Stripped White Willow Wood", "Entrindetes Weißweidenholz");
        this.block("white_willow_planks", "White Willow Planks", "Weißweidenbretter");
        this.block("white_willow_stairs", "White Willow Stairs", "Weißweidentreppe");
        this.block("white_willow_slab", "White Willow Slab", "Weißweidenstufe");
        this.block("white_willow_fence", "White Willow Fence", "Weißweidenzaun");
        this.block("white_willow_fence_gate", "White Willow Fence Gate", "Weißweidenzauntor");
        this.block("white_willow_door", "White Willow Door", "Weißweidentür");
        this.block("white_willow_trapdoor", "White Willow Trapdoor", "Weißweidenfalltür");
        this.block("white_willow_button", "White Willow Button", "Weißweidenknopf");
        this.block("white_willow_pressure_plate", "White Willow Pressure Plate", "Weißweidendruckplatte");
        this.block("white_willow_leaves", "White Willow Leaves", "Weißweidenlaub");
        this.block("white_willow_sapling", "White Willow Sapling", "Weißweidensetzling");
        this.block("weeping_white_willow", "Weeping White Willow", "Hängende Weißweide");
        this.block("soul_block", "Soul Block", "Seelenblock");
        this.block("resonant_deepslate", "Resonant Deepslate", "Resonanter Tiefenschiefer");
        this.block("sift_portal", "Deep Dark Portal", "Portal des Tiefen Dunkels");
        this.block("spectral_barrier", "Spectral Barrier", "Spektrale Barriere");
        this.block("ichor", "Ichor", "Ichor");

        // ---------------------------------------------------------------- items
        this.item("ichor_bucket", "Bucket of Ichor", "Ichoreimer");
        this.item("singers_horn", "Singer's Horn", "Horn des Sängers");
        this.desc("singers_horn", "Plays the song of the Singers. Use it on a frame of reinforced or resonant deepslate to open a portal to the Sift.",
                "Spielt das Lied der Sänger. An einem Rahmen aus verstärktem oder resonantem Tiefenschiefer öffnet es ein Portal in die Sift.");
        this.item("monarch_lure", "Monarch Lure", "Monarchenköder");
        this.desc("monarch_lure", "Use it in the Sift to summon the Monarch.", "In der Sift benutzen, um den Monarchen herbeizurufen.");
        this.item("sifter_chitin", "Sifter Chitin", "Sifter-Chitin");
        this.item("sculker_claw", "Sculker Claw", "Sculker-Klaue");
        this.item("soul_fragment", "Soul Fragment", "Seelenfragment");
        this.item("enraged_heart", "Enraged Heart", "Rasendes Herz");
        this.item("monarch_feather", "Monarch Feather", "Monarchenfeder");
        this.item("sift_dart", "Sift Dart", "Sift-Pfeil");
        this.item("goo_glob", "Goo Glob", "Schleimklumpen");
        this.item("soul_wisp", "Soul Wisp", "Seelenirrlicht");

        this.item("battlestaff", "Battlestaff", "Kampfstab");
        this.desc("battlestaff", "A large two-ended staff with long reach and heavy knockback.",
                "Ein großer zweiseitiger Stab mit großer Reichweite und starkem Rückstoß.");
        this.item("riftslasher", "Riftslasher", "Riftschlitzer");
        this.desc("riftslasher", "Every third hit tears a small rift that hurts all enemies around the target.",
                "Jeder dritte Treffer reißt einen kleinen Riss auf, der alle Gegner um das Ziel verletzt.");
        this.item("spectral_spear", "Spectral Spear", "Spektralspeer");
        this.desc("spectral_spear", "Forged in ichor. Inflicts additional soul damage.",
                "In Ichor geschmiedet. Verursacht zusätzlichen Seelenschaden.");
        this.item("sculkers_bane", "Sculker's Bane", "Sculkerfluch");
        this.desc("sculkers_bane", "Deals 50% more damage to sculk creatures.", "Verursacht 50 % mehr Schaden an Sculk-Kreaturen.");
        this.item("sculker_claws", "Sculker Claws", "Sculker-Klauen");
        this.desc("sculker_claws", "Fast claws. The third hit of a combo strikes hard.",
                "Schnelle Klauen. Der dritte Treffer einer Kombo trifft besonders hart.");
        this.item("cacophonous_cleaver", "Cacophonous Cleaver", "Kakophonisches Hackbeil");
        this.desc("cacophonous_cleaver", "Restores health for every soul gathered.", "Stellt für jede gesammelte Seele Gesundheit wieder her.");
        this.item("soul_reaper", "Soul Reaper", "Seelenschnitter");
        this.desc("soul_reaper", "Increases souls gathered (experience) by 60%.", "Erhöht gesammelte Seelen (Erfahrung) um 60 %.");

        this.item("echo_ocarina", "Echo Ocarina", "Echo-Okarina");
        this.desc("echo_ocarina", "Protects you and your allies against ranged attacks.", "Schützt dich und deine Verbündeten vor Fernangriffen.");
        this.item("warding_chimes", "Warding Chimes", "Schutzglockenspiel");
        this.desc("warding_chimes", "Their music turns your skin as thick as carapace.", "Ihre Musik macht deine Haut so dick wie einen Panzer.");
        this.item("humbling_horn", "Humbling Horn", "Demütigendes Horn");
        this.desc("humbling_horn", "Knock enemies over and leave their ears ringing.", "Wirft Gegner um und lässt ihre Ohren klingeln.");
        this.item("corrupted_seeds", "Corrupted Seeds", "Verderbte Samen");
        this.desc("corrupted_seeds", "Instantly sprout into deadly vines, poisoning and binding enemies.",
                "Sprießen sofort zu tödlichen Ranken, die Gegner vergiften und festhalten.");
        this.item("soul_harvester", "Soul Harvester", "Seelenernter");
        this.desc("soul_harvester", "A soul-powered hex that hurts every enemy around you and heals you.",
                "Ein seelenbetriebener Fluch, der alle Gegner um dich verletzt und dich heilt.");
        this.item("corrupted_beacon", "Corrupted Beacon", "Verderbtes Leuchtfeuer");
        this.desc("corrupted_beacon", "Fires a soul beam that pierces every enemy in its path.",
                "Feuert einen Seelenstrahl, der alle Gegner in seiner Bahn durchbohrt.");
        this.t("item.dungeons2.artifact.cooldown", "Cooldown: %s s", "Abklingzeit: %s s");

        this.item("sifter_helmet", "Sifter Helmet", "Sifter-Helm");
        this.item("sifter_chestplate", "Sifter Chestplate", "Sifter-Brustpanzer");
        this.item("sifter_leggings", "Sifter Leggings", "Sifter-Beinschutz");
        this.item("sifter_boots", "Sifter Boots", "Sifter-Stiefel");
        this.item("mad_sifter_helmet", "Mad Sifter Helmet", "Wahnsinniger Sifter-Helm");
        this.item("mad_sifter_chestplate", "Mad Sifter Chestplate", "Wahnsinniger Sifter-Brustpanzer");
        this.item("mad_sifter_leggings", "Mad Sifter Leggings", "Wahnsinniger Sifter-Beinschutz");
        this.item("mad_sifter_boots", "Mad Sifter Boots", "Wahnsinnige Sifter-Stiefel");
        this.t("item.dungeons2.sifter_armor.desc", "Full set: immunity to ichor", "Komplettes Set: Immunität gegen Ichor");
        this.t("item.dungeons2.mad_sifter_armor.desc", "Full set: frenzy when badly hurt", "Komplettes Set: Raserei bei schweren Verletzungen");

        // ---------------------------------------------------------------- entities
        this.entity("blub", "Blub", "Blub");
        this.entity("singer", "Singer", "Sänger");
        this.entity("echo_golem", "Echo Golem", "Echogolem");
        this.entity("seedling", "Seedling", "Setzling");
        this.entity("sentinel", "Sentinel", "Wächter");
        this.entity("pollinator", "Pollinator", "Bestäuber");
        this.entity("nester", "Nester", "Nister");
        this.entity("sprout", "Sprout", "Spross");
        this.entity("harmonizer", "Harmonizer", "Harmonisierer");
        this.entity("dartback", "Dartback", "Pfeilrücken");
        this.entity("hunter", "Hunter", "Jäger");
        this.entity("scavenger", "Scavenger", "Plünderer");
        this.entity("stalker", "Stalker", "Pirscher");
        this.entity("trapper", "Trapper", "Fallensteller");
        this.entity("monarch", "Monarch", "Monarch");
        this.entity("sift_dart", "Sift Dart", "Sift-Pfeil");
        this.entity("goo_glob", "Goo Glob", "Schleimklumpen");
        this.entity("soul_wisp", "Soul Wisp", "Seelenirrlicht");
        this.entity("monarch_feather", "Monarch Feather", "Monarchenfeder");

        // ---------------------------------------------------------------- world
        this.t("biome.dungeons2.singers_meadow", "Singer's Meadow", "Sängerwiese");
        this.t("biome.dungeons2.lullaby_hills", "Lullaby Hills", "Wiegenliedhügel");
        this.t("biome.dungeons2.the_carapace", "The Carapace", "Der Panzer");
        this.t("biome.dungeons2.ichor_ravines", "Ichor Ravines", "Ichorschluchten");
        this.t("dimension.dungeons2.the_sift", "The Sift", "Die Sift");

        this.t("effect.dungeons2.soul_burn", "Soul Burn", "Seelenbrand");
        this.t("effect.dungeons2.echo_ward", "Echo Ward", "Echoschutz");
        this.t("effect.dungeons2.enraged", "Enraged", "Raserei");

        this.t("death.attack.dungeons2.soulBurn", "%1$s was drained by soul fire", "%1$s wurde von Seelenfeuer ausgezehrt");
        this.t("death.attack.dungeons2.soulBurn.player", "%1$s was drained by soul fire while fighting %2$s",
                "%1$s wurde im Kampf gegen %2$s von Seelenfeuer ausgezehrt");

        this.t("tide.dungeons2.flow", "Flow", "Fluss");
        this.t("tide.dungeons2.thrive", "Thrive", "Gedeihen");
        this.t("tide.dungeons2.endure", "Endure", "Ausdauer");
        this.t("message.dungeons2.tide_shift", "The tide of the Sift shifts: %s", "Die Gezeiten der Sift wandeln sich: %s");
        this.t("message.dungeons2.portal_incomplete", "The song echoes, but the frame is not closed.",
                "Das Lied hallt wider, doch der Rahmen ist nicht geschlossen.");
        this.t("message.dungeons2.singer.needs_soul_block", "The Singer needs a Soul Block nearby to create an Echo Golem.",
                "Der Sänger braucht einen Seelenblock in der Nähe, um einen Echogolem zu erschaffen.");
        this.t("message.dungeons2.monarch_lure.wrong_dimension", "The Monarch only answers in the Sift.",
                "Der Monarch antwortet nur in der Sift.");
    }
}
