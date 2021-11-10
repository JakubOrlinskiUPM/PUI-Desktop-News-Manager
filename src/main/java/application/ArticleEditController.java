/**
 * 
 */
package application;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;


import application.news.Article;
import application.news.Categories;
import application.news.Controller;
import application.news.User;
import application.utils.JsonArticle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

/**
 * @author Ã�ngelLucas
 *
 */
public class ArticleEditController implements Controller {
	private ConnectionManager connection;
	private ArticleEditModel editingArticle;
	private User usr;
	private boolean abstractSet = true;
	private boolean commuter = true;
	@FXML
	private Label userId;
	@FXML
	private Label abstractLabel;
	@FXML
	private TextField articleTitle;
	@FXML
	private TextField articleSubtitle;
	@FXML
	private ChoiceBox<Categories> articleCategory;
	@FXML
	private HTMLEditor articleEditor;
	@FXML
	private TextArea textArea;
	@FXML
	private ImageView articleImage;
	@FXML
	private Button backButton;
	@FXML
	private Button toggleTextButton;
	@FXML
	private Button toggleHtmlButton;



	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				// Scene scene = new Scene(root, 570, 420);
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					editingArticle.setImage(image);
					articleImage.setImage(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Send and article to server,
	 * Title and category must be defined and category must be different to ALL
	 * @return true if the article has been saved
	 */
	@FXML
	private boolean send(ActionEvent event) {
		String titleText = this.editingArticle.titleProperty().get(); 
		Categories category = articleCategory.getValue(); 
		if (titleText == null || category == null || 
				titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article! Title and categoy are mandatory.", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		if (usr == null) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article! You need to be logged.", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		try {
			editingArticle.abstractTextProperty().set(this.articleEditor.getHtmlText());
			editingArticle.setCategory(articleCategory.getValue());
			editingArticle.commit();
			connection.saveArticle(editingArticle.getArticleOriginal());
			// go back to main while saving the data
			editingArticle.titleProperty().unbind();
			editingArticle.subtitleProperty().unbind();
			editingArticle.abstractTextProperty().unbind();
			editingArticle.bodyTextProperty().unbind();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.close();
		} catch (ServerCommunicationError e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	public void setUsr(User usr) {
		this.usr = usr;
		if(this.usr != null) {
			this.userId.setText("User: " + this.usr.getIdUser());
		} else {
			this.userId.setText("");
		}
	}

	Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
			
		}
		return result;
	}

	/**
	 * PRE: User must be set
	 * 
	 * @param article
	 *            the article to set
	 */
	void setArticle(Article article) {
		//this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);
		this.articleCategory.getItems().addAll(Categories.values());
		if(article != null) {
			this.editingArticle = new ArticleEditModel(article);
			this.articleTitle.setDisable(true);
		} else if(article == null && this.usr == null) { // Logging not needed - Create article
			this.editingArticle = new ArticleEditModel(new Article());
		} else {
			this.editingArticle = new ArticleEditModel(usr);
		}
		// Updating UI
		this.articleTitle.setText(this.editingArticle.getTitle());
		this.articleSubtitle.setText(this.editingArticle.getSubtitle());
		this.articleCategory.setValue(this.editingArticle.getCategory());
		this.articleEditor.setHtmlText(this.editingArticle.getAbstractText());
		this.articleImage.setImage(this.editingArticle.getImage());
		
		// Binding
		this.editingArticle.titleProperty().bind(articleTitle.textProperty());
		this.editingArticle.subtitleProperty().bind(articleSubtitle.textProperty());
		// it is needed to bind abstract and body properties, but 0 idea how to do it.
	}


	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	@FXML
	private void write() {
		//TODO Consolidate all changes	
		this.editingArticle.commit();
		//Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?","");
		String fileName = name +".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(data.toString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receiveArticle(Article article) {}

	@FXML
	public void changeTextfield() {
		if (this.abstractSet) {
			this.abstractSet = false;
			if(!this.commuter) {
				this.textArea.setText(this.editingArticle.getBodyText());
				this.abstractLabel.setVisible(true);
				this.abstractLabel.setText("Body");
			} else {
				this.articleEditor.setHtmlText(this.editingArticle.getBodyText());
			}
			this.toggleTextButton.setText("Show Abstract");
		} else {
			this.abstractSet = true;
			if(!this.commuter) {
				this.textArea.setText(this.editingArticle.getAbstractText());
				this.abstractLabel.setVisible(true);
				this.abstractLabel.setText("Abstract");
			} else {
				this.articleEditor.setHtmlText(this.editingArticle.getAbstractText());
			}
			this.toggleTextButton.setText("Show Body");
		}
	}
	
	@FXML
	public void commuteTextHtml() {
		if (this.commuter) {
			this.commuter = false;
			this.articleEditor.setVisible(false);
			this.textArea.setVisible(true);
			this.textArea.setWrapText(true);
			this.textArea.setText(this.articleEditor.getHtmlText());
			this.abstractLabel.setVisible(true);
			if(abstractSet) {
				this.abstractLabel.setText("Abstract");
			} else {
				this.abstractLabel.setText("Body");
			}
			this.toggleHtmlButton.setText("Show Text");
		} else {
			this.textArea.setVisible(false);
			this.articleEditor.setVisible(true);
			this.abstractLabel.setVisible(false);
			if(abstractSet) {
				this.articleEditor.setHtmlText(this.editingArticle.getAbstractText());
			} else {
				this.articleEditor.setHtmlText(this.editingArticle.getBodyText());
			}
			this.commuter = true;
			this.toggleHtmlButton.setText("Show Html");
		}
	}

	@FXML
	public void goBackToMain(ActionEvent event) {
		editingArticle.titleProperty().unbind();
		editingArticle.subtitleProperty().unbind();
		editingArticle.abstractTextProperty().unbind();
		editingArticle.bodyTextProperty().unbind();
		editingArticle.discardChanges();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	public void setCategory() {
		articleCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categories>() {
			public void changed (ObservableValue ov, Categories value, Categories new_value) {
				articleCategory.setValue(new_value);
			}
		});
		//		articleCategory.show();
		//		articleCategory.setValue(this.editingArticle.getCategory());
		//		
		//		articleCategory.setOnAction(ev -> {
		//            this.editingArticle.setCategory(this.articleCategory.getSelectionModel().getSelectedItem());
		//        });
		//		articleCategory.hide();
	}
}
