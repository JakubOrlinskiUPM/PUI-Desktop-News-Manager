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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;

import java.io.IOException;

/**
 * @author Ã�ngelLucas
 *
 */
public class NewsReaderController implements Controller {

    private NewsReaderModel newsReaderModel = new NewsReaderModel();
    private User usr;
    private ConnectionManager connectionManager;

    @FXML
    private Label newsHeader;
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
    
    @FXML
    private MenuItem menuEdit;
    
    @FXML
    private MenuItem menuDelete;
    


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
        this.menuEdit.setDisable(true);
        this.menuDelete.setDisable(true);
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
    @Override
	public void setUsr(User usr) {

        this.usr = usr;
        //Reload articles
        this.getData();
        this.newsHeader.setText("News Online for User: " + usr.getIdUser());
    }

    public void onMenuLoad() {

    }

    public void onMenuLogin() {
        routeToLogin();
        this.menuEdit.setDisable(false);
        this.menuDelete.setDisable(false);
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
            this.routeToEditPage(AppScenes.EDITOR, article);
        } else {
            this.routeToEditPage(AppScenes.EDITOR, null);
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
            

            if (article != null) {
            	stage.show();
                Controller controller = loader.<NewsReaderController>getController();
                if (this.usr != null) {
                	controller.setUsr(usr);
                }
                controller.receiveArticle(article);
            } else {
            	stage.showAndWait();
            	LoginController controller = loader.<LoginController>getController();
            	User newUsr = controller.getLoggedUsr();
            	if (newUsr != null) {
            		setUsr(newUsr);
                    System.out.println("User Id in main screen: " + this.usr.getIdUser());
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void routeToEditPage (AppScenes scene, Article article) {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getFxmlFile()));
            Pane root = loader.load();
            

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle(scene.toString());
            stage.setScene(new Scene(root));
            
            if(scene == AppScenes.EDITOR) {
            	ArticleEditController controller = loader.<ArticleEditController>getController();
            	if(article != null && usr != null) {
            		controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
            		controller.setUsr(this.usr);
            		controller.setArticle(article);
            		stage.show();
            		return;
            	} else if (article == null && usr != null) {
            		controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
            		controller.setUsr(this.usr);
            		controller.setArticle(article);
            		stage.show();
            		return;
            	} else {
            		controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
            		controller.setUsr(null);
            		controller.setArticle(null);
            		stage.show();
            		return;
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
