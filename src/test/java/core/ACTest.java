package core;

import client.TestEndimatedEntityRenderer;
import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.registry.LootInjectionRegistry;
import com.teamabnormals.abnormals_core.core.util.RegistryHelper;
import core.registry.TestEntities;
import core.registry.TestEntitySpawnHelper;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Test
@Mod(ACTest.MOD_ID)
@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ACTest {
	public static final String MOD_ID = "ac_test";
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

	public ACTest() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(EventPriority.LOWEST, this::commonSetup);

		REGISTRY_HELPER.getDeferredItemRegister().register(modEventBus);
		REGISTRY_HELPER.getDeferredBlockRegister().register(modEventBus);
		REGISTRY_HELPER.getDeferredSoundRegister().register(modEventBus);
		REGISTRY_HELPER.getDeferredEntityRegister().register(modEventBus);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(EventPriority.LOWEST, this::registerItemColors);
		});
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(() -> {
			GlobalEntityTypeAttributes.put(TestEntities.ENDIMATED_TEST.get(), CreatureEntity.func_233666_p_().create());
			GlobalEntityTypeAttributes.put(TestEntities.COW.get(), CowEntity.func_234188_eI_().create());
			TestEntitySpawnHelper.processSpawnAdditions();
		});
		this.registerLootInjectors();
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(TestEntities.COW.get(), CowRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(TestEntities.ENDIMATED_TEST.get(), TestEndimatedEntityRenderer::new);
	}

	@OnlyIn(Dist.CLIENT)
	private void registerItemColors(ColorHandlerEvent.Item event) {
		REGISTRY_HELPER.processSpawnEggColors(event);
	}

	private void registerLootInjectors() {
		LootInjectionRegistry.LootInjector injector = new LootInjectionRegistry.LootInjector(MOD_ID);
		injector.registerLootInjection(injector.buildLootBool("test", 1, 0), LootTables.CHESTS_NETHER_BRIDGE, LootTables.CHESTS_JUNGLE_TEMPLE);
	}
}