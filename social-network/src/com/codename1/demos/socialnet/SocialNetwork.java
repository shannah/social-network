package com.codename1.demos.socialnet;


import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import java.util.Map;

/**
 * The main lifecycle class for the social network app.
 * @author shannah
 */
public class SocialNetwork {

    private Form current;
    private Resources theme;
    
    // REST client to interact with social network server
    private SocialClient client;
    
    //TODO Add image placeholders for common images types
   
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
        
        //TODO Enable hamburger menu
        
        // Initialize the REST client
        client = new SocialClient();
        
        //TODO Initialize the image placeholders
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
       Form f = new Form("Login");
        
        Container padding = new Container();
        Style s = new Style();
        s.setPadding(0, 15, 5, 5);
        s.setPaddingUnit(new byte[]{
            Style.UNIT_TYPE_DIPS, 
            Style.UNIT_TYPE_DIPS,
            Style.UNIT_TYPE_DIPS,
            Style.UNIT_TYPE_DIPS
        });
        
        
        padding.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        padding.addComponent(new Label("Username"));
        TextField usernameField = new TextField();
        TextField passwordField = new TextField();
        passwordField.setConstraint(TextField.PASSWORD);
        padding.addComponent(usernameField);
        padding.addComponent(new Label("Password"));
        padding.addComponent(passwordField);
        
        Button loginButton = new Button("Login");
        loginButton.addActionListener((e)->{
            try {
                client.login(usernameField.getText(), passwordField.getText());
                
            } catch (Exception ex) {
                Dialog.show("Login Failed", ex.getMessage(), "OK", "Cancel");
                return;
            }
            
            try {
                
                java.util.List<Map> friends = client.getFriends();
                if (friends.isEmpty()) {
                    showSendFriendRequestForm();
                } else {
                    showFeed();
                }
            } catch (Exception ex) {
                Log.e(ex);
                Dialog.show("Friend Lookup Failed", ex.getMessage(), "OK", "Cancel");
                return;
            }
        });
        
        padding.addComponent(loginButton);
        
        Button registerButton = new Button("Register");
        registerButton.addActionListener((e) -> {
           showRegisterForm(); 
        });
        
        padding.addComponent(registerButton);
        f.setLayout(new BorderLayout());
        f.addComponent(BorderLayout.CENTER, padding);
        f.show();
    }
    
     /**
     * Shows the specified error message in a modal dialog.
     * @param msg 
     */
    public void showError(String msg) {
        Dialog.show("Failed", msg, "OK", null);
    }
    
    /**
     * Shows the Registration Form
     */
    public void showRegisterForm() {
       Form f = new Form("Register");
        final Form currentForm = Display.getInstance().getCurrent();
        f.setBackCommand(new Command("Back") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                currentForm.showBack();
            }
            
        });
        Container padding = new Container();
        Style s = new Style();
        s.setPadding(0, 15, 5, 5);
        s.setPaddingUnit(new byte[]{
            Style.UNIT_TYPE_DIPS, 
            Style.UNIT_TYPE_DIPS,
            Style.UNIT_TYPE_DIPS,
            Style.UNIT_TYPE_DIPS
        });
        
        
        padding.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        padding.addComponent(new Label("Username"));
        TextField usernameField = new TextField();
        TextField passwordField = new TextField();
        passwordField.setConstraint(TextField.PASSWORD);
        padding.addComponent(usernameField);
        padding.addComponent(new Label("Password"));
        padding.addComponent(passwordField);
        
        padding.addComponent(new Label("Password Verify"));
        TextField passwordVerifyField = new TextField();
        passwordVerifyField.setConstraint(TextField.PASSWORD);
        padding.addComponent(passwordVerifyField);
        
        Button registerButton = new Button("Register");
        registerButton.addActionListener((e) -> {
            try {
                client.register(usernameField.getText(), passwordField.getText());
                client.login(usernameField.getText(), passwordField.getText());
                
                java.util.List<Map> friends = client.getFriends();
                if (friends.isEmpty()) {
                    showSendFriendRequestForm();
                } else {
                    showFeed();
                }
            } catch (Exception ex) {
                Log.e(ex);
                showError(ex.getMessage());
            }
        });
        
        
        padding.addComponent(registerButton);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener((e) -> {
           currentForm.showBack(); 
        });
        padding.addComponent(cancelButton);
        
        f.setLayout(new BorderLayout());
        f.addComponent(BorderLayout.CENTER, padding);
        f.show();
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
        //TODO Implement show friend requests form
    }
    

}
