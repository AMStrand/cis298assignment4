package edu.kvcc.cis298.cis298assignment4;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BeverageFetcher {
        // String for identifying the current class in error reporting:
    private static final String TAG = "BeverageFetcher";

        // Private method to get the url bytes from the url source:
    private byte[] getUrlBytes(String urlString) throws IOException {
            // Variable to save the url:
        URL url = new URL(urlString);
            // Variable to hold the connection and open the connection to the passed in url:
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            // Enter a try/catch in case of IOException:
        try {
                // Variable to hold the byte array stream to be output:
            ByteArrayOutputStream out = new ByteArrayOutputStream();
                // Variable to hold the input stream:
            InputStream in = connection.getInputStream();
                // If the response code from the connection is not HTTP_OK...
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    // Throw an IOException:
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlString);
            }
                // Variable to hold the bytes read, initialize to 0:
            int bytesRead = 0;
                // Variable to hold an array of bytes, a kb at a time:
            byte[] buffer = new byte[1024];
                // As long as the bytes read in is greater than 0...
            while ((bytesRead = in.read(buffer)) > 0) {
                    // Write the bytes to the output stream:
                out.write(buffer, 0, bytesRead);
            }
                // Close the input and output streams:
            out.close();
            in.close();
                // Return the output stream as a byte array:
            return out.toByteArray();
        } finally {
                // Disconnect from the url connection:
            connection.disconnect();
        }
    }

        // Private method to get the url string from the bytes array:
    private String getUrlString(String urlString) throws IOException {
        return new String(getUrlBytes(urlString));
    }

        // Public method to get the list of crimes from the JSON data:
    public List<Beverage> fetchBeverages() {
            // Create a local instance of the beverage list:
        List<Beverage> beverages = new ArrayList<>();
            // Enter a try/catch in case of JSON or IO Exceptions:
        try {
                // Save the url as a string:
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi")
                    .buildUpon()
                    .build().toString();
                // Get the json string from the url:
            String jsonString = getUrlString(url);
                // Save the json string as a json array:
            JSONArray jsonArray = new JSONArray(jsonString);
                // Parse the beverages, sending in the list and the json array:
            parseBeverages(beverages, jsonArray);
                // Log out success:
            Log.i(TAG, "Fetched contents of URL: " + jsonString);
        } catch (JSONException jse) {
                // Log JSON Exception:
            Log.e(TAG, "Failed to parse JSON: ", jse);
        } catch (IOException ioe) {
                // Log IO Exception:
            Log.e(TAG, "Failed to load", ioe);
        }
            // Return the list of beverages:
        return beverages;
    }

        // Private method to parse the JSON data:
    private void parseBeverages(List<Beverage> beverages, JSONArray jsonArray)
        throws IOException, JSONException {
            // Loop through the objects in the json array:
        for (int i=0; i < jsonArray.length(); i++) {
                // Get the json object at the current point based on the counter variable:
            JSONObject beverageJsonObject = jsonArray.getJSONObject(i);
                // Save the parts of the object as variables:
            String idString = beverageJsonObject.getString("id");
            String nameString = beverageJsonObject.getString("name");
            String packString = beverageJsonObject.getString("pack");
                // Parse the price as a double:
            Double price = Double.parseDouble(beverageJsonObject.getString("price"));
                // If the isActive data is 1, then the bool is set to true:
            Boolean isActive = (beverageJsonObject.getString("isActive").equals("1"));
                // Create a new beverage using the data:
            Beverage newBeverage = new Beverage(idString, nameString, packString, price, isActive);
                // Add the beverage to the list:
            beverages.add(newBeverage);
        }
    }

}
























