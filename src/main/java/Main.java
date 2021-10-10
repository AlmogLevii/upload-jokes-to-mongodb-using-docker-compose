import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Main {

    public static final String MONGODB_CONNECTION_URI = "mongodb://my-database:27017";
    public static final String MONGODB_NAME = "JOKES";
    public static final String MONGODB_COLLECTION_NAME = "CHUCK_NORRIS_JOKES";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Main entry point of the application. It will execute the following steps:
     * - Read a random joke from a json file using the JOKES_BANK_URI
     * - Connect to the specified mongo db database and insert the daily joke
     * @param args
     */
    public static void main(String[] args) {

            try(MongoClient mongoClient = MongoClients.create(MONGODB_CONNECTION_URI)){
            var db = mongoClient.getDatabase(MONGODB_NAME);
            var collection = db.getCollection(MONGODB_COLLECTION_NAME);

            String chuckJoke = getChuckJoke();
            String dateAndTime = getDateAndTime();

            final Document dailyJoke = new Document(dateAndTime, chuckJoke);

            collection.insertOne(dailyJoke);

            LOGGER.info("gatling results saved successfully in " +
                    MONGODB_CONNECTION_URI + "/" + MONGODB_NAME
                    + " database.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getDateAndTime() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Use Jerusalem's time zone to format the date in
        df.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));

        return df.format(date);
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static String getChuckJoke() throws IOException {
        JSONObject json = readJsonFromUrl("https://api.chucknorris.io/jokes/random");
        String joke = json.getString("value");
        return joke;
    }

}

