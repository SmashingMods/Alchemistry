package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.network.jei.CombinerTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.CompactorTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.DissolverTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FissionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FusionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.LiquifierTransferPacket;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Pins {@link PacketHandler#register} to the set of payloads the mod actually sends. NeoForge refuses to send
 * any payload missing from its registration table: {@code PacketDistributor.sendToServer} reaches
 * {@code NetworkRegistry.checkPacket}, which throws {@code UnsupportedOperationException("Payload ... may not
 * be sent to the server!")} -- so a registration dropped here is a feature that dies at its send site, not a
 * compile error.
 *
 * <p>Regression: the six JEI recipe-transfer registrations were removed for the 1.21.3 hop (JEI had no 1.21.3
 * build, so the "+"-button senders were excluded with it) with a note to restore them at 1.21.4 alongside the
 * JEI integration. The integration returned; the registrations did not. Every machine's JEI "+" button then
 * died silently: the gating pass of {@code TransferHandler.transferRecipe} never touches the network, so the
 * button stayed lit, and the click's {@code sendToServer} threw inside the transfer handler, where JEI catches
 * and logs the error. Both existing gates were blind -- the packet round-trip tests exercise only the codecs,
 * and the transfer gametests invoke each packet's {@code handle()} directly, bypassing the registration table.</p>
 *
 * <p>This drives the production {@code register} with a real {@link RegisterPayloadHandlersEvent} (its no-arg
 * constructor and {@link NetworkRegistry} are internal API, which a test may lean on) and asserts against the
 * same static table the outbound send check consults. That table is JVM-global and rejects re-registration of
 * an id, so registration happens once in {@code @BeforeAll} and this class must stay the only unit test that
 * registers payloads. Extends {@link BootstrappedTest} because class-loading the packets initialises their
 * {@code STREAM_CODEC}s, which compose registry-backed codecs. The live-server twin of this pin -- which also
 * proves the mod constructor wired {@code PACKET_HANDLER::register} onto the mod bus, invisible from here --
 * is the payload-registration gametest in {@code AlchemistryGameTests}.</p>
 */
class PacketHandlerTest extends BootstrappedTest {

    /**
     * Every payload the mod sends to the server: the TYPE of each {@code Alchemistry.PACKET_HANDLER.sendToServer}
     * call site. The six recipe-transfer packets are sent by the JEI "+"-button transfer handlers in
     * {@code common/network/jei/}; the other four by GUI buttons and the recipe-selector screen.
     */
    private static final List<CustomPacketPayload.Type<?>> SENT_TO_SERVER = List.of(
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

    @BeforeAll
    static void registerPayloads() {
        new PacketHandler().register(new RegisterPayloadHandlersEvent());
    }

    @Test
    void everySentPayload_isRegisteredServerBoundForPlay() {
        for (CustomPacketPayload.Type<?> type : SENT_TO_SERVER) {
            assertNotNull(NetworkRegistry.getCodec(type.id(), ConnectionProtocol.PLAY, PacketFlow.SERVERBOUND),
                    () -> type.id() + " must be registered server-bound: an unregistered payload throws at its sendToServer call site");
        }
    }

    @Test
    void sentPayloads_doNotResolveForTheClientBoundFlow() {
        for (CustomPacketPayload.Type<?> type : SENT_TO_SERVER) {
            assertNull(NetworkRegistry.getCodec(type.id(), ConnectionProtocol.PLAY, PacketFlow.CLIENTBOUND),
                    () -> type.id() + " is server-bound only: a flow-inverted registration would kill the send the same way a missing one does");
        }
    }
}
