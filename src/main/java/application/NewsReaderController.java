/**
 *
 */
package application;


import application.news.Article;
import application.news.Categories;
import application.news.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import serverConection.ConnectionManager;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

    private NewsReaderModel newsReaderModel = new NewsReaderModel();
    private User usr;

    @FXML
    private ListView<Article> articleListView;
    private FilteredList<Article> filteredData;

    @FXML
    private ChoiceBox<Categories> categoriesChoiceBox;

    @FXML
    private Label articleContent;

    @FXML
    private ImageView articleImageView;



    //TODO add attributes and methods as needed

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
        });

        this.articleListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
            @Override
            public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newValue) {
                if (newValue != null) {
                    articleImageView.setImage(newValue.getImageData());
                    articleContent.setText(newValue.getAbstractText());
                } else { //Nothing selected
                    articleImageView.setImage(null);
                    articleContent.setText("");
                }
            }

        });
    }


    private void getData() {
        Image img3 = new Image("file:./src/main/resources/application/images/abstract1.jpg");

        System.out.println(img3.isError());
        System.out.println("Working Directory = " + System.getProperty("user.dir"));


        //TODO retrieve data and update UI
        //The method newsReaderModel.retrieveData() can be used to retrieve data
        newsReaderModel.retrieveData();

        filteredData = new FilteredList<>(newsReaderModel.getArticles(), article -> true);

        this.articleListView.setItems(filteredData);

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


}
