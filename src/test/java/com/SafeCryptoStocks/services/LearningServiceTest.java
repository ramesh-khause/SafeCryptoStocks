package com.SafeCryptoStocks.services;

import com.SafeCryptoStocks.model.Article;
import com.SafeCryptoStocks.model.Quiz;
import com.SafeCryptoStocks.model.Video;
import com.SafeCryptoStocks.repository.ArticleRepository;
import com.SafeCryptoStocks.repository.QuizRepository;
import com.SafeCryptoStocks.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LearningServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private LearningService learningService;

    private Article article;
    private Video video;
    private Quiz quiz;

    @BeforeEach
    public void setUp() {
        article = new Article(1L, "Test Article", "Category", "http://testlink.com");
        video = new Video(1L, "Test Video", "http://testvideo.com");
        quiz = new Quiz(1L, "Test Question", "Test Answer");
    }

    // ===================== Articles =====================

    @Test
    public void testGetArticles() {
        when(articleRepository.findAll()).thenReturn(Arrays.asList(article));

        List<Article> articles = learningService.getArticles();

        assertNotNull(articles);
        assertEquals(1, articles.size());
        assertEquals("Test Article", articles.get(0).getTitle());
    }

    @Test
    public void testCreateArticle() {
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        Article createdArticle = learningService.createArticle(article);

        assertNotNull(createdArticle);
        assertEquals("Test Article", createdArticle.getTitle());
    }

    @Test
    public void testDeleteArticle() {
        when(articleRepository.findById(anyLong())).thenReturn(Optional.of(article));
        doNothing().when(articleRepository).deleteById(anyLong());

        learningService.deleteArticle(1L);

        verify(articleRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteArticle_NotFound() {
        when(articleRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            learningService.deleteArticle(1L);
        });

        assertEquals("Article not found with id: 1", exception.getMessage());
    }

    // ===================== Videos =====================

    @Test
    public void testGetVideos() {
        when(videoRepository.findAll()).thenReturn(Arrays.asList(video));

        List<Video> videos = learningService.getVideos();

        assertNotNull(videos);
        assertEquals(1, videos.size());
        assertEquals("Test Video", videos.get(0).getTitle());
    }

    @Test
    public void testCreateVideo() {
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        Video createdVideo = learningService.createVideo(video);

        assertNotNull(createdVideo);
        assertEquals("Test Video", createdVideo.getTitle());
    }

    @Test
    public void testDeleteVideo() {
        when(videoRepository.findById(anyLong())).thenReturn(Optional.of(video));
        doNothing().when(videoRepository).deleteById(anyLong());

        learningService.deleteVideo(1L);

        verify(videoRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteVideo_NotFound() {
        when(videoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            learningService.deleteVideo(1L);
        });

        assertEquals("Video not found with id: 1", exception.getMessage());
    }

    // ===================== Quizzes =====================

    @Test
    public void testGetQuizzes() {
        when(quizRepository.findAll()).thenReturn(Arrays.asList(quiz));

        List<Quiz> quizzes = learningService.getQuizzes();

        assertNotNull(quizzes);
        assertEquals(1, quizzes.size());
        assertEquals("Test Question", quizzes.get(0).getQuestion());
    }

    @Test
    public void testCreateQuiz() {
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        Quiz createdQuiz = learningService.createQuiz(quiz);

        assertNotNull(createdQuiz);
        assertEquals("Test Question", createdQuiz.getQuestion());
    }

    @Test
    public void testDeleteQuiz() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));
        doNothing().when(quizRepository).deleteById(anyLong());

        learningService.deleteQuiz(1L);

        verify(quizRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteQuiz_NotFound() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            learningService.deleteQuiz(1L);
        });

        assertEquals("Quiz not found with id: 1", exception.getMessage());
    }
}
