
package spacejam;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;


import twitter4j.TwitterObjectFactory;
import twitter4j.conf.ConfigurationBuilder;

public class GetChirpsFr
{
	Twitter twitter;

	String searchString = "Syrie OR Putin OR OR climat OR Russia OR Turkey OR ISIS #syriaairstrikes OR #Russia lang:fr since:2015-12-11 until:2015-12-12 -filter:retweets";
    List<Status> tweets;
    int totalTweets;

    void setup()
    {


        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);
        cb.setOAuthConsumerKey("oauth-consumer-key");
        cb.setOAuthConsumerSecret("oauth-consumer-secret");
        cb.setOAuthAccessToken("oauth-access-token");
        cb.setOAuthAccessTokenSecret("oauth-access-token-secret");

        TwitterFactory tf = new TwitterFactory(cb.build());

        twitter = tf.getInstance();
        getNewTweets();
    }

 // The below function receives the JsonArray and returns the list of embedded properties of the entities
    
 ArrayList getObjList(JSONArray jarray, String prop) {
	
	ArrayList als = new ArrayList();
	
	try {
	
    for (int i = 0; i < jarray.length(); ++i) {
      JSONObject al = jarray.getJSONObject(i);
      
		als.add(al.getString(prop));
	}
	}
	catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return als;
    }
    
   
    void getNewTweets(){
        try{
            Query query = new Query(searchString);

            query.setCount(200); 
            QueryResult result = twitter.search(query);
            tweets = result.getTweets(); // this function returns a list

            PrintWriter writer = new PrintWriter("chirps_fr_Dec11a.json", "UTF-8");
            writer.println("[");

            for (Status tweet : tweets) {
               
                String json = TwitterObjectFactory.getRawJSON(tweet);
              //  System.out.println(json);
                
                

                JSONObject obj = new JSONObject(json);

                JSONObject newObj = new JSONObject();
                
                JSONObject ent = obj.getJSONObject("entities");
                JSONObject user = obj.getJSONObject("user");
                
                String originalString = obj.getString("created_at");
                Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss +SSSS yyyy").parse(originalString);
                String newDate = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss'Z'").format(date);

                newObj.put("id",obj.getString("id"));
                newObj.put("text_fr",obj.getString("text"));
                newObj.put("created_at", newDate);
                newObj.put("screen_name",getObjList(ent.getJSONArray("user_mentions"),"screen_name"));
                newObj.put("user_name",getObjList(ent.getJSONArray("user_mentions"),"name"));
                newObj.put("tweet_lang",obj.getString("lang"));
                
                
                newObj.put("user_lang",user.getString("lang"));
                newObj.put("tweet_hashtags",getObjList(ent.getJSONArray("hashtags"),"text")); // use function to retrieve values from entitiy objects
                newObj.put("tweet_url",getObjList(ent.getJSONArray("urls"),"expanded_url"));
                newObj.put("location",user.getString("location"));
                
               // writer.println(newObj);
                writer.println(newObj +",");
            }
            writer.println("]");
            writer.close();

        }
        catch(Exception e){
            System.out.println("Exception "+e);
        }


    }

    public static void main(String[] args) {
        GetChirpsFr TwitterGetObj = new GetChirpsFr();
        TwitterGetObj.setup();
    }
	
}





