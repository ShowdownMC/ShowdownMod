package io.github.pixityomg.showdownmod;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Showdownmod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK =
            BUILDER.comment("Log the dirt block during common setup")
                    .define("log_dirt_block", true);

    private static final ModConfigSpec.IntValue MAGIC_NUMBER =
            BUILDER.comment("Some configurable magic number")
                    .defineInRange("magic_number", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRO =
            BUILDER.comment("Intro message for the magic number")
                    .define("magic_number_intro", "The magic number is... ");

    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_IDS =
            BUILDER.comment("List of item IDs to log during common setup")
                    .defineList("items", List.of("minecraft:iron_ingot"), Config::validateItemId);

    // --- Iron Golem Self-Destruct Settings ---
    private static final ModConfigSpec.BooleanValue ENABLE_ASCENDING_EXPLODING_GOLEMS =
            BUILDER.comment("Enable iron golems to ascend into the sky and explode, dropping iron.")
                    .define("enable_ascending_exploding_golems", true);

    private static final ModConfigSpec.DoubleValue GOLEM_ASCENT_SPEED =
            BUILDER.comment("The speed at which the iron golem ascends.")
                    .defineInRange("golem_ascent_speed", 0.1D, 0.01D, 1.0D);

    private static final ModConfigSpec.DoubleValue GOLEM_EXPLOSION_HEIGHT =
            BUILDER.comment("The Y-level height at which the iron golem will explode.")
                    .defineInRange("golem_explosion_height", 256.0D, 64.0D, 320.0D);

    private static final ModConfigSpec.DoubleValue GOLEM_EXPLOSION_POWER =
            BUILDER.comment("The power of the explosion when the iron golem detonates.")
                    .defineInRange("golem_explosion_power", 4.0D, 0.0D, 10.0D);

    private static final ModConfigSpec.IntValue GOLEM_IRON_DROP_AMOUNT =
            BUILDER.comment("The number of iron ingots dropped when the iron golem explodes.")
                    .defineInRange("golem_iron_drop_amount", 4, 3, 16);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    // --- Public accessors for new Golem settings ---
    public static boolean enableAscendingExplodingGolems;
    public static double golemAscentSpeed;
    public static double golemExplosionHeight;
    public static double golemExplosionPower;
    public static int golemIronDropAmount;

    private static boolean validateItemId(Object obj) {
        if (obj instanceof String id) {
            return BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id));
        }
        return false;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRO.get();
        items = ITEM_IDS.get().stream()
                .map(id -> BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(id)))
                .collect(Collectors.toSet());

        // Load new Golem settings
        enableAscendingExplodingGolems = ENABLE_ASCENDING_EXPLODING_GOLEMS.get();
        golemAscentSpeed = GOLEM_ASCENT_SPEED.get();
        golemExplosionHeight = GOLEM_EXPLOSION_HEIGHT.get();
        golemExplosionPower = GOLEM_EXPLOSION_POWER.get();
        golemIronDropAmount = GOLEM_IRON_DROP_AMOUNT.get();
    }
}
