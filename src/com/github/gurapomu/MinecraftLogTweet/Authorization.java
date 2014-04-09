package com.github.gurapomu.MinecraftLogTweet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Authorization {
	
	private static Twitter twitter;
	private static AccessToken accessToken = null;
	private static RequestToken requestToken;

	public static int statusNum = -1;
	public static Status[] timelineStatus = new Status[256];
	
	public static void authReady(){
		
		if(accessToken != null){
			
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(oauthKey.consumerKey, oauthKey.consumerSecret);
			twitter.setOAuthAccessToken(accessToken);
			//System.out.println("!=null");
		} else{
			
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(oauthKey.consumerKey, oauthKey.consumerSecret);
			//System.out.println("null");
			getAuthUrl();
		}
	}
	
	static void getAuthUrl(){
		
		try {
			requestToken = twitter.getOAuthRequestToken();
			System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
			System.out.println("Please enter command (/authpin [PIN])");
		} catch (TwitterException e) {
			
			e.printStackTrace();
		}
	}
	
	static void getOAuthAccessTokenG (String pin){
		
		AccessToken accessTokenA = null;
		
		try{
			
			if(pin.length() > 0){
				
				//System.out.println(pin);
				accessTokenA = twitter.getOAuthAccessToken(requestToken, pin);
			} else{
				
				System.out.println("failed");
				accessTokenA = twitter.getOAuthAccessToken();
			}
		} catch(TwitterException te){
			
			if(401 == te.getStatusCode()){
				
				System.out.println("Unable to get the access token.");
			} else{
				
				te.printStackTrace();
			}
		}
		//System.out.println("getsuccess");
		storeAccessToken(accessTokenA);
		
	}
	
	public static boolean tweetString(String arg0){
		
		Twitter twitterT;
		accessToken = loadAccessToken();
		
		try {
			
			if(accessToken != null){
				
				twitterT = new TwitterFactory().getInstance();
				twitterT.setOAuthConsumer(oauthKey.consumerKey, oauthKey.consumerSecret);
				twitterT.setOAuthAccessToken(accessToken);
			} else{
				
				System.out.println("Please enter command(/getauthurl)");
				return false;
			}
			
			Status status = twitterT.updateStatus(arg0);
			System.out.println("Successfully updated the status to [" + status.getText() + "]");
			return true;
		} catch (TwitterException e) {
			
			e.printStackTrace();
			return false;
		}
	}
	
	public static void startStreaming(TwitterStream twStream){
		
		twStream.user();
	}
	public static void stopStreaming(TwitterStream twStream){
		
		twStream.shutdown();
	}
	
	public static AccessToken loadAccessToken(){
		
		File f = createAccessTokenFileName();
		
		ObjectInputStream is = null;
		try{
			
			is = new ObjectInputStream(new FileInputStream(f));
			AccessToken accessTokenL = (AccessToken) is.readObject();
			return accessTokenL;
		} catch(IOException e){
			
			return null;
		} catch(Exception e){
			
			return null;
		} finally{
			
			if(is != null){
				
				try{
					
					is.close();
				} catch(IOException e){
					
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void storeAccessToken(AccessToken accessTokenS){
		
		File f = createAccessTokenFileName();
		
		File d = f.getParentFile();
		if(!d.exists())	d.mkdirs();
		
		ObjectOutputStream os = null;
		try{
			
			os = new ObjectOutputStream(new FileOutputStream(f));
			os.writeObject(accessTokenS);
		} catch(IOException e){
			
			e.printStackTrace();
		} finally{
			
			if(os != null){
				
				try{
					
					os.close();
				} catch(IOException e){
					
					e.printStackTrace();
				}
			}
		}
	}
	
	static File createAccessTokenFileName(){
		
		String s = System.getProperty("user.home") + "/.twitter/client/logTweet/accessToken.dat";
		return new File(s);
	}
	
	static String getScreenName(){
		
		AccessToken accessTokenG = loadAccessToken();
		return accessTokenG.getScreenName();
	}
	
	static class MyStatusListener implements UserStreamListener {

		
		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onStallWarning(StallWarning arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onStatus(Status arg0) {
			
			if(statusNum >= 255)
				statusNum = -1;
			
			timelineStatus[++statusNum] = arg0;
			if(MinecraftLogTweet.timeline == true){
			
				for(int i = 0; i < MinecraftLogTweet.playerName.length; i++){
				
					if(MinecraftLogTweet.playerName[i] == null)	continue;
				
					MinecraftLogTweet.playerName[i].sendMessage(arg0.getUser().getScreenName() + ": " + arg0.getText() + " (" + MinecraftLogTweet.getTime() + ")");
				}
				System.out.println(arg0.getUser().getScreenName() + ": " + arg0.getText());
			}
			if(arg0.getText().indexOf(getScreenName()) != -1){
				
				String s = arg0.getText();
				String r = "@" + getScreenName() + " ";
				s = s.replace(r, "");
				if(s.startsWith("/") == true){
					
					System.out.println(MinecraftLogTweet.replyCommandOP);
					System.out.println(arg0.getUser().getScreenName());
					
					if(arg0.getUser().getScreenName().equals(MinecraftLogTweet.replyCommandOP)){
						
						s = s.replace("/", "");
						
						if(checkCommand(s) == false)
							tweetString("@" + arg0.getUser().getScreenName() + " 使用可能なコマンドではありません。コマンドのスペル、引数を確認してください。 " + MinecraftLogTweet.getTime());
					} else{
						
						tweetString("@" + arg0.getUser().getScreenName() + " replyCommandの権限がありません。 " + MinecraftLogTweet.getTime());
					}
				} else{
					
					for(int i = 0; i < MinecraftLogTweet.playerName.length; i++){
					
						if(MinecraftLogTweet.playerName[i] == null)	continue;
				
						MinecraftLogTweet.playerName[i].sendMessage(ChatColor.YELLOW + "@" + arg0.getUser().getScreenName() + "からのリプライ: " + arg0.getText());
					}

					System.out.println("@" + arg0.getUser().getScreenName() + "からのリプライ: " + arg0.getText());
				}
			}
		}

		@Override
		public void onTrackLimitationNotice(int arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onException(Exception arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onBlock(User arg0, User arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onDeletionNotice(long arg0, long arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onDirectMessage(DirectMessage arg0) {
			
		}

		@Override
		public void onFavorite(User arg0, User arg1, Status arg2) {
			
			for(int i = 0; i < MinecraftLogTweet.playerName.length; i++){
				
				if(MinecraftLogTweet.playerName[i] == null)	continue;
				
				Random rnd = new Random();
				int r = rnd.nextInt(10) + 1;
				MinecraftLogTweet.playerName[i].sendMessage(arg0.getScreenName() + "にふぁぼられて" + r + "EXPを入手した。");
				MinecraftLogTweet.playerName[i].giveExp(r);
			}
			
			System.out.println(arg0.getScreenName() + "にふぁぼられた");
		}

		@Override
		public void onFollow(User arg0, User arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onFriendList(long[] arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUnblock(User arg0, User arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUnfavorite(User arg0, User arg1, Status arg2) {
			
			for(int i = 0; i < 256; i++){
				
				if(MinecraftLogTweet.playerName[i] == null)	continue;
				
				MinecraftLogTweet.playerName[i].sendMessage(arg0.getScreenName() + "にあんふぁぼされた");
			}
			
			System.out.println(arg0.getScreenName() + "にあんふぁぼされた。");
		}

		@Override
		public void onUserListCreation(User arg0, UserList arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUserListDeletion(User arg0, UserList arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUserListUpdate(User arg0, UserList arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onUserProfileUpdate(User arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}
	}
	
	public static boolean retweetStatus(int i){
		
		Twitter twitterR;
		accessToken = loadAccessToken();
			
		if(accessToken != null){
				
			twitterR = new TwitterFactory().getInstance();
			twitterR.setOAuthConsumer(oauthKey.consumerKey, oauthKey.consumerSecret);
			twitterR.setOAuthAccessToken(accessToken);
		} else{
				
			System.out.println("Please enter command(/getauthurl)");
			return true;
		}
		
		int num = statusNum - i + 1;
		if(num < 0)
			num = 256 - num;
		
		System.out.println(num);
		if(timelineStatus[num] == null){
			
			return false;
		}
		try {
			
			twitterR.retweetStatus(timelineStatus[num].getId());
		} catch (TwitterException e) {
			
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static boolean checkCommand(String s){
		
		if(s.equals("reload")){
			
			MinecraftLogTweet.server.reload();
			return true;
		} else if(s.equals("stop")){
			
			MinecraftLogTweet.server.shutdown();
			return true;
		}
		return false;
	}
}
