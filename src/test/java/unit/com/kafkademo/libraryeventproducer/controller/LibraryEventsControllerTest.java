package com.kafkademo.libraryeventproducer.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkademo.libraryeventproducer.domain.Book;
import com.kafkademo.libraryeventproducer.domain.LibraryEvent;
import com.kafkademo.libraryeventproducer.domain.LibraryEventType;
import com.kafkademo.libraryeventproducer.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventsControllerTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Test
    void postLibraryEvent() throws Exception {

        Book book = Book.builder().author("Amish").id(1).name("Immortals of Meluha").build();


        LibraryEvent libraryEvent = LibraryEvent.builder().book(book).libraryEventId(1).libraryEventType(LibraryEventType.NEW).build();

        when(libraryEventProducer.sendLibraryEventAsynchronous_Approach2(libraryEvent)).thenReturn(null);

        String body = objectMapper.writeValueAsString(libraryEvent);
        mockMvc.perform(post("/v1/library-event")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

    }


    @Test
    void putLibraryEvent() throws Exception {

        Book book = Book.builder().author("Amish").id(1).name("Immortals of Meluha").build();


        LibraryEvent libraryEvent = LibraryEvent.builder().book(book).libraryEventId(1).libraryEventType(LibraryEventType.UPDATE).build();

        when(libraryEventProducer.sendLibraryEventAsynchronous_Approach2(libraryEvent)).thenReturn(null);

        String body = objectMapper.writeValueAsString(libraryEvent);
        mockMvc.perform(put("/v1/library-event")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

    }

    @Test
    void putLibraryEvent_throwsException() throws Exception {

        Book book = Book.builder().author("Amish").id(1).name("Immortals of Meluha").build();


        LibraryEvent libraryEvent = LibraryEvent.builder().book(book).libraryEventId(null).libraryEventType(LibraryEventType.UPDATE).build();

        when(libraryEventProducer.sendLibraryEventAsynchronous_Approach2(libraryEvent)).thenReturn(null);

        String body = objectMapper.writeValueAsString(libraryEvent);
        mockMvc.perform(put("/v1/library-event")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please pass a valid library event id"));

    }

    @Test
    void postLibraryEvent_4xxErrors() throws Exception {

        Book book = Book.builder().author(null).id(1).name("Immortals of Meluha").build();


        LibraryEvent libraryEvent = LibraryEvent.builder().book(book).libraryEventId(1).libraryEventType(LibraryEventType.NEW).build();

        when(libraryEventProducer.sendLibraryEventAsynchronous_Approach2(libraryEvent)).thenReturn(null);

        String expectedErrorMessage = "book.author - must not be blank";
        String body = objectMapper.writeValueAsString(libraryEvent);
        mockMvc
                .perform(post("/v1/library-event").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedErrorMessage));
    }
}