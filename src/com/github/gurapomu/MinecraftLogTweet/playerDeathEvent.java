package com.github.gurapomu.MinecraftLogTweet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class playerDeathEvent implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerDeath(PlayerDeathEvent event){

        if(MinecraftLogTweet.deathTweet == true){

            Authorization.tweetString(event.getDeathMessage() + "  " + MinecraftLogTweet.getTime());
        }
	}
}
