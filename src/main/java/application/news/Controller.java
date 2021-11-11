package application.news;

import serverConection.ConnectionManager;

public interface Controller {
    void receiveArticle(Article article);
    void setUsr(User usr);
    void setConnectionManager(ConnectionManager connectionManager);
}
