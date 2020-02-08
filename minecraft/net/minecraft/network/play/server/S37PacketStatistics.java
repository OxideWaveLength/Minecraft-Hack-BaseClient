package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics implements Packet<INetHandlerPlayClient> {
	private Map<StatBase, Integer> field_148976_a;

	public S37PacketStatistics() {
	}

	public S37PacketStatistics(Map<StatBase, Integer> p_i45173_1_) {
		this.field_148976_a = p_i45173_1_;
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleStatistics(this);
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		int i = buf.readVarIntFromBuffer();
		this.field_148976_a = Maps.<StatBase, Integer>newHashMap();

		for (int j = 0; j < i; ++j) {
			StatBase statbase = StatList.getOneShotStat(buf.readStringFromBuffer(32767));
			int k = buf.readVarIntFromBuffer();

			if (statbase != null) {
				this.field_148976_a.put(statbase, Integer.valueOf(k));
			}
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.field_148976_a.size());

		for (Entry<StatBase, Integer> entry : this.field_148976_a.entrySet()) {
			buf.writeString(((StatBase) entry.getKey()).statId);
			buf.writeVarIntToBuffer(((Integer) entry.getValue()).intValue());
		}
	}

	public Map<StatBase, Integer> func_148974_c() {
		return this.field_148976_a;
	}
}
