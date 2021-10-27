/**
 * 
 */
package application;


import application.news.Article;
import application.news.Controller;
import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController implements Controller {
	//TODO add attributes and methods as needed
	    private Article article;
	    private boolean abstractSet = true;
	    @FXML
	    private User usr;
	    @FXML
	    private Label articleHeader;
	    @FXML
	    private Label articleTitle;
	    @FXML
	    private Label articleSubtitle;
	    @FXML
	    private Label articleCategory;
	    @FXML
	    private WebView articleTextfield;
	    @FXML
	    private Button backButton;
	    @FXML
	    private Button toggleTextfieldButton;
	    @FXML
	    private ImageView articleImage;
	    
	    

		/**
		 * @param usr the usr to set
		 */
		public void setUsr(User usr) {
			this.usr = usr;
			if (usr == null) {
				return; //Not logged user
			}
			this.articleHeader.setText("News Online for User: " + usr.getIdUser());
			// Update UI information - especially articleHeader
		}

		/**
		 * @param article the article to set
		 */
		void setArticle(Article article) {
			this.article = article;
			receiveArticle(article);
		}
		

	@Override
	public void receiveArticle(Article article) {
		if (this.usr != null) {
			this.articleHeader.setText("News Online for User: " + usr.getIdUser());
		}
		this.article = article;
		this.articleImage.setImage(article.getImageData());
		this.articleTitle.setText("Title: " + article.getTitle());
		this.articleSubtitle.setText("Subtitle: " + article.getSubtitle());
		this.articleCategory.setText("Category: " + article.getCategory());
		this.articleTextfield.getEngine().loadContent("Abstract: " + article.getAbstractText());
	}
	
	
	public void receiveArticle(User usr, Article article) {
		if (this.usr != null) {
			this.articleHeader.setText("News Online for " + usr.getIdUser());
		}
		this.article = article;
		this.articleImage.setImage(article.getImageData());
		this.articleTitle.setText("Title: " + article.getTitle());
		this.articleSubtitle.setText("Subtitle: " + article.getSubtitle());
		this.articleCategory.setText("Category: " + article.getCategory());
		this.articleTextfield.getEngine().loadContent("Abstract: " + article.getAbstractText());
	}
	
	@FXML
	public void changeTextfield() {
		if (this.abstractSet) {
			this.abstractSet = false;
			this.articleTextfield.getEngine().loadContent("Body: " + article.getBodyText());
			this.toggleTextfieldButton.setText("Abstract");
		} else {
			this.abstractSet = true;
			this.articleTextfield.getEngine().loadContent("Abstract: " + article.getAbstractText());
			this.toggleTextfieldButton.setText("Body");
		}
	}
	
	@FXML
	public void goBackToMain(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
	}
}
