package me.wavelength.baseclient.module.modules.world;

import java.util.HashMap;
import java.util.Map;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.commands.NamesCommand;
import me.wavelength.baseclient.event.events.MessageReceivedEvent;
import me.wavelength.baseclient.event.events.PlayerSpawnEvent;
import me.wavelength.baseclient.event.events.RenderLivingLabelEvent;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class NameProtect extends Module {

	private Map<String, String> names;

	public NameProtect() {
		super("Name Protect", "Hide players names", 0, Category.WORLD);

		this.names = new HashMap<String, String>();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String message = event.getMessage();

		if (!(names.containsKey(mc.thePlayer.getName())))
			getNewName(mc.thePlayer.getName());

		for (String name : names.keySet()) {
			String newName = getNewName(name);
			message = message.replace(name, newName);
		}

		event.setMessage(message);
	}

	@Override
	public void onRenderLivingLabel(RenderLivingLabelEvent event) {
		if (!(event.getEntity() instanceof EntityOtherPlayerMP))
			return;

		if (event.getEntity() == null || event.getEntity().getName() == null)
			return;

		if (((NamesCommand) BaseClient.instance.getCommandManager().getCommand(NamesCommand.class)).isInExceptions(event.getEntity().getName()))
			return;

		event.setLabel(getNewName(event.getEntity().getName()));
	}

	@Override
	public void onPlayerSpawn(PlayerSpawnEvent event) {
		getNewName(event.getEntity().getName());
	}

	public String getNewName(String name) {
		String newName = null;
		if (!(names.containsKey(name))) {
			names.put(name, Strings.randomString(10, true, false, false) + (name.equals(mc.thePlayer.getName()) ? " (YOU)" : ""));
		}

		newName = names.get(name);

		return newName;
	}

}