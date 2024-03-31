package com.example.ls;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private RadioButton themeLight;
    private RadioButton themeDark;
    private ListView<String> songListView;
    private MediaView mediaView;
    private Label timeLabel;

    // MediaPlayer for audio playback
    private MediaPlayer mediaPlayer;
    private Media media;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lesotho Trivia Game");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

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

        ToggleGroup themeGroup = new ToggleGroup();
        themeLight = new RadioButton("Light Theme");
        themeDark = new RadioButton("Dark Theme");
        themeLight.setToggleGroup(themeGroup);
        themeDark.setToggleGroup(themeGroup);

        songListView = new ListView<>();
        songListView.getItems().addAll("nas_and_damian_marley_-_patience_(studio_version)_&_DL_Link(0).m4a",
                "nas_and_damian_marley_-_strong_will_continue_(studio_version)_&_DL_Link(0).m4a", "views.mp4");

        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedSong = newValue;
                File songFile = new File(getClass().getResource("/" + selectedSong).toExternalForm());
                media = new Media(songFile.toURI().toString());
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

        timeLabel = new Label();

        // Create a VBox for center position to hold question and answer components
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(questionLabel, imageView, option1, option2, option3, option4, submitButton, feedbackLabel);

        // Create an HBox for bottom position to hold score label and media controls
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(scoreLabel, scoreProgressBar, playButton, pauseButton, stopButton, volumeSlider, themeLight, themeDark);

        // Create a VBox for left position to hold the list of songs
        VBox leftBox = new VBox(10);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.getChildren().addAll(new Label("Available Songs"), songListView);

        // Set center, bottom and left components in the BorderPane
        root.setTop(menuBar);
        root.setCenter(centerBox);
        root.setBottom(bottomBox);
        root.setLeft(leftBox);
        root.setRight(mediaView);
        root.setTop(timeLabel);

        // Generate questions
        generateQuestions();

        // Display first question
        displayQuestion();

        // Create scene and set it in the stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Generate questions
    private void generateQuestions() {
        // Add questions with image and correct answers
        questions.add(new Question("What is the capital city of Lesotho?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Maseru", "Leribe", "Mafeteng", "Quthing", "Maseru"));
        questions.add(new Question("Which mountain range dominates the landscape of Lesotho?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Drakensberg Mountains", "Himalayas", "Andes Mountains", "Maloti Mountains", "Maloti Mountains"));
        questions.add(new Question("What is the traditional Basotho hat called?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Top Hat", "Beret", "Fedora", "Mokorotlo", "Mokorotlo"));
        questions.add(new Question("Which language is widely spoken in Lesotho?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "English", "French", "Zulu", "Sesotho", "Sesotho"));
        questions.add(new Question("What is the currency of Lesotho?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Rand", "Pula", "Euro", "Loti", "Loti"));
        // Add more questions
        questions.add(new Question("Lesotho is known as the ____ of Africa.", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Mountain Kingdom", "Desert Kingdom", "River Kingdom", "Plains Kingdom", "Mountain Kingdom"));
        questions.add(new Question("What is the name of Lesotho's highest peak?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Thabana Ntlenyana", "Mount Everest", "Kangchenjunga", "Mount Kilimanjaro", "Thabana Ntlenyana"));
        questions.add(new Question("Which of the following is a traditional Basotho dish?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Pizza", "Pap en Vleis", "Sushi", "Tacos", "Pap en Vleis"));
        questions.add(new Question("Who was the first Prime Minister of Lesotho?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Leabua Jonathan", "Nelson Mandela", "Julius Nyerere", "Haile Selassie", "Leabua Jonathan"));
        questions.add(new Question("What is the name of Lesotho's national park?", new Image(getClass().getResourceAsStream("/crm3.JPG")),
                "Kruger National Park", "Yellowstone National Park", "Maloti Drakensberg Park", "Grand Canyon National Park", "Maloti Drakensberg Park"));
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
        } else {
            // Display final score
            questionLabel.setText("Game Over! Your final score is: " + score + "/" + questions.size());
            imageView.setImage(null);
            option1.setVisible(false);
            option2.setVisible(false);
            option3.setVisible(false);
            option4.setVisible(false);
            submitButton.setVisible(false);
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

        public Question(String question, Image image, String option1, String option2, String option3, String option4, String correctAnswer) {
            this.question = question;
            this.image = image;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.correctAnswer = correctAnswer;
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

        // Check if the provided answer is correct
        public boolean isCorrectAnswer(String answer) {
            return correctAnswer.equals(answer);
        }
    }
}
