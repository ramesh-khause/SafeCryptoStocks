package com.SafeCryptoStocks.controller;

import com.SafeCryptoStocks.model.Article;
import com.SafeCryptoStocks.model.Quiz;
import com.SafeCryptoStocks.model.Video;
import com.SafeCryptoStocks.services.LearningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LearningControllerTest {

    @Mock
    private LearningService learningService;

    @InjectMocks
    private LearningController learningController;

    private Article article;
    private Video video;
    private Quiz quiz;

    @BeforeEach
    public void setup() {
        article = new Article(1L, "Test Article", "Category", "http://testlink.com");
        video = new Video(1L, "Test Video", "http://testvideo.com");
        quiz = new Quiz(1L, "Test Question", "Test Answer");
    }

    @Test
    public void testGetArticles() {
        List<Article> articles = Arrays.asList(article);
        when(learningService.getArticles()).thenReturn(articles);

        ResponseEntity<List<Article>> response = learningController.getArticles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Article", response.getBody().get(0).getTitle());
    }

    @Test
    public void testCreateArticle() {
        when(learningService.createArticle(any(Article.class))).thenReturn(article);

        ResponseEntity<Article> response = learningController.createArticle(article);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Article", response.getBody().getTitle());
    }

    @Test
    public void testDeleteArticle() {
        doNothing().when(learningService).deleteArticle(anyLong());

        ResponseEntity<Void> response = learningController.deleteArticle(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(learningService, times(1)).deleteArticle(1L);
    }

    @Test
    public void testGetVideos() {
        List<Video> videos = Arrays.asList(video);
        when(learningService.getVideos()).thenReturn(videos);

        ResponseEntity<List<Video>> response = learningController.getVideos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Video", response.getBody().get(0).getTitle());
    }

    @Test
    public void testCreateVideo() {
        when(learningService.createVideo(any(Video.class))).thenReturn(video);

        ResponseEntity<Video> response = learningController.createVideo(video);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Video", response.getBody().getTitle());
    }

    @Test
    public void testDeleteVideo() {
        doNothing().when(learningService).deleteVideo(anyLong());

        ResponseEntity<Void> response = learningController.deleteVideo(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(learningService, times(1)).deleteVideo(1L);
    }

    @Test
    public void testGetQuizzes() {
        List<Quiz> quizzes = Arrays.asList(quiz);
        when(learningService.getQuizzes()).thenReturn(quizzes);

        ResponseEntity<List<Quiz>> response = learningController.getQuizzes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Question", response.getBody().get(0).getQuestion());
    }

    @Test
    public void testCreateQuiz() {
        when(learningService.createQuiz(any(Quiz.class))).thenReturn(quiz);

        ResponseEntity<Quiz> response = learningController.createQuiz(quiz);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Question", response.getBody().getQuestion());
    }

    @Test
    public void testDeleteQuiz() {
        doNothing().when(learningService).deleteQuiz(anyLong());

        ResponseEntity<Void> response = learningController.deleteQuiz(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(learningService, times(1)).deleteQuiz(1L);
    }
}
