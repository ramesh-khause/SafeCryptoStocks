package com.SafeCryptoStocks.services;

import com.SafeCryptoStocks.model.Article;
import com.SafeCryptoStocks.model.Quiz;
import com.SafeCryptoStocks.model.Video;
import com.SafeCryptoStocks.repository.ArticleRepository;
import com.SafeCryptoStocks.repository.QuizRepository;
import com.SafeCryptoStocks.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LearningService {

    private final ArticleRepository articleRepository;
    private final VideoRepository videoRepository;
    private final QuizRepository quizRepository;

    @Autowired
    public LearningService(ArticleRepository articleRepository, VideoRepository videoRepository, QuizRepository quizRepository) {
        this.articleRepository = articleRepository;
        this.videoRepository = videoRepository;
        this.quizRepository = quizRepository;
    }

    // ===================== Articles =====================

    // Get all articles
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    // Create a new article
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    // Delete an article by ID
    public void deleteArticle(Long id) {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isPresent()) {
            articleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Article not found with id: " + id);
        }
    }

    // ===================== Videos =====================

    // Get all videos
    public List<Video> getVideos() {
        return videoRepository.findAll();
    }

    // Create a new video
    public Video createVideo(Video video) {
        return videoRepository.save(video);
    }

    // Delete a video by ID
    public void deleteVideo(Long id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isPresent()) {
            videoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Video not found with id: " + id);
        }
    }

    // ===================== Quizzes =====================

    // Get all quizzes
    public List<Quiz> getQuizzes() {
        return quizRepository.findAll();
    }

    // Create a new quiz
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    // Delete a quiz by ID
    public void deleteQuiz(Long id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        if (quiz.isPresent()) {
            quizRepository.deleteById(id);
        } else {
            throw new RuntimeException("Quiz not found with id: " + id);
        }
    }
}
