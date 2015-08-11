package com.codename1.demos.socialnet;


import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;

public class SocialNetwork {

    private Form current;
    private Resources theme;
    
    //TODO Add REST client member

    public void init(Object context) {
        try {
            theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
        } catch(IOException e){
            e.printStackTrace();
        }
        // Pro users - uncomment this code to get crash reports sent to you automatically
        /*Display.getInstance().addEdtErrorHandler(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                evt.consume();
                Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
                Log.p("OS " + Display.getInstance().getPlatformName());
                Log.p("Error " + evt.getSource());
                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
                Log.e((Throwable)evt.getSource());
                Log.sendLog();
            }
        });*/
        
        //TODO Initialize REST client
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        showLoginForm();
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }
    
    
    public void showLoginForm() {
        //TODO Implement showLoginForm()
    }
    
    
    public void showRegisterForm() {
        
    }
    
    public void showProfile() {
       
    }
    
    public void showProfile(String username) {
        
    }
    
    
    public void showAddPostForm(Form back) {

    }
    
    public void showFeed() {
        
    }
    
    public void showPosts(String username) {
        
    }
    
    
    
    public void showFriends() {
        
    }
    
    public void showFriendRequests() {
        
    }
    
    public void showSendFriendRequestForm() {
        
    }
    
    public void showSendFriendRequestForm(String username) {
        
    }
    
    public void showAcceptFriendRequestForm(String username) {
        
    }
    
}
