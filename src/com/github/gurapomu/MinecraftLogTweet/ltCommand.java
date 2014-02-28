package com.github.gurapomu.MinecraftLogTweet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Random;

public class ltCommand implements CommandExecutor {

	private MinecraftLogTweet plugin;
	private String locale = Locale.getDefault().getLanguage();
	
	public ltCommand(MinecraftLogTweet plugin) {
		
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		Player player = null;
		Random rn = new Random();
		
		if (sender instanceof Player) {
			
			player = (Player) sender;
		}
		
		if (cmd.getName().equalsIgnoreCase("getauthurl")){
			
			if(player == null){
			
				Authorization.authReady();
			} else{

				System.out.println("This command can only be run by console.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("authpin")){
			
			if(player == null){
				
				if(args[0] == null){
					
					return false;
				} else{
					
					Authorization.getOAuthAccessTokenG(args[0]);
					if(locale.equals("ja")){

						Authorization.tweetString("認証成功 (@" + Authorization.getScreenName() + ") " + MinecraftLogTweet.getTime());
					} else{
						
						Authorization.tweetString("Successfully authorization. (@" + Authorization.getScreenName() + ") " + MinecraftLogTweet.getTime());
					}
				}
			} else{

				System.out.println("This command can only be run by console.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("tweet")){
			
			if(args[0] == null){
					
				return false;
			} else{
				
				String s = "";
				String str = MinecraftLogTweet.tweetMessage;
				
				for(int i = 0; i < args.length; i++){
					
					if(i == 0)	s = args[i];
					else		s = s + " " + args[i];
				}
				
				str = MinecraftLogTweet.checkConstants(str);
				str = MinecraftLogTweet.subStr(str, "[playerName]", sender.getName());
				str = MinecraftLogTweet.subStr(str, "[tweetString]", s);
				
				if(Authorization.tweetString(str) == false){
					
					System.out.println("Failed to tweet");
					return false;
				}
				
				for(int i = 0; i < MinecraftLogTweet.playerName.length; i++){
					
					if(MinecraftLogTweet.playerName[i] == null)	continue;
					
					if(locale.equals("ja")){
						
						MinecraftLogTweet.playerName[i].sendMessage(sender.getName() + "がツイート。 [" + s + "]");
					} else{
						
						MinecraftLogTweet.playerName[i].sendMessage(sender.getName() + " success to tweet [" + s + "]");
					}
				}
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("shinchoku")){
			
			int frag = rn.nextInt(10);
			if(frag == 0){
				
				if(Authorization.tweetString("進捗してます！ (送信者:" + sender.getName() + ") " + MinecraftLogTweet.getTime()) == false){
					
					
					System.out.println("Faild to tweet");
				} else{
					
					for(int i = 0; i < 256; i++){
						
						if(MinecraftLogTweet.playerName[i] == null)	continue;
						
						MinecraftLogTweet.playerName[i].sendMessage(sender.getName() + "は進捗してます");
					}
				}
			} else {
				
				
				
				if(Authorization.tweetString("進捗ダメです (送信者:" + sender.getName() + ") " + MinecraftLogTweet.getTime()) == false){
					
					System.out.println("Faild to tweet");
				} else{
					
					for(int i = 0; i < 256; i++){
						
						if(MinecraftLogTweet.playerName[i] == null)	continue;
						
						MinecraftLogTweet.playerName[i].sendMessage(sender.getName() + "は進捗ダメです");
					}
				}
				
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("prime")){
			
			if(args[0] == null){
				
				return false;
			}
			int p = Integer.valueOf(args[0]).intValue();
			
			if(isPrime(p) == true){
				
				Authorization.tweetString(p + "ﾊ ｿｽｳﾀﾞ!!! " + "送信者: " + sender.getName() + " " + MinecraftLogTweet.getTime());
				
				for(int i = 0; i < 256; i++){
					
					if(MinecraftLogTweet.playerName[i] == null)	continue;
					
					MinecraftLogTweet.playerName[i].sendMessage(p + "ﾊ ｿｽｳﾀﾞ!!! " + "送信者: " + sender.getName() + " " + MinecraftLogTweet.getTime());
				}
				return true;
			} else{
				
				Authorization.tweetString(p + "ﾊ ｿｽｳｼﾞｬﾅｲ " + "送信者: " + sender.getName() + " " + MinecraftLogTweet.getTime());
				
				for(int i = 0; i < MinecraftLogTweet.playerName.length; i++){
					
					if(MinecraftLogTweet.playerName[i] == null)	continue;
					
					MinecraftLogTweet.playerName[i].sendMessage(p + "ﾊ ｿｽｳﾀﾞ!!! " + "送信者: " + sender.getName() + " " + MinecraftLogTweet.getTime());
				}
				return true;
			}
			
		} else if (cmd.getName().equalsIgnoreCase("reply")){
			
			if(args[0] == null){
				
				return false;
			}
			
			String s = " ";
			for(int i = 1; i < args.length; i++){
				
				if(i == 1)	s = args[i];
				else		s = s + " " + args[i];
			}
			Authorization.tweetString("@" + args[0] + " " + s + "  送信者: " + sender.getName());
			
			for(int i = 0; i < MinecraftLogTweet.playerName.length; i++){
				
				if(MinecraftLogTweet.playerName[i] == null)
					continue;
				
				MinecraftLogTweet.playerName[i].sendMessage(sender.getName() + "は" + "@" + args[0] + "に" + "「" + s + "」を送信しました");
			}
			
			return true;
		} else if (cmd.getName().equalsIgnoreCase("rt")){
			
			if(args[0] == null){
				
				return false;
			}
			if(Authorization.retweetStatus(Integer.valueOf(args[0]).intValue()) == false){
				
				sender.sendMessage("指定したstatusが存在していない可能性があります。");
			}
		}
		return false;
	}
	
	private boolean isPrime(int n){
		   
		int i;
		 
		if(n < 2){
	
			return false;
		}
		else if(n == 2){

			return true;
		}
		
		if(n % 2 == 0){
			
			return false;
		}
		 
		for(i = 3; i * i <= n; i += 2){
			
			if(n % i == 0){
				
				return false;
			}
		}
		
		return true;
	}
}
