package com.github.gurapomu.MinecraftLogTweet;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

import com.github.gurapomu.MinecraftLogTweet.Authorization.MyStatusListener;

public class MinecraftLogTweet extends JavaPlugin {
	
	public static Player[] playerName = new Player[256];
	public static String serverName, serverMOTD;
	public static String startMessage, stopMessage, loginMessage, logoutMessage, tweetMessage, regularTweetMessage, regularTweetMessageNP, replyCommandOP;
	public static boolean timeline, startTweet, stopTweet, loginTweet, logoutTweet, deathTweet, regularTweet;
	public static int player;
	private ltCommand myExecutor;
	public static TwitterStream twStream = new TwitterStreamFactory().getInstance();
	public static Server server;
	static TimerTask task = new tweetTask();
	static Timer timer = new Timer("timer");
	
	public void onEnable(){

		this.saveDefaultConfig();
		this.reloadConfig();
		this.loadConfig();
		serverName = this.getServer().getName();
		serverMOTD = this.getServer().getMotd();
		getServer().getPluginManager().registerEvents(new playerJoinEvent(this), this);
		getServer().getPluginManager().registerEvents(new playerQuitEvent(this), this);
		getServer().getPluginManager().registerEvents(new playerDeathEvent(), this);
		
		player = this.getServer().getOnlinePlayers().length;
		if(this.getServer().getOnlinePlayers().length == 0){
			
			for(int i = 0; i < 256; i++){
				
				playerName[i] = null;
			}
		} else if(this.getServer().getOnlinePlayers().length > 0){
			
			playerName = this.getServer().getOnlinePlayers();
		}
		server = this.getServer();
		
		if(Authorization.loadAccessToken() == null){
			
			System.out.println("Please enter command (/getauthurl /authpin)");
		} else{

            if(MinecraftLogTweet.startTweet == true){

                String str = startMessage;
                str = checkConstants(str);

                Authorization.tweetString(str);
            }
			
			twStream.setOAuthConsumer(oauthKey.consumerKey, oauthKey.consumerSecret);
			twStream.setOAuthAccessToken(Authorization.loadAccessToken());
			twStream.addListener(new MyStatusListener());
			
			Authorization.startStreaming(twStream);
		}
		
		myExecutor = new ltCommand(this);
		getCommand("getauthurl").setExecutor(myExecutor);
		getCommand("authpin").setExecutor(myExecutor);
        getCommand("authrm").setExecutor(myExecutor);
		getCommand("tweet").setExecutor(myExecutor);
		getCommand("shinchoku").setExecutor(myExecutor);
		getCommand("prime").setExecutor(myExecutor);
		getCommand("reply").setExecutor(myExecutor);
		getCommand("rt").setExecutor(myExecutor);

		timer.schedule(task, 0, this.getConfig().getInt("tweetInterval") * 1000 * 60);
	}
	
	public void onDisable(){
		
		timer.cancel();
		
		if(Authorization.loadAccessToken() == null){
			
			System.out.println("Please enter command (/getauthurl /authpin)");
		} else{

            if(MinecraftLogTweet.stopTweet == true){

                String str = stopMessage;
                str = checkConstants(str);

                Authorization.tweetString(str);
            }
		}
		
		Authorization.stopStreaming(twStream);
	}
	public static String getTime(){
		
		Calendar now = Calendar.getInstance();
		
		int month = now.get(Calendar.MONTH) + 1;
		int date = now.get(Calendar.DATE);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minit = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		
		if(hour >= 10 && minit >= 10 && second >= 10){
		
			return(month + "/" + date + " " + hour + ":" + minit + ":" + second);
		} else if(hour < 10 && minit >= 10 && second >= 10){
			
			return(month + "/" + date + " 0" + hour + ":" + minit + ":" + second);
		} else if(hour >= 10 && minit < 10 && second >= 10){
			
			return(month + "/" + date + " " + hour + ":0" + minit + ":" + second);
		} else if(hour >= 10 && minit >= 10 && second < 10){
			
			return(month + "/" + date + " " + hour + ":" + minit + ":0" + second);
		} else if(hour < 10 && minit < 10 && second >= 10){
			
			return(month + "/" + date + " 0" + hour + ":0" + minit + ":" + second);
		} else if(hour >= 10 && minit < 10 && second < 10){
			
			return(month + "/" + date + " " + hour + ":0" + minit + ":0" + second);
		} else if(hour < 10 && minit >= 10 && second < 10){
			
			return(month + "/" + date + " 0" + hour + ":" + minit + ":0" + second);
		} else{
			
			return(month + "/" + date + " 0" + hour + ":0" + minit + ":0" + second);
		}
	}
	public static String getPlayerName(){
		
		String s = null;
		for(int i = 0; i < playerName.length; i++){
			
			if(playerName[i] != null && s == null)	s = playerName[i].getName();
			else if (playerName[i] != null)	s = s + ", " + playerName[i].getName();
		}
		
		return s;
	}
	public static String checkConstants(String str){
		
		str = subStr(str, "[screenName]", Authorization.getScreenName());
	 	str = subStr(str, "[dateAndTime]", getTime());
		str = subStr(str, "[serverName]", serverName);
		str = subStr(str, "[serverMOTD]", serverMOTD);
		str = subStr(str, "[onlinePlayerNumber]", Integer.toString(player));
		str = subStr(str, "[onlinePlayerName]", getPlayerName());
		
		return str;
	}
	public static String subStr(String str, String key, String sub){
		
		if(str.indexOf(key) < 0)
			return str;
		else
			return str.replace(key, sub);
	}
	private void loadConfig(){
		
		replyCommandOP = this.getConfig().getString("replyCommandOP");
		timeline = this.getConfig().getBoolean("timeline");
        startTweet = this.getConfig().getBoolean("startTweet");
        stopTweet = this.getConfig().getBoolean("stopTweet");
        loginTweet = this.getConfig().getBoolean("loginTweet");
        logoutTweet = this.getConfig().getBoolean("logoutTweet");
        regularTweet = this.getConfig().getBoolean("regularTweet");
		
		if(Locale.getDefault().getLanguage().equals("ja")){
			
			startMessage = this.getConfig().getString("startMessageJP");
			stopMessage = this.getConfig().getString("stopMessageJP");
			loginMessage = this.getConfig().getString("loginMessageJP");
			logoutMessage = this.getConfig().getString("logoutMessageJP");
			tweetMessage = this.getConfig().getString("tweetMessageJP");
			regularTweetMessage = this.getConfig().getString("regularTweetJP");
			regularTweetMessageNP = this.getConfig().getString("regularTweetNPJP");
		} else{
			
			startMessage = this.getConfig().getString("startMessageEN");
			stopMessage = this.getConfig().getString("stopMessageEN");
			loginMessage = this.getConfig().getString("loginMessageEN");
			logoutMessage = this.getConfig().getString("logoutMessageEN");
			tweetMessage = this.getConfig().getString("tweetMessageEN");
			regularTweetMessage = this.getConfig().getString("regularTweetEN");
			regularTweetMessageNP = this.getConfig().getString("regularTweetNPEN");
		}
	}
}
