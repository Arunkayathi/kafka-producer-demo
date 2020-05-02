package com.kafkademo.libraryeventproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafkademo.libraryeventproducer.domain.LibraryEvent;
import com.kafkademo.libraryeventproducer.domain.LibraryEventType;
import com.kafkademo.libraryeventproducer.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/library-event")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        //invoke kafka producer

        //Asynchronous logic-approach1
        // libraryEventProducer.sendLibraryEventAsynchronous_Approach1(libraryEvent);

        //Asynchronous logic-approach2
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEventAsynchronous_Approach2(libraryEvent);

        //Synchronous logic
        //SendResult<Integer, String> sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        //log.info("Message sent successfully {}", sendResult.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
