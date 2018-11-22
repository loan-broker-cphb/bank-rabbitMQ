package com.rabbitbank;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.Random;

@Component
public class MessageReceiver {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageConverter messageConverter;

    @Autowired
    RabbitAdmin rabbitAdmin;

    public MessageReceiver() {
    }

    public void handleMessage(Message message) {
        MessageRequest quoteRequest = (MessageRequest) messageConverter.fromMessage(message);
        String replyTo = message.getMessageProperties().getReplyTo();

        // Calculate Loan intrest rate
        float intrest_rate = 2 + new Random().nextInt(10) + new Random().nextFloat();

        Properties prop = rabbitAdmin.getQueueProperties(replyTo);
        if (prop == null) {
            Queue queue = new Queue(replyTo, true, false, true);
            rabbitAdmin.declareQueue(queue);
            Binding binding = new Binding(replyTo, Binding.DestinationType.QUEUE, Application.exchange, replyTo, null);
            rabbitAdmin.declareBinding(binding);
        }
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        MessageResponce responce = new MessageResponce();
        responce.setInterestRate(BigDecimal.valueOf(intrest_rate));
        responce.setSsn(quoteRequest.getSsn());

        // Convert response to Message
        Message responseMessage = messageConverter.toMessage(responce, properties);
//        rabbitTemplate.setQueue(replyTo);
        rabbitTemplate.send(replyTo, responseMessage);

//        below goes in Translator
//        rabbitTemplate.convertAndSend(message, m -> {
//            m.getMessageProperties().setContentType("application/json");
//            m.getMessageProperties().setReplyTo("g4.rabbit.reply-to");
//            return m;
//        });
    }

    private class MessageRequest{

        private String ssn;

        private int creditScore;

        private double loanAmount;

        private int loanDuration;

        public MessageRequest() {
        }

        public String getSsn() {
            return ssn;
        }

        public void setSsn(String ssn) {
            this.ssn = ssn;
        }

        public int getCreditScore() {
            return creditScore;
        }

        public void setCreditScore(int creditScore) {
            this.creditScore = creditScore;
        }

        public double getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(double loanAmount) {
            this.loanAmount = loanAmount;
        }

        public int getLoanDuration() {
            return loanDuration;
        }

        public void setLoanDuration(int loanDuration) {
            this.loanDuration = loanDuration;
        }
    }

    private class MessageResponce{

        private BigDecimal interestRate;
        private String ssn;

        public MessageResponce() {
        }

        public BigDecimal getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
        }

        public String getSsn() {
            return ssn;
        }

        public void setSsn(String ssn) {
            this.ssn = ssn;
        }
    }
}
