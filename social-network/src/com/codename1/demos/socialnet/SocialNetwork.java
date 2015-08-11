package com.codename1.demos.socialnet;


import com.codename1.capture.Capture;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.MultiList;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        showProfile(client.getUsername());
    }
    
    
    /**
     * Shows the profile of a given user.
     * @param username 
     */
    public void showProfile(String username) {
        try {
            Map profile = client.getProfile(username);
            Form f = new Form((String)profile.get("screen_name"));
            addMenu(f);
            f.setLayout(new BorderLayout());
            Container padding = new Container();
            padding.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            Image avatar = defaultAvatarLarge;
            if (profile.get("avatar") != null) {
                String url = (String)profile.get("avatar");
                avatar = URLImage.createToStorage((EncodedImage)avatar, url+"?large", url, URLImage.RESIZE_SCALE_TO_FILL);
            }
            Button avatarBtn = new Button(avatar);
            padding.addComponent(avatarBtn);
            
            ComponentGroup g = new ComponentGroup();
            
            Button screenName = new Button("Screen name: "+(String)profile.get("screen_name"));
            screenName.addActionListener((evt)->{
                //TODO: Update screen name when clicked
            });
            
            g.addComponent(screenName);
            padding.addComponent(g);
            
            f.addComponent(BorderLayout.CENTER, padding);
            
            avatarBtn.addActionListener((e) -> {
                //TODO: Update avatar when clicked
            });
            
             
            f.show();
            
            
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }
    
    /**
     * Shows form to add a post
     * @param back The form to return to on completion.
     */
    public void showAddPostForm(Form back) {
        Form f = new Form("Add Post");
        
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        TextArea commentField = new TextArea();
        commentField.setRows(5);
        commentField.setHint("Enter comment");

        Button photoButton = new Button("Attach Photo");
        photoButton.setTextPosition(Label.BOTTOM);
        photoButton.addActionListener((evt)-> {
            String file = Capture.capturePhoto(1024, -1);
            if (file == null) {
                return;
            }
            try {
                Image img = Image.createImage(file).scaledSmallerRatio(256, 256);
                photoButton.setIcon(img);
                f.revalidate();
            } catch (IOException ex) {
                showError(ex.getMessage());
                return;
            }

        });

        Button submitButton = new Button("Submit");
        submitButton.addActionListener((evt)->{
            try {
                Map vals = new HashMap();
                vals.put("comment", commentField.getText());
                
                if (photoButton.getIcon() != null) {
                    vals.put("photo", photoButton.getIcon());
                }
                long id = client.post(vals);
                back.showBack();
            } catch(Exception ex) {
                showError(ex.getMessage());
                return;
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener((evt)->{
            back.showBack();
        });
        
        f.addComponent(commentField);
        f.addComponent(photoButton);
        f.addComponent(submitButton);
        f.addComponent(cancelButton);
        
        f.show();
        
    }
    
    /**
     * Shows the news feed for the current user.
     */
    public void showFeed() {
        Form f = new Form("Updates");
        addMenu(f);
        f.setLayout(new BorderLayout());
        
        Container buttons = new Container();
        buttons.setLayout(new GridLayout(1, 2));
        Button addPost = new Button("Add Post");
        addPost.addActionListener((e)->{
            showAddPostForm(f);
        });
        buttons.addComponent(addPost);
        
        
        Container list = new Container();
        list.setScrollableY(true);
        list.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        try {
            java.util.List<Map> feed = client.getFeed(null);
            for(Map item : feed) {
                Container itemWrapper = new Container();
                itemWrapper.setUIID("FeedItem");
                itemWrapper.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
                
                Container topRow = new Container();
                topRow.setUIID("FeedItemTopRow");
                topRow.setLayout(new BoxLayout(BoxLayout.X_AXIS));
                String avatarUrl =  (String)item.get("avatar");
                URLImage img = URLImage.createToStorage((EncodedImage)defaultAvatarSmall, avatarUrl+"?small", avatarUrl, URLImage.RESIZE_SCALE_TO_FILL);
                topRow.addComponent(new Label(img));
                
                Container postDetails = new Container();
                postDetails.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
                
                postDetails.addComponent(new Label((String)item.get("screen_name")));
                Label posted = new Label("Posted "+L10NManager.getInstance().formatDateTime(new Date(1000l*Long.parseLong((String)item.get("date_posted")))));
                posted.setUIID("FeedDateLabel");
                postDetails.addComponent(posted);
                topRow.addComponent(postDetails);
                itemWrapper.addComponent(topRow);
                
                itemWrapper.addComponent(new SpanLabel((String)item.get("comment")));
                
                if (item.get("photo") != null) {
                    String photoUrl = (String)item.get("photo");
                    URLImage photo = URLImage.createToStorage((EncodedImage)fullWidthPlaceHolder, photoUrl+"?"+Display.getInstance().getDisplayWidth(), photoUrl, URLImage.RESIZE_SCALE_TO_FILL);
                    itemWrapper.addComponent(new Label(photo));
                }
                
                list.addComponent(itemWrapper);
                
            }
        } catch (IOException ex) {
            showError(ex.getMessage());
            return;
        }
        
        f.addComponent(BorderLayout.NORTH, buttons);
        f.addComponent(BorderLayout.CENTER, list);
    
        
        f.show();
    }
   
    /**
     * Shows the friends of the current user.
     */
    public void showFriends() {
        Form f = new Form("Friends");
        addMenu(f);
        f.setLayout(new BorderLayout());
        
        
        MultiList list = new MultiList();
        
        final ArrayList<Map> resultListModel = new ArrayList<Map>();
        try {
            java.util.List<Map> friends = client.getFriends();
            resultListModel.addAll(friends);
            for (Map entry : resultListModel) {
                entry.put("Line1", entry.get("screen_name"));
                entry.put("Line2", entry.get("username"));
                entry.put("Line3", "Click to accept");
                
                String avatarUrl =  (String)entry.get("avatar");
                if (avatarUrl == null) {
                    entry.put("icon", defaultAvatarSmall);
                } else {
                    entry.put("icon", URLImage.createToStorage((EncodedImage)defaultAvatarSmall, avatarUrl+"?small", avatarUrl, URLImage.RESIZE_SCALE_TO_FILL));
                }
            }
            DefaultListModel model = new DefaultListModel(resultListModel);
            list.setModel(model);
            
        } catch (Exception ex) {
            showError(ex.getMessage());
            return;
        }
        
        
        list.addActionListener((e) -> {
            Map sel = (Map)list.getSelectedItem();
            showProfile((String)sel.get("username"));
        });
        
        f.addComponent(BorderLayout.CENTER, list);
        
        f.show();
    }
    
    /**
     * Shows the pending friend requests for the current user.
     */
    public void showFriendRequests() {
        Form f = new Form("Pending Friend Requests");
        addMenu(f);
        f.setLayout(new BorderLayout());
        
        
        MultiList list = new MultiList();
        
        final ArrayList<Map> resultListModel = new ArrayList<Map>();
        try {
            java.util.List<Map> requests = client.getPendingFriendRequests();
            resultListModel.addAll(requests);
            for (Map entry : resultListModel) {
                entry.put("Line1", entry.get("screen_name"));
                entry.put("Line2", entry.get("username"));
                entry.put("Line3", "Click to accept");
                
                String avatarUrl =  (String)entry.get("avatar");
                if (avatarUrl == null) {
                    entry.put("icon", defaultAvatarSmall);
                } else {
                    entry.put("icon", URLImage.createToStorage((EncodedImage)defaultAvatarSmall, avatarUrl+"?small", avatarUrl, URLImage.RESIZE_SCALE_TO_FILL));
                }
            }
            DefaultListModel model = new DefaultListModel(resultListModel);
            list.setModel(model);
            
        } catch (Exception ex) {
            showError(ex.getMessage());
            return;
        }
        
        
        list.addActionListener((e) -> {
            Map sel = (Map)list.getSelectedItem();
            
            if (Dialog.show("Accept Friend Request?", "Accept friend request from "+sel.get("screen_name")+"?", "Yes", "Cancel")) {
                InfiniteProgress p = new InfiniteProgress();
                Dialog dlg = p.showInifiniteBlocking();
                try {
                    client.acceptFriendRequest((String)sel.get("username"));
                    Dialog.show("Request Accepted", "You are now friends with "+sel.get("screen_name"), "OK", null);
                    showFriendRequests();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
                dlg.dispose();
            }
        });
        
        f.addComponent(BorderLayout.CENTER, list);
        
        f.show();
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
        
        list.addActionListener((e) -> {
            Map sel = (Map)list.getSelectedItem();
            if (Integer.parseInt((String)sel.get("is_friend")) == 1) {
                return;
            }
            if (Integer.parseInt((String)sel.get("has_pending_invite")) == 1) {
                return;
            }
            if (Dialog.show("Send Friend Request", "Send a frend request to "+sel.get("screen_name")+"?", "Send", "Cancel")) {
                InfiniteProgress p = new InfiniteProgress();
                Dialog dlg = p.showInifiniteBlocking();
                try {
                    client.sendFriendRequest((String)sel.get("username"));
                    Dialog.show("Send Request Sent", "The request was successfully sent.", "OK", null);
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
                dlg.dispose();
            }
        });
        
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
