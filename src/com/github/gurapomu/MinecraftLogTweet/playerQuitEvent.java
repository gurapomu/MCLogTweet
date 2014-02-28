package com.github.gurapomu.MinecraftLogTweet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class playerQuitEvent implements Listener {

	private MinecraftLogTweet plugin;
	
	public playerQuitEvent(MinecraftLogTweet plugin) {
		
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerQuit(PlayerQuitEvent event){
		
		for(int i = 0;; i++){
			
			if(MinecraftLogTweet.playerName[i] == event.getPlayer()){
				
				MinecraftLogTweet.playerName[i] = null;
				break;
			}
		}
		
		MinecraftLogTweet.player--;
		String str = MinecraftLogTweet.logoutMessage;
		str = MinecraftLogTweet.subStr(str, "[playerName]", event.getPlayer().getName());
		str = MinecraftLogTweet.checkConstants(str);
		
		Authorization.tweetString(str);
		
		MinecraftLogTweet.server = plugin.getServer();
	}
}
