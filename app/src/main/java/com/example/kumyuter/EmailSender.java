package com.example.kumyuter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class EmailSender {

    private static final String ABSTRACT_API_KEY = "7a2f6fc7e74a4e51a9d516cf31944201";
    private static final String API_URL = "https://emailvalidation.abstractapi.com/v1/?api_key=" + ABSTRACT_API_KEY + "&email=";

    // Method to validate the email address
    public static void validateEmail(String recipientEmail) {
        OkHttpClient client = new OkHttpClient();

        // Construct the full API request URL with the recipient email
        String url = API_URL + recipientEmail;

        // Create the HTTP GET request
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log failure and print the error message
                System.err.println("Failed to validate email: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check if the request was successful
                if (!response.isSuccessful()) {
                    System.err.println("Email validation failed with code: " + response.code() + " and message: " + response.message());
                    return;
                }

                // Parse JSON response to retrieve validation results
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);

                    // Check if the email is deliverable
                    String deliverabilityStatus = jsonObject.optString("deliverability", "UNKNOWN");
                    boolean isDeliverable = "DELIVERABLE".equalsIgnoreCase(deliverabilityStatus);

                    // Log validation result based on deliverability status
                    if (isDeliverable) {
                        System.out.println("Email is valid and deliverable.");
                    } else {
                        System.out.println("Email is invalid or undeliverable.");
                    }

                    // Additional logging if needed
                    System.out.println("Full JSON Response: " + jsonObject.toString(2)); // Pretty print JSON

                } catch (Exception e) {
                    // Catch parsing errors and print the exception details
                    System.err.println("Error parsing email validation response: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    response.close();
                }
            }
        });
    }
}
