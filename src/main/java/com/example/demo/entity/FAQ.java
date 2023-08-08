package com.example.demo.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FAQ")
public class FAQ {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "faqId")
    private Long faqId;

    @NotNull(message = "FAQ Type is mandatory")
    private String faqType;

    @NotNull(message = "FAQ Question is mandatory")
    private String question;

    @NotNull(message = "FAQ Answer is mandatory")
    private String answer;

    public FAQ() {
    }

    public FAQ(Long faqId, String faqType, String question, String answer) {
        this.faqId = faqId;
        this.faqType = faqType;
        this.question = question;
        this.answer = answer;
    }

    public Long getFaqId() {
        return faqId;
    }

    public void setFaqId(Long faqId) {
        this.faqId = faqId;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFaqType() {
        return faqType;
    }

    public void setFaqType(String faqType) {
        this.faqType = faqType;
    }
}
