package com.kafkademo.libraryeventproducer.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkademo.libraryeventproducer.domain.Book;
import com.kafkademo.libraryeventproducer.domain.LibraryEvent;
import com.kafkademo.libraryeventproducer.domain.LibraryEventType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class LibraryEventProducerTest {

    @InjectMocks
    LibraryEventProducer libraryEventProducer;

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper;

    @Test
    void testLibraryEventProducerApproach2_onFailure() {

        Book book = Book.builder().author("Amish").id(1).name("Immortals of Meluha").build();


        LibraryEvent libraryEvent = LibraryEvent.builder().book(book).libraryEventId(1).libraryEventType(LibraryEventType.NEW).build();

        SettableListenableFuture<SendResult<Integer, String>> sendResultSettableListenableFuture = new SettableListenableFuture<>();

        sendResultSettableListenableFuture.setException(new RuntimeException("Exception in sending message"));
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(sendResultSettableListenableFuture);


        assertThrows(Throwable.class, () -> libraryEventProducer.sendLibraryEventAsynchronous_Approach2(libraryEvent).get());

    }


    @Test
    void testLibraryEventProducerApproach2_onSuccess() throws JsonProcessingException, ExecutionException, InterruptedException {

        Book book = Book.builder().author("Amish").id(1).name("Immortals of Meluha").build();


        LibraryEvent libraryEvent = LibraryEvent.builder().book(book).libraryEventId(1).libraryEventType(LibraryEventType.NEW).build();

        String message = objectMapper.writeValueAsString(libraryEvent);
        SettableListenableFuture<SendResult<Integer, String>> sendResultSettableListenableFuture = new SettableListenableFuture<>();

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>("library-event", message);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-event", 1), 1
                , 1, 300, System.currentTimeMillis(), 1, 1);
        SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetadata);
        sendResultSettableListenableFuture.set(sendResult);
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(sendResultSettableListenableFuture);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventProducer.sendLibraryEventAsynchronous_Approach2(libraryEvent);
        SendResult<Integer, String> sendResult1 = listenableFuture.get();


        assertEquals(sendResult1.getRecordMetadata().topic(), "library-event");


    }

}
