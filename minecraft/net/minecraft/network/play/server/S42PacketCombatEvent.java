package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.CombatTracker;

public class S42PacketCombatEvent implements Packet<INetHandlerPlayClient> {
	public S42PacketCombatEvent.Event eventType;
	public int field_179774_b;
	public int field_179775_c;
	public int field_179772_d;
	public String deathMessage;

	public S42PacketCombatEvent() {
	}

	@SuppressWarnings("incomplete-switch")
	public S42PacketCombatEvent(CombatTracker combatTrackerIn, S42PacketCombatEvent.Event combatEventType) {
		this.eventType = combatEventType;
		EntityLivingBase entitylivingbase = combatTrackerIn.func_94550_c();

		switch (combatEventType) {
		case END_COMBAT:
			this.field_179772_d = combatTrackerIn.func_180134_f();
			this.field_179775_c = entitylivingbase == null ? -1 : entitylivingbase.getEntityId();
			break;

		case ENTITY_DIED:
			this.field_179774_b = combatTrackerIn.getFighter().getEntityId();
			this.field_179775_c = entitylivingbase == null ? -1 : entitylivingbase.getEntityId();
			this.deathMessage = combatTrackerIn.getDeathMessage().getUnformattedText();
		}
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.eventType = (S42PacketCombatEvent.Event) buf.readEnumValue(S42PacketCombatEvent.Event.class);

		if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
			this.field_179772_d = buf.readVarIntFromBuffer();
			this.field_179775_c = buf.readInt();
		} else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
			this.field_179774_b = buf.readVarIntFromBuffer();
			this.field_179775_c = buf.readInt();
			this.deathMessage = buf.readStringFromBuffer(32767);
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeEnumValue(this.eventType);

		if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
			buf.writeVarIntToBuffer(this.field_179772_d);
			buf.writeInt(this.field_179775_c);
		} else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
			buf.writeVarIntToBuffer(this.field_179774_b);
			buf.writeInt(this.field_179775_c);
			buf.writeString(this.deathMessage);
		}
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleCombatEvent(this);
	}

	public static enum Event {
		ENTER_COMBAT, END_COMBAT, ENTITY_DIED;
	}
}
