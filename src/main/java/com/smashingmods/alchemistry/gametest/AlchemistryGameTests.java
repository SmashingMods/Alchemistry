package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemistry.common.network.SetSideConfigurationPacket;
import com.smashingmods.alchemistry.common.network.ToggleAutoBalanceButtonPacket;
import com.smashingmods.alchemistry.common.network.ToggleReactorAutoejectPacket;
import com.smashingmods.alchemistry.common.network.jei.CombinerTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.CompactorTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.DissolverTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FissionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FusionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.LiquifierTransferPacket;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.util.List;

/**
 * In-game test bodies for Alchemistry. 1.21.5 replaced the annotation-driven gametest framework
 * ({@code @GameTest}/{@code @GameTestHolder}/{@code @PrefixGameTestTemplate}) with a registry-based system: each
 * test body is a {@code Consumer<GameTestHelper>} registered to {@link BuiltInRegistries#TEST_FUNCTION}, paired with
 * a {@code GameTestInstance} (a structure + run parameters) in the {@code TEST_INSTANCE} datapack registry. The
 * {@code gameTestServer} run boots a dedicated server, runs every registered instance, and exits non-zero if any
 * required test fails. {@link AlchemistryGameTestRegistry} owns the registration; the bodies stay here as thin
 * {@code static} methods. This package is compiled into {@code src/main} so the mod scan discovers it, but the
 * {@code jar} task excludes it so the tests never ship in the published jar.
 *
 * <p>The tests here are boot-time invariants: the chain load-smoke, a required check that asserts the full ChemLib ->
 * AlchemyLib -> Alchemistry chain registered before the server reached the in-world phase, and the
 * payload-registration pin, which asserts every packet the mod sends survived NeoForge's network setup. Every
 * gametest in this package -- these plus the richer per-machine and guidebook behaviour tests in
 * {@link MachineGameTests}, {@link ReactorGameTests}, {@link FluidMachineGameTests}, and {@link GuidebookGameTests}
 * -- is registered as {@code required}, so a failure in any (including a behavioural regression) drives a non-zero
 * {@code gameTestServer} exit and fails the gate. The load-smoke is the broadest check: it is the first to break if
 * the chain does not register at all.</p>
 */
public class AlchemistryGameTests {

    private AlchemistryGameTests() {}

    /**
     * Asserts the full mod chain registered, then succeeds. The registry lookups are static and do not need the
     * in-world structure, but running them inside a gametest proves the checks execute against a fully-booted server
     * with the chain loaded.
     *
     * <p>Each check is a static built-in registry lookup against a known id from one rung of the chain: a ChemLib
     * element (hydrogen, element #1, always present), an Alchemistry machine block, its block-entity type, and its
     * recipe type. A missing id means that rung never registered, so the gate fails the required test.</p>
     */
    public static void fullChainLoaded(GameTestHelper helper) {
        assertRegistered(helper, "chemlib element", BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath("chemlib", "hydrogen"));
        assertRegistered(helper, "alchemistry block", BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "dissolver"));
        assertRegistered(helper, "alchemistry block-entity type", BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "dissolver_block_entity"));
        assertRegistered(helper, "alchemistry recipe type", BuiltInRegistries.RECIPE_TYPE,
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "dissolver"));
        helper.succeed();
    }

    /**
     * Asserts every payload the mod sends to the server resolved a server-bound registration on a fully-booted
     * server -- i.e. that the mod constructor wired {@code PACKET_HANDLER::register} onto the mod bus AND that
     * {@code register()} listed the payload. NeoForge refuses to send an unregistered payload
     * ({@code UnsupportedOperationException} at the {@code sendToServer} call site), and JEI catches that throw
     * inside its transfer handlers, so the omission ships as a silently dead "+" button rather than a crash --
     * which is exactly how the six JEI transfer registrations' absence reached the 1.21.4 release: the per-machine
     * transfer gametests drive each packet's {@code handle()} directly and never cross the registration table.
     * {@code PacketHandlerTest} pins the same list at the unit layer; this one additionally proves the live
     * mod-bus wiring, which no plain-JUnit test can see.
     */
    public static void sentPayloadsRegisteredServerBound(GameTestHelper helper) {
        List<CustomPacketPayload.Type<?>> sentToServer = List.of(
                ToggleAutoBalanceButtonPacket.TYPE,
                SetRecipePacket.TYPE,
                CombinerTransferPacket.TYPE,
                CompactorTransferPacket.TYPE,
                DissolverTransferPacket.TYPE,
                FissionTransferPacket.TYPE,
                FusionTransferPacket.TYPE,
                LiquifierTransferPacket.TYPE,
                ToggleReactorAutoejectPacket.TYPE,
                SetSideConfigurationPacket.TYPE);
        for (CustomPacketPayload.Type<?> type : sentToServer) {
            if (NetworkRegistry.getCodec(type.id(), ConnectionProtocol.PLAY, PacketFlow.SERVERBOUND) == null) {
                helper.fail(Component.literal(String.format("payload not registered server-bound, its sendToServer call site will throw: %s", type.id())));
            }
        }
        helper.succeed();
    }

    private static void assertRegistered(GameTestHelper helper, String what, Registry<?> registry, ResourceLocation id) {
        if (!registry.containsKey(id)) {
            helper.fail(Component.literal(String.format("%s not registered: %s", what, id)));
        }
    }
}
