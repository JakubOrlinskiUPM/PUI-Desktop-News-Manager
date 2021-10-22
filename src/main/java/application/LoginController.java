package application;


import application.news.Article;
import application.news.Controller;
import application.news.User;

import serverConection.ConnectionManager;



public class LoginController implements Controller {
//TODO Add all attribute and methods as needed 
	private LoginModel loginModel = new LoginModel();
	
	private User loggedUsr = null;

	public LoginController (){
	
		//Uncomment next sentence to use data from server instead dummy data
		//loginModel.setDummyData(false);
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