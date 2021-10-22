package application;


import application.news.Article;
import application.news.Controller;
import application.news.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import serverConection.ConnectionManager;



public class LoginController implements Controller {
    public BorderPane loginWindow;

	public ImageView loginLogo;

	public TextField userIdField;
	public PasswordField passwordField;
	public Button backButton;
	public Button loginButton;
	public Label alertLabel;

	private LoginModel loginModel = new LoginModel();
	
	private User loggedUsr = null;

	public LoginController (){
	
		//Uncomment next sentence to use data from server instead dummy data
		//loginModel.setDummyData(false);
	}

	@FXML
	public void initialize() {
		Image logo = new Image("file:./src/main/resources/application/images/EITLogo.png");
		loginLogo.setImage(logo);

		loginButton.setOnAction(ev -> loginUser());
		backButton.setOnAction(ev -> goBack());
	}

	private void goBack() {
		Stage stage = (Stage) loginButton.getScene().getWindow();
		stage.close();
	}

	private void loginUser() {
		alertLabel.setVisible(false);
		User user = loginModel.validateUser(userIdField.getText(), passwordField.getText());

		if (user != null) {
			System.out.println("authorized");
		} else {
			alertLabel.setVisible(true);
		}
	}

	User getLoggedUsr() {
		return loggedUsr;
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}

	@Override
	public void receiveArticle(Article article) {

	}
}