package com.SafeCryptoStocks.controller;

import com.SafeCryptoStocks.model.Article;
import com.SafeCryptoStocks.model.Quiz;
import com.SafeCryptoStocks.model.Video;
import com.SafeCryptoStocks.services.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LearningController {

    private final LearningService learningService;

    @Autowired
    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    // GET: Fetch all articles
    @GetMapping("/articles")
    public ResponseEntity<List<Article>> getArticles() {
        List<Article> articles = learningService.getArticles();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    // POST: Create a new article
    @PostMapping("/articles")
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Article newArticle = learningService.createArticle(article);
        return new ResponseEntity<>(newArticle, HttpStatus.CREATED);
    }

    // DELETE: Delete an article by ID
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        learningService.deleteArticle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Status 204
    }

    // GET: Fetch all videos
    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getVideos() {
        List<Video> videos = learningService.getVideos();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    // POST: Create a new video
    @PostMapping("/videos")
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        Video newVideo = learningService.createVideo(video);
        return new ResponseEntity<>(newVideo, HttpStatus.CREATED);
    }

    // DELETE: Delete a video by ID
    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        learningService.deleteVideo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Status 204
    }

    // GET: Fetch all quizzes
    @GetMapping("/quiz")
    public ResponseEntity<List<Quiz>> getQuizzes() {
        List<Quiz> quizzes = learningService.getQuizzes();
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // POST: Create a new quiz
    @PostMapping("/quiz")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        Quiz newQuiz = learningService.createQuiz(quiz);
        return new ResponseEntity<>(newQuiz, HttpStatus.CREATED);
    }

    // DELETE: Delete a quiz by ID
    @DeleteMapping("/quiz/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        learningService.deleteQuiz(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Status 204
    }
}
