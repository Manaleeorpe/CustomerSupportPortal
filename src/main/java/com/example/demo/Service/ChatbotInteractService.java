package com.example.demo.Service;

import org.json.JSONArray;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class ChatbotInteractService {

    private Map<String, String> responses;
    private String[] genericResponses = {
            "I'm not sure I understand. Can you please rephrase?",
            "I'm learning new things every day. Could you provide more details?"
    };

    public ChatbotInteractService() {
        responses = new HashMap<>();
        responses.put("greet", "Hi! How can I help you today?");
        responses.put("bye", "Goodbye! Have a nice day!");
        responses.put("thanks", "You're welcome!");
        responses.put("working_hours", "Our working hours are from 9 AM to 5 PM, Monday to Friday.");
        responses.put("services_offered", "We offer a range of banking services, including account management, loans, and online banking.");
        responses.put("open_new_account", "To open a new bank account, visit the nearest bank.");
        responses.put("post_complaint", "Are you logged in as a customer?");
        responses.put("post_complaint_yes", "Now, go to the Complaints section using the following link - <a href=\"http://localhost:3000/AddComplaints\">www.customerportal.addcomplaint.com</a>.");
        responses.put("post_complaint_no", "Then log in as a customer first, as this procedure requires u to be authenticated ,i will be able to assist u further after you logged in.");
        responses.put("complaint", "After we receive a complaint, a mail is sent to you regarding the same and the issue is resolved within 2 days. You will receive a mail after your issue has been resolved.");
        responses.put("view_complaint", "To check the status of your complaint(s), you have to login as a customer first and then click on <a href=\"http://localhost:3000/ViewComplaints\">www.customerportal.viewcomplaints.com</a>.");
        responses.put("update_profile", "For updating your profile details, click on the following link - <a href=\"http://localhost:3000/updateUser\">www.customerportal.updateprofile.com</a>.");
    }

    public String generateResponse(String userInput) {
        String extractedIntent = getRasaIntent(userInput);

        if (responses.containsKey(extractedIntent)) {
            return responses.get(extractedIntent);
        }

        return getRandomGenericResponse();
    }

    private String getRandomGenericResponse() {
        Random random = new Random();
        int randomIndex = random.nextInt(genericResponses.length);
        return genericResponses[randomIndex];
    }

    public String getRasaIntent(String userInput) {
        String rasaUrl = "http://localhost:5005/model/parse";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(userInput, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(rasaUrl, entity, String.class);
        String responseJson = responseEntity.getBody();

        // Parse the Rasa response JSON to extract the intent
        JSONObject jsonObject = new JSONObject(responseJson);
        JSONArray intentArray = jsonObject.getJSONArray("intent_ranking");
        if (intentArray.length() > 0) {
            JSONObject topIntentObject = intentArray.getJSONObject(0);
            String extractedIntent = topIntentObject.getString("name");
            return extractedIntent;
        } else {
            // Handle the case when intent_ranking array is empty
            return "fallback_intent"; // or some default value
        }

    }
}