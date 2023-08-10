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
            "account login issues", "incorrect transaction amount",
            "simple balance inquiries", "atm withdrawal issues",
            "card activation requests", "basic online banking questions",
            "address or contact information updates", "routine account information updates"
        };
        String[] level2Keywords = {
            "unauthorized transactions investigation", "transaction disputes requiring verification",
            "minor card issues", "non-urgent loan inquiries",
            "moderate technical issues on the bank's app or website",
            "routine account maintenance requests", "minor account security concerns"
        };
        String[] level3Keywords = {
            "identity theft or major fraud cases", "complex transaction disputes requiring investigation",
            "account compromise requiring thorough examination", "lost or stolen card cases",
            "urgent loan or mortgage inquiries", "technical issues impacting multiple customers",
            "account closure requests", "account security breaches",
            "major billing or fee disputes"
        };

        if (containsPartialKeywords(complaintDescription, level3Keywords)) {
            return "Level 3";
        } else if (containsPartialKeywords(complaintDescription, level2Keywords)) {
            return "Level 2";
        } else if (containsPartialKeywords(complaintDescription, level1Keywords)) {
            return "Level 1";
        } else {
            return "Level 1"; // Default to Level 1
        }
    }

    private boolean containsPartialKeywords(String text, String[] keywords) {
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
            if (wordMatchCount >= 2) {
                return true;
            }
        }
        return false;
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