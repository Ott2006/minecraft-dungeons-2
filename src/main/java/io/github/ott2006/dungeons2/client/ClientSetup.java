package io.github.ott2006.dungeons2.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.shaders.FogShape;
import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.client.geo.GeoDefinition;
import io.github.ott2006.dungeons2.client.model.BlubModel;
import io.github.ott2006.dungeons2.client.model.DartbackModel;
import io.github.ott2006.dungeons2.client.model.EchoGolemModel;
import io.github.ott2006.dungeons2.client.model.MonarchModel;
import io.github.ott2006.dungeons2.client.model.NesterModel;
import io.github.ott2006.dungeons2.client.model.PollinatorModel;
import io.github.ott2006.dungeons2.client.model.SculkerModel;
import io.github.ott2006.dungeons2.client.model.SeedlingModel;
import io.github.ott2006.dungeons2.client.model.SentinelModel;
import io.github.ott2006.dungeons2.client.model.SingerModel;
import io.github.ott2006.dungeons2.client.model.SproutModel;
import io.github.ott2006.dungeons2.client.render.EnragedLayer;
import io.github.ott2006.dungeons2.client.render.GeoMobRenderer;
import io.github.ott2006.dungeons2.client.render.GlowLayer;
import io.github.ott2006.dungeons2.entity.Blub;
import io.github.ott2006.dungeons2.entity.Monarch;
import io.github.ott2006.dungeons2.entity.Sculker;
import io.github.ott2006.dungeons2.entity.SiftMonster;
import io.github.ott2006.dungeons2.entity.Sprout;
import io.github.ott2006.dungeons2.registry.ModEntities;
import io.github.ott2006.dungeons2.registry.ModFluids;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Camera;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/** Client-only registrations: entity models, renderers and the look of ichor. */
public final class ClientSetup {
    private static final String[] GEO_MODELS = {"blub", "singer", "echo_golem", "seedling", "sentinel", "pollinator", "nester",
            "sprout", "dartback", "sculker", "monarch"};
    private static final Map<String, ModelLayerLocation> LAYERS = new java.util.HashMap<>();

