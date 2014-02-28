package com.github.gurapomu.MinecraftLogTweet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class playerJoinEvent extends JavaPlugin implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerJoin(PlayerJoinEvent event){
		
		for(int i = 0;; i++){
			
			if(MinecraftLogTweet.playerName[i] == null){
				
				MinecraftLogTweet.playerName[i] = event.getPlayer();
				break;
			}
		}
		
		MinecraftLogTweet.player++;
		String str = MinecraftLogTweet.loginMessage;
		str = MinecraftLogTweet.subStr(str, "[playerName]", event.getPlayer().getName());
		str = MinecraftLogTweet.checkConstants(str);
		
		Authorization.tweetString(str);
		
		MinecraftLogTweet.server = this.getServer();
	}
}
