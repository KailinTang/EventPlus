package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class TicketMasterAPI {
	private static final String API_HOST = "app.ticketmaster.com";
	private static final String SEARCH_PATH = "/discovery/v2/events.json";
	private static final String DEFAULT_TERM = "";  // no restriction
	private static final String API_KEY = "EiLASLC7AGV9o81g7aFgIq3LR199bdcX";
	
    public JSONArray search(double lat, double lon, String term) {
    	// create a base url, based on API_HOST and SEARCH_PATH
    			String url = "http://" + API_HOST + SEARCH_PATH;
    			// Convert geo location to geo hash with a precision of 4 (+- 20km)
    			String geoHash = GeoHash.encodeGeohash(lat, lon, 4);
    			if (term == null) {
    				term = DEFAULT_TERM;
    			}
    			// Encode term in url since it may contain special characters
    			term = urlEncodeHelper(term);
    	         // Make your url query part like: "http://app.ticketmaster.com/discovery/v2/events.json?apikey=12345&geoPoint=abcd&keyword=music&radius=50"
    			String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=50", API_KEY, geoHash, term);
    			try {
    				// Open a HTTP connection between your Java application and TicketMaster based on url
    				HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
    				// Set requrest method to GET
    				connection.setRequestMethod("GET");

    				// Send request to TicketMaster and get response, response code could be returned directly
    				// response body is saved in InputStream of connection.
    				int responseCode = connection.getResponseCode();
    				System.out.println("\nSending 'GET' request to URL : " + url + "?" + query);
    				System.out.println("Response Code : " + responseCode);

    				// Now read response body to get events data
    				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    				String inputLine;
    				StringBuilder response = new StringBuilder();
    				while ((inputLine = in.readLine()) != null) {
    					response.append(inputLine);
    				}
    				in.close();

    				JSONObject responseJson = new JSONObject(response.toString());
    			    JSONObject embedded = (JSONObject) responseJson.get("_embedded");
    			    JSONArray events = (JSONArray) embedded.get("events");
    				return events;
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			return null;
    	}

    
	private String urlEncodeHelper(String term) {
		try {
			term = java.net.URLEncoder.encode(term, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return term;
	}
	
	private void queryAPI(double lat, double lon) {
		JSONArray events = search(lat, lon, null);
		try {
		    for (int i = 0; i < events.length(); i++) {
		        JSONObject event = events.getJSONObject(i);
		        System.out.println(event);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
	    	tmApi.queryAPI(37.38, -122.08);
	}

	
}