    private ClientSetup() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::onRegisterLayers);
        modBus.addListener(ClientSetup::onRegisterRenderers);
        modBus.addListener(ClientSetup::onRegisterClientExtensions);
        modBus.addListener(ClientSetup::onClientSetup);
    }

    private static ModelLayerLocation layer(String name) {
        return LAYERS.computeIfAbsent(name, n -> new ModelLayerLocation(Dungeons2.id(n), "main"));
    }

    private static ResourceLocation texture(String path) {
        return Dungeons2.id("textures/entity/" + path + ".png");
    }

    private static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (String name : GEO_MODELS) {
            event.registerLayerDefinition(layer(name), () -> GeoDefinition.get(name).createLayer());
        }
    }

    private static <T extends Mob, M extends EntityModel<T>> void mob(EntityRenderersEvent.RegisterRenderers event, EntityType<T> type,
                                                                       String geo, Function<ModelPart, M> model, float shadow, float scale,
                                                                       Function<T, ResourceLocation> tex, Function<T, ResourceLocation> glow) {
        event.registerEntityRenderer(type, (EntityRendererProvider<T>) context -> {
            GeoMobRenderer<T, M> renderer = new GeoMobRenderer<>(context, model.apply(context.bakeLayer(layer(geo))), shadow, scale, tex);
            if (glow != null) {
                renderer.addLayer(new GlowLayer<>(renderer, glow));
            }
            return renderer;
        });
    }

    private static <T extends SiftMonster, M extends EntityModel<T>> void monster(EntityRenderersEvent.RegisterRenderers event, EntityType<T> type,
                                                                                   String geo, Function<ModelPart, M> model, float shadow, float scale,
                                                                                   Function<T, ResourceLocation> tex, Function<T, ResourceLocation> glow) {
        event.registerEntityRenderer(type, (EntityRendererProvider<T>) context -> {
            GeoMobRenderer<T, M> renderer = new GeoMobRenderer<>(context, model.apply(context.bakeLayer(layer(geo))), shadow, scale, tex);
            if (glow != null) {
                renderer.addLayer(new GlowLayer<>(renderer, glow));
            }
            renderer.addLayer(new EnragedLayer<>(renderer));
            return renderer;
        });
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        Map<Blub.Variant, ResourceLocation> blubTextures = new EnumMap<>(Blub.Variant.class);
        Map<Blub.Variant, ResourceLocation> blubGlow = new EnumMap<>(Blub.Variant.class);
        for (Blub.Variant v : Blub.Variant.values()) {
            blubTextures.put(v, texture("blub/" + v.name));
            blubGlow.put(v, v.glows ? texture("blub/" + v.name + "_glow") : null);
        }
        mob(event, ModEntities.BLUB.get(), "blub", BlubModel::new, 0.3F, 1.0F,
                e -> blubTextures.get(e.getVariant()), e -> blubGlow.get(e.getVariant()));

        ResourceLocation singer = texture("singer");
        ResourceLocation singerGlow = texture("singer_glow");
        mob(event, ModEntities.SINGER.get(), "singer", SingerModel::new, 0.8F, 1.0F, e -> singer, e -> singerGlow);

        ResourceLocation golem = texture("echo_golem");
        ResourceLocation golemGlow = texture("echo_golem_glow");
        mob(event, ModEntities.ECHO_GOLEM.get(), "echo_golem", EchoGolemModel::new, 0.6F, 1.0F, e -> golem,
                e -> e.isDormant() ? null : golemGlow);

        simpleMonster(event, ModEntities.SEEDLING.get(), "seedling", SeedlingModel::new, 0.35F);
        simpleMonster(event, ModEntities.SENTINEL.get(), "sentinel", SentinelModel::new, 0.5F);
        simpleMonster(event, ModEntities.POLLINATOR.get(), "pollinator", PollinatorModel::new, 0.6F);
        simpleMonster(event, ModEntities.NESTER.get(), "nester", NesterModel::new, 0.6F);
        ResourceLocation dartback = texture("dartback");
        ResourceLocation dartbackGlow = texture("dartback_glow");
        monster(event, ModEntities.DARTBACK.get(), "dartback", DartbackModel::new, 0.9F, 1.6F, e -> dartback, e -> dartbackGlow);

        ResourceLocation sprout = texture("sprout");
        ResourceLocation sproutGlow = texture("sprout_glow");
        monster(event, ModEntities.SPROUT.get(), "sprout", SproutModel::new, 0.5F, 1.0F, e -> sprout,
                e -> e.isAggressive() || e.getAction() == Sprout.ACTION_BEAM ? sproutGlow : null);
        ResourceLocation harmonizer = texture("harmonizer");
        ResourceLocation harmonizerGlow = texture("harmonizer_glow");
        monster(event, ModEntities.HARMONIZER.get(), "sprout", SproutModel::new, 0.5F, 2.4F, e -> harmonizer, e -> harmonizerGlow);

        sculker(event, ModEntities.HUNTER.get(), Sculker.Kind.HUNTER);
        sculker(event, ModEntities.SCAVENGER.get(), Sculker.Kind.SCAVENGER);
        sculker(event, ModEntities.STALKER.get(), Sculker.Kind.STALKER);
        sculker(event, ModEntities.TRAPPER.get(), Sculker.Kind.TRAPPER);

        ResourceLocation monarch = texture("monarch");
        ResourceLocation monarchEcho = texture("monarch_echo");
        ResourceLocation monarchGlow = texture("monarch_glow");
        monster(event, ModEntities.MONARCH.get(), "monarch", MonarchModel::new, 0.9F, 1.25F,
                (Monarch e) -> e.isEcho() ? monarchEcho : monarch, e -> e.isEcho() ? null : monarchGlow);

        event.registerEntityRenderer(ModEntities.SIFT_DART.get(), context -> new ThrownItemRenderer<>(context, 0.75F, false));
        event.registerEntityRenderer(ModEntities.GOO_GLOB.get(), context -> new ThrownItemRenderer<>(context, 1.25F, false));
        event.registerEntityRenderer(ModEntities.SOUL_WISP.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
        event.registerEntityRenderer(ModEntities.MONARCH_FEATHER.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
    }

    private static <T extends SiftMonster, M extends EntityModel<T>> void simpleMonster(EntityRenderersEvent.RegisterRenderers event,
                                                                                         EntityType<T> type, String name,
                                                                                         Function<ModelPart, M> model, float shadow) {
        ResourceLocation tex = texture(name);
        ResourceLocation glow = texture(name + "_glow");
        monster(event, type, name, model, shadow, 1.0F, e -> tex, e -> glow);
    }

    private static void sculker(EntityRenderersEvent.RegisterRenderers event, EntityType<Sculker> type, Sculker.Kind kind) {
        ResourceLocation tex = texture("sculker/" + kind.name);
        ResourceLocation glow = texture("sculker/" + kind.name + "_glow");
        monster(event, type, "sculker", SculkerModel::new, 0.5F, 1.0F, e -> tex, e -> glow);
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = Dungeons2.id("block/ichor_still");
            private static final ResourceLocation FLOW = Dungeons2.id("block/ichor_flow");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOW;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance,
                                                    float darkenWorldAmount, Vector3f fluidFogColor) {
                return new Vector3f(0.55F, 0.08F, 0.3F);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick,
                                        float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(0.0F);
                RenderSystem.setShaderFogEnd(4.0F);
            }
        }, ModFluids.ICHOR_TYPE.get());
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.ICHOR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_ICHOR.get(), RenderType.translucent());
        });
    }
}
