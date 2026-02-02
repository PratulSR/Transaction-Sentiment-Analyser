package com.hackathon.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.model.Transaction;
import com.hackathon.model.TransactionMega;
import com.hackathon.model.TransactionNegative;
import com.hackathon.processor.TransactionProcessor;
import com.hackathon.repository.TransactionNegativeRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class TransactionListener {

    private TransactionNegativeRepo transactionNegativeRepo;
    private ObjectMapper objectMapper;

    @JmsListener(destination = "transactionListener")
    public void processMessage(String content) throws Exception{
        log.info("Processing " + content);

        var transactionMega = objectMapper.readValue(content, TransactionMega.class);
        var transactionNegative = objectMapper.readValue(content, TransactionNegative.class);
        var transaction = objectMapper.readValue(content, Transaction.class);

        String[] results = TransactionProcessor.filterDescription(transactionMega.getDescription());
        transactionMega.setDescriptionCensored(results[0]);
        transactionMega.setSentimentScore(results[1]);

        transaction.setDescription(results[0]);

        transactionNegative.setSentimentScore(transactionMega.getSentimentScore());
        transactionNegative.setDescription(results[0]);

        if (Double.parseDouble(transactionNegative.getSentimentScore()) < 0){
            transactionNegativeRepo.save(transactionNegative);
//            thread(new TransactionProducer(transaction), false);
        }
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class TransactionProducer implements Runnable {

        private Transaction transactionToSend;
//        private JmsTemplate jmsTemplate;
        private ObjectMapper mapper;

        public TransactionProducer(Transaction transactionToSend) {
            this.transactionToSend = transactionToSend;
        }

        public void run() {
            try {
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");

                // Create a Connection
                Connection connection = (Connection) connectionFactory.createConnection();
                connection.start();

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("transactionListener");

                // Create a MessageProducer from the Session to the Topic or Queue
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                // Create a messages
//                String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
                String text = mapper.writeValueAsString(this.transactionToSend);
                TextMessage message = session.createTextMessage(text);
//                ObjectMessage objectMessage = session.createObjectMessage();
//                objectMessage.setObject(transactionToSend);

                // Tell the producer to send the message
                System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);

                // Clean up
                session.close();
                connection.close();
            }
            catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
    }

}
