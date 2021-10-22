/**
 *
 */
package application;


import application.news.Article;
import application.news.Categories;
import application.news.Controller;
import application.news.User;
import application.utils.JsonArticle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

import java.io.IOException;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController implements Controller {

    private NewsReaderModel newsReaderModel = new NewsReaderModel();
    private User usr;

    @FXML
    private ListView<Article> articleListView;
    private FilteredList<Article> filteredData;

    @FXML
    private ChoiceBox<Categories> categoriesChoiceBox;

    @FXML
    private WebView articleContent;

    @FXML
    private ImageView articleImageView;

    @FXML
    private Button readMore;


    public NewsReaderController() {
        //TODO
        //Uncomment next sentence to use data from server instead dummy data
        //newsReaderModel.setDummyData(false);
        //Get text Label

    }

    @FXML
    void initialize() {
        categoriesChoiceBox.setValue(Categories.ALL);

        categoriesChoiceBox.setOnAction(ev -> {
            Categories selectedCategory = this.categoriesChoiceBox.getSelectionModel().getSelectedItem();
            filteredData = new FilteredList<>(newsReaderModel.getArticles(), article -> (
                    selectedCategory == null || selectedCategory == Categories.ALL ||
                            article.getCategory() == selectedCategory.toString()));

            this.articleListView.setItems(filteredData);
            this.articleListView.getSelectionModel().select(0);
        });

        this.articleListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
            @Override
            public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newValue) {
                articleSelected(oldValue, newValue);
            }
        });
    }

    @Override
    public void receiveArticle(Article article) {

    }

    private void articleSelected(Article oldValue, Article newValue) {
        if (newValue != null) {
            articleImageView.setImage(newValue.getImageData());
            articleContent.getEngine().loadContent(newValue.getAbstractText());

            readMore.setOnAction(ev -> {
                this.routeToArticleDetails(newValue);
            });
        } else { //Nothing selected
            articleImageView.setImage(null);
            articleContent.getEngine().loadContent("");
            readMore.setOnAction(null);
        }
    }


    private void getData() {
        newsReaderModel.retrieveData();

        filteredData = new FilteredList<>(newsReaderModel.getArticles(), article -> true);
        this.articleListView.setItems(filteredData);

        this.articleListView.getSelectionModel().select(0);

        this.categoriesChoiceBox.setItems(newsReaderModel.getCategories());
    }

    /**
     * @return the usr
     */
    User getUsr() {
        return usr;
    }

    void setConnectionManager(ConnectionManager connection) {
//		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
        this.newsReaderModel.setConnectionManager(connection);
        this.getData();
    }

    /**
     * @param usr the usr to set
     */
    void setUsr(User usr) {

        this.usr = usr;
        //Reload articles
        this.getData();
        //TODO Update UI
    }

    public void onMenuLoad() {

    }

    public void onMenuLogin() {
        routeToLogin();
    }

    public void onMenuExit() {
        Stage stage = (Stage) articleContent.getScene().getWindow();
        stage.close();
    }

    public void onMenuEdit() {
        this.routeToForm(this.articleListView.getSelectionModel().getSelectedItem());
    }

    public void onMenuNew() {
        routeToForm(null);
    }

    public void onMenuDelete() {
        newsReaderModel.deleteArticle(this.articleListView.getSelectionModel().getSelectedItem());
    }

    private void routeToForm(Article article) {
        if (article != null) {
            System.out.println(article.getTitle());
            this.routeToPage(AppScenes.EDITOR, article);
        } else {
            this.routeToPage(AppScenes.EDITOR, null);
        }
    }

    private void routeToArticleDetails(Article article) {
        this.routeToPage(AppScenes.NEWS_DETAILS, article);
    }

    private void routeToLogin() {
        this.routeToPage(AppScenes.LOGIN, null);
    }

    private void routeToPage(AppScenes scene, Article article) {
        System.out.println("Routing to " + scene.toString());

        FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getFxmlFile()));
        try {
            Pane root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle(scene.toString());
            stage.setScene(new Scene(root));
            stage.show();

            if (article != null) {
                Controller controller = loader.<NewsReaderController>getController();

                controller.receiveArticle(article);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
