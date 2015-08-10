package com.codename1.demos.socialnet;


import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;

/**
 * The main lifecycle class for the social network app.
 * @author shannah
 */
public class SocialNetwork {

    private Form current;
    private Resources theme;
   
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
        
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        
        // Show the login form to start the app
        showLoginForm();
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }
    
    /**
     * Shows the login form.
     */
    public void showLoginForm() {
       
    }
    
    /**
     * Shows the Registration Form
     */
    public void showRegisterForm() {
       
    }
    
    
    /**
     * Shows the profile of the current user.
     */
    public void showProfile() {
        
    }
    
    
    /**
     * Shows the profile of a given user.
     * @param username 
     */
    public void showProfile(String username) {
        
    }
    
    /**
     * Shows form to add a post
     * @param back The form to return to on completion.
     */
    public void showAddPostForm(Form back) {
        
        
    }
    
    /**
     * Shows the news feed for the current user.
     */
    public void showFeed() {
        
    }
   
    /**
     * Shows the friends of the current user.
     */
    public void showFriends() {
       
    }
    
    /**
     * Shows the pending friend requests for the current user.
     */
    public void showFriendRequests() {
        
    }
    
    /**
     * Shows form to search for users and invite to become friends.
     */
    public void showSendFriendRequestForm() {
        
    }
    

}
