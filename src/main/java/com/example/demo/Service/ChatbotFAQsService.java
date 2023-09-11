package com.example.demo.Service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Customer;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

@Service
public class ChatbotFAQsService {

	  private TokenizerME tokenizer;

	    public ChatbotFAQsService() {
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

	    public String determineType(String complaintDescription) {
	    	String[] loginAndAccountKeywords = {
	    		    "Account login issues",
	    		    "ATM", "atm banking problem"
	    		};

	    		String[] transactionKeywords = {
	    		    "Incorrect transaction amount",
	    		    "Unauthorized transactions investigation",
	    		    "Transaction disputes requiring verification"
	    		};

	    		String[] balanceKeywords = {
	    		    "Simple balance inquiries",
	    		    "Basic online banking questions",
	    		    "Routine account information updates"
	    		};

	    		String[] cardAndATMKeywords = {
	    		    "ATM withdrawal issues",
	    		    "Card activation requests",
	    		    "Minor card issues",
	    		    "Lost or stolen card cases"
	    		};

	    		String[] personalInfoKeywords = {
	    		    "Address or contact information updates"
	    		};

	    		String[] loansAndMortgagesKeywords = {
	    		    "Non-urgent loan inquiries",
	    		    "Urgent loan or mortgage inquiries"
	    		};

	    		String[] technicalKeywords = {
	    		    "Moderate technical issues on the bank's app or website",
	    		    "Technical issues impacting multiple customers"
	    		};

	    		String[] accountMaintenanceKeywords = {
	    		    "Routine account maintenance requests"
	    		};

	    		String[] securityKeywords = {
	    		    "Minor account security concerns",
	    		    "Identity theft or major fraud cases",
	    		    "Account compromise requiring thorough examination",
	    		    "Account security breaches"
	    		};

	    		String[] accountClosureKeywords = {
	    		    "Account closure requests"
	    		};

	    		String[] billingKeywords = {
	    		    "Major billing or fee disputes"
	    		};

	    		if (containsPartialKeywords(complaintDescription, loginAndAccountKeywords)) {
	    		    return "Login And Account";
	    		} else if (containsPartialKeywords(complaintDescription, transactionKeywords)) {
	    		    return "Transaction";
	    		} else if (containsPartialKeywords(complaintDescription, balanceKeywords)) {
	    		    return "Balance";
	    		} else if (containsPartialKeywords(complaintDescription, cardAndATMKeywords)) {
	    		    return "Card and ATM";
	    		} else if (containsPartialKeywords(complaintDescription, personalInfoKeywords)) {
	    		    return "Personal Information";
	    		} else if (containsPartialKeywords(complaintDescription, loansAndMortgagesKeywords)) {
	    		    return "Loans and Mortgages";
	    		} else if (containsPartialKeywords(complaintDescription, technicalKeywords)) {
	    		    return "Technical Issues";
	    		} else if (containsPartialKeywords(complaintDescription, accountMaintenanceKeywords)) {
	    		    return "Account Maintenance";
	    		} else if (containsPartialKeywords(complaintDescription, securityKeywords)) {
	    		    return "Security";
	    		} else if (containsPartialKeywords(complaintDescription, accountClosureKeywords)) {
	    		    return "Account Closure";
	    		} else if (containsPartialKeywords(complaintDescription, billingKeywords)) {
	    		    return "Billing and Fees";
	    		} else {
	    		    return "Uncategorized";
	    		}

	    }

	    private boolean containsPartialKeywords(String text, String[] keywords) {
	    	text = text.replaceAll("[^a-zA-Z]", "");
	        String[] textWords = text.toLowerCase().split("\\s+");

	        for (String keyword : keywords) {
	            String[] keywordWords = keyword.toLowerCase().split("\\s+");
	            int wordMatchCount = 0;

	            for (String keywordWord : keywordWords) {
	                for (String textWord : textWords) {
	                    if (textWord.contains(keywordWord)) {
	                        wordMatchCount++;
	                        break; 
	                    }
	                }
	            }

	            if (wordMatchCount >= 2) {
	                return true;
	            }
	        }
	        return false;
	    } 

}