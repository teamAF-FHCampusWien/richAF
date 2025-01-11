package at.ac.fhcampuswien.richAF.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DashboardDesign extends Application {

    @Override
    public void start(Stage stage) {
        // Main container
        VBox mainContainer = new VBox();
        mainContainer.setPadding(new Insets(10));
        mainContainer.setSpacing(10);

        // Top section with 3 info cards
        HBox topInfo = new HBox(10);
        topInfo.setPadding(new Insets(10));

        // Add 3 labels
        Label feedsLabel = createTopCard("3 Feeds\nadded");
        Label articlesLabel = createTopCard("42 Articles\ncrawled");
        Label tslaLabel = createTopCard("TSLA\nlikely to gain");
        Label nvdaLabel = createTopCard("NVDA\nlikely to loose");

        topInfo.getChildren().addAll(feedsLabel, articlesLabel, tslaLabel, nvdaLabel);

        // Bottom section for the articles
        HBox articlesContainer = new HBox(10);
        articlesContainer.setPadding(new Insets(10));

        // Create 3 example article cards
        VBox card1 = createArticleCard("Why Nvidia Stock (NVDA) Declined Today",
                "NVIDIA’s stock dipped after a senior executive met with China’s Vice Commerce Minister amid looming U.S. semiconductor restrictions. Investors are concerned about geopolitical risks, high market expectations, and competition with Apple for the top market value. Despite strong chip demand, uncertainties weigh on the stock.");
        VBox card2 = createArticleCard("Why Nvidia Stock (NVDA) Declined Today",
                "NVIDIA’s stock dipped after a senior executive met with China’s Vice Commerce Minister amid looming U.S. semiconductor restrictions. Investors are concerned about geopolitical risks, high market expectations, and competition with Apple for the top market value. Despite strong chip demand, uncertainties weigh on the stock.");
        VBox card3 = createArticleCard("Why Nvidia Stock (NVDA) Declined Today",
                "NVIDIA’s stock dipped after a senior executive met with China’s Vice Commerce Minister amid looming U.S. semiconductor restrictions. Investors are concerned about geopolitical risks, high market expectations, and competition with Apple for the top market value. Despite strong chip demand, uncertainties weigh on the stock.");

        articlesContainer.getChildren().addAll(card1, card2, card3);

        // Add everything to main container
        mainContainer.getChildren().addAll(topInfo, articlesContainer);

        // Set up scene
        Scene scene = new Scene(mainContainer, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Dashboard Design");
        stage.show();
    }

    private Label createTopCard(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-alignment: center; -fx-background-color: #808080; -fx-padding: 10px; -fx-background-radius: 5px;");
        label.setMinWidth(200);
        label.setMinHeight(50);
        return label;
    }

    private VBox createArticleCard(String title, String summary) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #d3d3d3; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        card.setMinWidth(300);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Summary
        Label summaryLabel = new Label(summary);
        summaryLabel.setWrapText(true);
        summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        // Buttons
        HBox buttons = new HBox(5);
        Button matchButton = new Button("✔ match");
        matchButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 5px;");
        Button knownStockButton = new Button("✔ known stock");
        knownStockButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 5px;");
        Button likelyToLoseButton = new Button("❌ likely to lose");
        likelyToLoseButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 5px;");

        buttons.getChildren().addAll(matchButton, knownStockButton, likelyToLoseButton);

        // RSS Source
        Label rssLabel = new Label("📰 found in \"S&P 500 news\"");
        rssLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");

        // Add everything to the card
        card.getChildren().addAll(titleLabel, summaryLabel, buttons, rssLabel);
        return card;
    }

    public static void main(String[] args) {
        launch();
    }
}
