package com.example.ls;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LesothoTriviaGame extends Application {

    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<Question> questions = new ArrayList<>();

    // UI components
    private Label questionLabel;
    private ImageView imageView;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private Button submitButton;
    private Label feedbackLabel;
    private Label scoreLabel;
    private ProgressBar scoreProgressBar;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Slider volumeSlider;
    private ListView<String> songListView;
    private MediaView mediaView;
    private Label timeLabel;
    private VBox videoContainer; // Container for video view
    private VBox musicContainer; // Container for music listing

    // MediaPlayer for audio playback
    private MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lesotho Trivia Game");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Add CSS stylesheet
        root.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // MenuBar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu toolsMenu = new Menu("Tools");
        Menu helpMenu = new Menu("Help");

        menuBar.getMenus().addAll(fileMenu, editMenu, toolsMenu, helpMenu);

        // Initialize UI components
        questionLabel = new Label();
        imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);

        option1 = new RadioButton();
        option2 = new RadioButton();
        option3 = new RadioButton();
        option4 = new RadioButton();
        ToggleGroup toggleGroup = new ToggleGroup();
        option1.setToggleGroup(toggleGroup);
        option2.setToggleGroup(toggleGroup);
        option3.setToggleGroup(toggleGroup);
        option4.setToggleGroup(toggleGroup);

        submitButton = new Button("Submit");
        submitButton.setOnAction(event -> submitAnswer());

        feedbackLabel = new Label();
        scoreLabel = new Label("Score: 0");
        scoreProgressBar = new ProgressBar(0);
        scoreProgressBar.setPrefWidth(200);

        playButton = new Button("Play");
        playButton.setOnAction(event -> {
            if (mediaPlayer != null) mediaPlayer.play();
        });

        pauseButton = new Button("Pause");
        pauseButton.setOnAction(event -> {
            if (mediaPlayer != null) mediaPlayer.pause();
        });

        stopButton = new Button("Stop");
        stopButton.setOnAction(event -> {
            if (mediaPlayer != null) mediaPlayer.stop();
        });

        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(newValue.doubleValue() / 100);
        });

        songListView = new ListView<>();
        songListView.getItems().addAll(
                "LESOTHO by drone __ Cinematic video.mp4"
        );

        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedSong = "/" + newValue; // Prepend '/' to indicate resource directory
                Media media = new Media(getClass().getResource(selectedSong).toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> {
                    playButton.setDisable(false);
                    pauseButton.setDisable(false);
                    stopButton.setDisable(false);
                    volumeSlider.setDisable(false);
                });
                mediaPlayer.currentTimeProperty().addListener((observable1, oldValue1, newValue1) -> updateMediaTime());
                mediaView.setMediaPlayer(mediaPlayer);
            }
        });

        mediaView = new MediaView();
        mediaView.setFitWidth(400); // Adjust width of MediaView
        mediaView.setFitHeight(250); // Adjust height of MediaView

        // Video container setup
        videoContainer = new VBox();
        videoContainer.setAlignment(Pos.CENTER);
        videoContainer.getChildren().add(mediaView); // Add mediaView to videoContainer
        VBox.setVgrow(videoContainer, Priority.ALWAYS); // Allow the videoContainer to grow vertically

        timeLabel = new Label();

        // Music container setup
        musicContainer = new VBox();
        musicContainer.setAlignment(Pos.CENTER);
        musicContainer.getChildren().add(songListView); // Add songListView to musicContainer
        VBox.setVgrow(musicContainer, Priority.ALWAYS); // Allow the musicContainer to grow vertically

        // Create a VBox for center position to hold question and answer components
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getStyleClass().add("center-box");
        centerBox.getChildren().addAll(questionLabel, imageView, option1, option2, option3, option4, submitButton, feedbackLabel);

        // Create an HBox for bottom position to hold score label and media controls
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getStyleClass().add("bottom-box");
        bottomBox.getChildren().addAll(scoreLabel, scoreProgressBar, playButton, pauseButton, stopButton, volumeSlider);

        // Set center, bottom, and left components in the BorderPane
        root.setTop(menuBar);
        root.setCenter(centerBox);
        root.setBottom(bottomBox);
        root.setLeft(musicContainer);
        root.setRight(videoContainer); // Add video container
        root.setTop(timeLabel);

        // Generate questions
        generateQuestions();

        // Display first question
        displayQuestion();

        // Create scene and set it in the stage
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Generate questions
    private void generateQuestions() {
        questions.add(new Question("What is the capital city of Lesotho?", new Image(getClass().getResourceAsStream("/capital.jpg")),
                "Maseru", "Leribe", "Mafeteng", "Quthing", "Maseru", "/videos/capital_city.mp4"));
        questions.add(new Question("Which mountain range dominates the landscape of Lesotho?", new Image(getClass().getResourceAsStream("/moutain.jpg")),
                "Drakensberg Mountains", "Himalayas", "Andes Mountains", "Maloti Mountains", "Maloti Mountains", "/videos/mountain_range.mp4"));
        questions.add(new Question("What is the traditional Basotho hat called?", new Image(getClass().getResourceAsStream("/mokorotlo.jpg")),
                "Top Hat", "Beret", "Fedora", "Mokorotlo", "Mokorotlo", "/videos/basotho_hat.mp4"));
        questions.add(new Question("What is the official language of Lesotho?", new Image(getClass().getResourceAsStream("/languages.jpg")),
                "French", "English", "Spanish", "Portuguese", "English", null));
        questions.add(new Question("What is the currency of Lesotho?", new Image(getClass().getResourceAsStream("/loti.jpg")),
                "Loti", "Rand", "Dollar", "Pound", "Loti", null));
    }

    // Display current question
    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionLabel.setText(currentQuestion.getQuestion());
            imageView.setImage(currentQuestion.getImage());
            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            option4.setText(currentQuestion.getOption4());
            feedbackLabel.setText("");

            // Display video if available
            if (currentQuestion.getVideoPath() != null) {
                URL videoResource = getClass().getResource(currentQuestion.getVideoPath());
                if (videoResource != null) {
                    Media videoMedia = new Media(videoResource.toString());
                    mediaPlayer = new MediaPlayer(videoMedia);
                    mediaPlayer.setAutoPlay(true); // Autoplay video
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaView.setVisible(true);
                    imageView.setVisible(false);
                } else {
                    System.err.println("Video resource not found: " + currentQuestion.getVideoPath());
                }
            } else {
                mediaView.setVisible(false);
                imageView.setVisible(true);
                // Set imageView with the image corresponding to the question
                imageView.setImage(currentQuestion.getImage());
            }
        } else {
            mediaPlayer.stop();
            showScoreMessage();
        }
    }

    // Submit answer and check correctness
    private void submitAnswer() {
        RadioButton selectedRadioButton = (RadioButton) option1.getToggleGroup().getSelectedToggle();
        if (selectedRadioButton != null) {
            String selectedAnswer = selectedRadioButton.getText();
            Question currentQuestion = questions.get(currentQuestionIndex);
            if (currentQuestion.isCorrectAnswer(selectedAnswer)) {
                score++;
                feedbackLabel.setText("Correct!");
            } else {
                feedbackLabel.setText("Incorrect! The correct answer is: " + currentQuestion.getCorrectAnswer());
            }
            scoreLabel.setText("Score: " + score);
            scoreProgressBar.setProgress((double) score / questions.size());
            currentQuestionIndex++;
            displayQuestion();
        }
    }

    // Display final score
    private void showScoreMessage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Your final score is: " + score + "/" + questions.size());
        alert.setContentText("Do you want to play again?");
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            // Reset game
            currentQuestionIndex = 0;
            score = 0;
            scoreLabel.setText("Score: 0");
            scoreProgressBar.setProgress(0);
            displayQuestion();
        } else {
            // Exit game
            System.exit(0);
        }
    }

    // Update time label for media player
    private void updateMediaTime() {
        if (mediaPlayer != null) {
            double duration = mediaPlayer.getTotalDuration().toSeconds();
            double currentTime = mediaPlayer.getCurrentTime().toSeconds();
            int minutes = (int) (currentTime / 60);
            int seconds = (int) (currentTime % 60);
            int durationMinutes = (int) (duration / 60);
            int durationSeconds = (int) (duration % 60);
            timeLabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, durationMinutes, durationSeconds));
        }
    }

    // Class representing a trivia question
    class Question {
        private String question;
        private Image image;
        private String option1;
        private String option2;
        private String option3;
        private String option4;
        private String correctAnswer;
        private String videoPath; // Path to the video clip

        public Question(String question, Image image, String option1, String option2, String option3, String option4, String correctAnswer, String videoPath) {
            this.question = question;
            this.image = image;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.correctAnswer = correctAnswer;
            this.videoPath = videoPath;
        }

        // Getters for question components
        public String getQuestion() {
            return question;
        }

        public Image getImage() {
            return image;
        }

        public String getOption1() {
            return option1;
        }

        public String getOption2() {
            return option2;
        }

        public String getOption3() {
            return option3;
        }

        public String getOption4() {
            return option4;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public String getVideoPath() {
            return videoPath;
        }

        // Check if the selected answer is correct
        public boolean isCorrectAnswer(String selectedAnswer) {
            return correctAnswer.equals(selectedAnswer);
        }
    }
}
