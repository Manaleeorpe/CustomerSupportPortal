package com.example.demo.Service;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ChatbotService {

    private TokenizerME tokenizer;

    public ChatbotService() {
        try {
            // Load tokenizer model from the Maven dependency
            InputStream tokenizerModelStream = getClass().getResourceAsStream("/en-token.bin");
            if (tokenizerModelStream != null) {
                TokenizerModel tokenizerModel = new TokenizerModel(tokenizerModelStream);
                tokenizer = new TokenizerME(tokenizerModel);
            } else {
                throw new RuntimeException("Tokenizer model could not be loaded.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String processComplaint(String complaintDescription) {
        String severityLevel = determineSeverity(complaintDescription);
        String responseText = generateResponse(severityLevel);
        return responseText;
    }

    public String determineSeverity(String complaintDescription) {
        String[] level1Keywords = {
            "account login issues", 
            "simple balance inquiries", "atm withdrawal issues",
            "card activation requests", "basic online banking questions",
            "address or contact information updates", "routine account information updates"
        };
        String[] level2Keywords = {
            "unauthorized transactions investigation", "transaction disputes requiring verification",
            "minor card issues", "non-urgent loan inquiries",
            "moderate technical issues on the bank's app or website",
            "routine account maintenance requests", "minor account security concerns","account security breaches",
            "complex transaction disputes requiring investigation"
        };
        String[] level3Keywords = {
            "identity theft or major fraud cases", "complex transaction disputes requiring investigation",
            "account compromise requiring thorough examination", "lost or stolen card cases",
            "urgent loan or mortgage inquiries", "technical issues impacting multiple customers",
            "account closure requests",
            "major billing or fee disputes","incorrect transaction amount"
        };
        
        int level1 = containsPartialKeywords(complaintDescription, level1Keywords);
        int level2 = containsPartialKeywords(complaintDescription, level2Keywords);
        int level3 = containsPartialKeywords(complaintDescription, level3Keywords);

        if (level1 > level2 && level1 > level3) {
            return "Level 1";
        } else if (level2 > level3 && level2 > level1) {
            return "Level 2";
        } else if (level3 > level1 && level3 > level2) {
            return "Level 3";
        } else {
            return "Level 2"; // Default to Level 1
        }
    }

    private int containsPartialKeywords(String text, String[] keywords) {
    	text = text.replaceAll("[^a-zA-Z]", "");
        String[] textWords = text.toLowerCase().split("\\s+");
       
        
        for (String keyword : keywords) {
            String[] keywordWords = keyword.toLowerCase().split("\\s+");
            int wordMatchCount = 0;

            for (String keywordWord : keywordWords) {
                for (String textWord : textWords) {
                    if (textWord.contains(keywordWord)) {
                        wordMatchCount++;
                        break; // Move to the next keyword word
                    }
                }
            }

            // Adjust the threshold as needed (e.g., 2 or 3) based on your requirement
            return wordMatchCount;
        }
        return 0;
    }


    private String generateResponse(String severityLevel) {
        switch (severityLevel) {
            case "Level 1":
                return "Thank you for reaching out. We will address your concern as quickly as possible.";
            case "Level 2":
                return "Thank you for your patience. Your issue will be resolved within 24 hours.";
            case "Level 3":
                return "We understand the seriousness of your concern. Our team is actively working on it and will resolve it within 48 hours.";
            default:
                return "Thank you for contacting us. Your concern has been noted.";
        }
    }
}