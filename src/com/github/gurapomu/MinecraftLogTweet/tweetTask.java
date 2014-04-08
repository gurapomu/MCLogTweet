package com.github.gurapomu.MinecraftLogTweet;

import java.util.TimerTask;

public class tweetTask extends TimerTask {
	
	@Override
	public void run(){
		
		if(Authorization.loadAccessToken() == null)	return;

        if(MinecraftLogTweet.regularTweet == true){

            if(MinecraftLogTweet.player == 0){

                String str = MinecraftLogTweet.regularTweetMessageNP;
                str = MinecraftLogTweet.checkConstants(str);

                Authorization.tweetString(str);
            } else{

                String str = MinecraftLogTweet.regularTweetMessage;
                str = MinecraftLogTweet.checkConstants(str);
                Authorization.tweetString(str);
            }
        }
	}
}
