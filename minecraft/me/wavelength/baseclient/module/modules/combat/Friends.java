package me.wavelength.baseclient.module.modules.combat;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.commands.FriendsCommand;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

public class Friends extends Module {

	public Friends() {
		super("Friends", "This mod enables friends. When you hit a friend, the event will be cancelled", 0, Category.COMBAT, false);
	}

	@Override
	public void setup() {
		setToggled(true);
	}

	@Override
	public void onPacketSent(PacketSentEvent event) {
		if (!(event.getPacket() instanceof C02PacketUseEntity))
			return;

		C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

		if (!(packet.getAction().equals(Action.ATTACK)))
			return;

		Entity entity = packet.getEntityFromWorld(mc.theWorld);

		if (!(entity instanceof EntityOtherPlayerMP || entity instanceof EntityPlayerMP))
			return;

		if (BaseClient.instance.getFriendsManager().isFriend(entity.getName()))
			event.setCancelled(true);
	}

}