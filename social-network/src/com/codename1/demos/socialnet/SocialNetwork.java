package com.codename1.demos.socialnet;


import com.codename1.components.InfiniteProgress;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.MultiList;
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
    
    // Image placeholders for use with URLImage to keep correct size
    // for current device.  Initialized in init()
    Image defaultAvatarLarge;
    Image defaultAvatarSmall;
    Image fullWidthPlaceHolder;
   
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
        
        // Enable the "Hamburger" menu
        Display.getInstance().setCommandBehavior(Display.COMMAND_BEHAVIOR_SIDE_NAVIGATION);
        
        // Initialize the REST client
        client = new SocialClient();
        
        // Initialize the image placeholders
        int maxAvatarWidth = (int)Math.round(Math.min(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight()) * 0.75);
        Image avatar = theme.getImage("avatar_default_512.png");
        defaultAvatarLarge = avatar.scaled(maxAvatarWidth, maxAvatarWidth);
        
        maxAvatarWidth = (int)Math.round(Math.min(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight()) / 6.0);
        defaultAvatarSmall = avatar.scaled(maxAvatarWidth, maxAvatarWidth);
        if (!(defaultAvatarSmall instanceof EncodedImage)) {
            defaultAvatarSmall = EncodedImage.createFromImage(defaultAvatarSmall, true);
        }
        
        int fullWidthImage = (int)Math.round(Math.min(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight())) - 20;
        fullWidthPlaceHolder = Image.createImage(fullWidthImage, (int)fullWidthImage * 9 / 16);
        if (!(fullWidthPlaceHolder instanceof EncodedImage)) {
            fullWidthPlaceHolder = EncodedImage.createFromImage(fullWidthPlaceHolder, true);
        }
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
        Form f = new Form("Find New Friends");
        addMenu(f);
        f.setLayout(new BorderLayout());
        TextField search = new TextField();
        search.setHint("Search user");
        
        MultiList list = new MultiList();
        list.setModel(new DefaultListModel());
        
        search.addDataChangeListener((type, index) -> {
            if (search.getText().length() > 2) {
                try {
                    java.util.List<Map> results = client.findUsers(search.getText());
                    for (Map entry : results) {
                        entry.put("Line1", entry.get("screen_name"));
                        entry.put("Line2", entry.get("username"));
                        if (Integer.parseInt((String)entry.get("is_friend")) == 1) {
                            entry.put("Line3", "Already friends");
                        } else if (Integer.parseInt((String)entry.get("has_pending_invite")) == 1) {
                            entry.put("Line3", "Invite pending");
                        } else {
                            entry.put("Line3", "Click to invite");
                        }
                        String avatarUrl =  (String)entry.get("avatar");
                        if (avatarUrl == null) {
                            entry.put("icon", defaultAvatarSmall);
                        } else {
                            entry.put("icon", URLImage.createToStorage((EncodedImage)defaultAvatarSmall, avatarUrl+"?small", avatarUrl, URLImage.RESIZE_SCALE_TO_FILL));
                        }
                    }
                    DefaultListModel model = new DefaultListModel(results);
                    list.setModel(model);
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
        });
        
        // TODO : Add ActionListener to list
        
        f.addComponent(BorderLayout.NORTH, search);
        f.addComponent(BorderLayout.CENTER, list);
        
        f.show();
    }
    
     /**
     * Adds the hamburger menu to a form.
     * @param f 
     */
    private void addMenu(Form f) {
        f.addCommand(new Command("Profile") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                showProfile();
            }
            
        });
        f.addCommand(new Command("News") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                showFeed();
            }
            
        });
        
        f.addCommand(new Command("My Friends") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                showFriends();
            }
            
        });
        
        f.addCommand(new Command("Pending Requests") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                showFriendRequests();
            }
            
        });
        
        f.addCommand(new Command("Invite Friends") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                showSendFriendRequestForm();
            }
            
        });
        
        f.addCommand(new Command("Logout") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    client.logout();
                    showLoginForm();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
            
        });
    }
    

}
