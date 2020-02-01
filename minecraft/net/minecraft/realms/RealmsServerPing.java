package net.minecraft.realms;

public class RealmsServerPing
{
    public volatile String nrOfPlayers = "0";
    public volatile long lastPingSnapshot = 0L;
    public volatile String playerList = "";
}
