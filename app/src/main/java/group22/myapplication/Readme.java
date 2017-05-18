package group22.myapplication;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Readme extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(R.layout.readme);

        String ReadMe = "<html><body>" +
                "<h1>User Guide</h1><br>" +
                "<h2>Main menu</h2><br>" +
                "<p>Select the language you are learning.</p><br>" +
                "<h2>Game mode menu</h2><br>" +
                "<p>Select from the following modes:\n" +
                "Quickdraw - A timed multiple-choice game;\n" +
                "Create Cards - Create your own custom cards;\n" +
                "View Cards - View all of your cards. Edit and delete cards.</p><br>" +
                "<h2>Quickdraw</h2><br>" +
                "<p>Find the correct translation as many times as possible in 60 seconds. " +
                "You will be presented with four possible translations of each word " +
                "and your score will be tracked. You may then share your results " +
                "via social media.</p><br>" +
                "<h2>Create Cards</h2><br>" +
                "<p>Create new cards to be used in other game modes. " +
                "<h3>Picture Card</h3><br>" +
                "<p>Choose an image for the card. You may either use an image from your " +
                "photo gallery, or take a new photo. Then, enter a name for the item/activity " +
                "and press the submit button. The app will then find the translation and store " +
                "the new picture card in the database</p><br>" +
                "<h3>Word Card</h3><br>" +
                "<p>Enter the name of the card(i.e. an english word) and press the submit button. " +
                " The app will then find the translation and store the new word card in the " +
                "database</p><br>" +
                "<h2>View Cards</h2><br>" +
                "<h3>Card Index</h3><br>" +
                "<p>This is a list of all cards in the selected language. Select a card to " +
                "view it.</p>" +
                "<h3>Card Display</h3><br>" +
                "<p>The card will be displayed here, with options to edit or delete it. " +
                "Pressing the delete button will delete the card and display a toast informing " +
                "you of the change. Pressing the edit button will allow you to change the english " +
                "word or the image used on the card(picture cards only). Pressing the submit button " +
                "will save the changes that have been made. Pressing the return button will return " +
                "to the card index.</p>" +
                "</body></html>";

        webView.loadData(ReadMe, "text/html", null);

    }


}
