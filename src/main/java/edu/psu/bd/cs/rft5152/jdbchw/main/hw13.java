/**
 * hw13.java
 *
 * An App the searches through the table
 * It is available to do change data
 *
 * @author Roman Tuzhilkin
 */

package edu.psu.bd.cs.rft5152.jdbchw.main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class hw13 extends Application
{
    //Searching request
    private int flagCodeName = 1; //When 1 - searching by name, 0 - searching by code
    private String searchFor = "";
    private String currentCode = "";//Keeps track of the current country code for previous button
    private ArrayList<String> results = new ArrayList<String>();//Keeps all the found country codes
    //GUI set

    private HBox hbox = new HBox();

    private TextField searchName = new TextField();
    private TextField searchCode = new TextField();
    private Button bName = new Button("Search by name");
    private Button bCode = new Button("Search by code");

    final private Label lname = new Label("Country Name: ");
    final private Label lcode = new Label("Country Code: ");
    final private Label lhead = new Label("Head of State: ");
    final private Label llang = new Label("Languages spoken: ");
    final private Label lcity = new Label("Cities: ");
    private TextField tname = new TextField();
    private TextField tcode = new TextField();
    private TextField thead = new TextField();
    private TextField tlang = new TextField();
    private TextField tcity = new TextField();

    private Button saveChange = new Button("Save Changes");
    private Button prev = new Button("Previous");
    private Button next = new Button("Next");

    //End of GUI set

    private void buildScene()//Function fills the main hbox that is being send to scene
    {
        HBox temp1;
        VBox temp2 = new VBox(5);

        searchName.setAlignment(Pos.CENTER);
        searchName.setMinWidth(150);
        searchCode.setAlignment(Pos.CENTER);
        searchCode.setMinWidth(150);
        temp1 = new HBox(searchName,searchCode);
        temp2.getChildren().addAll(temp1);

        bName.setMinWidth(150);
        bName.setMaxWidth(150);
        bCode.setMinWidth(150);
        bCode.setMaxWidth(150);
        temp1 = new HBox(bName,bCode);
        temp2.getChildren().addAll(temp1);

        lname.setMinWidth(130);
        tname.setMinWidth(180);
        temp1 = new HBox(lname,tname);
        temp2.getChildren().addAll(temp1);
        lcode.setMinWidth(130);
        tcode.setMinWidth(180);
        temp1 = new HBox(lcode,tcode);
        temp2.getChildren().addAll(temp1);
        lhead.setMinWidth(130);
        thead.setMinWidth(180);
        temp1 = new HBox(lhead,thead);
        temp2.getChildren().addAll(temp1);
        llang.setMinWidth(130);
        tlang.setMinWidth(250);
        tlang.setDisable(true);
        tlang.setAlignment(Pos.TOP_LEFT);
        temp1 = new HBox(llang,tlang);
        temp2.getChildren().addAll(temp1);
        lcity.setMinWidth(130);
        tcity.setMinWidth(250);
        tcity.setDisable(true);
        tcity.setAlignment(Pos.TOP_LEFT);
        temp1 = new HBox(lcity,tcity);
        temp2.getChildren().addAll(temp1);

        hbox.getChildren().addAll(temp2);

        saveChange.setMinWidth(60);
        saveChange.setAlignment(Pos.CENTER);
        temp2 = new VBox(5, saveChange);
        prev.setMinWidth(60);
        prev.setAlignment(Pos.CENTER);
        next.setMinWidth(60);
        next.setAlignment(Pos.CENTER);
        temp1 = new HBox(5, prev, next);
        temp2.getChildren().addAll(temp1);
        temp2.setAlignment(Pos.CENTER);

        hbox.getChildren().addAll(temp2);

    }
    private void setButtons()//Function that setsOnAction buttons in the App
    {
        bName.setOnAction(new SearchByName());
        bCode.setOnAction(new SearchByCode());
        saveChange.setOnAction(new ChangeData());
        prev.setOnAction(new ShowPreviousNote());
        next.setOnAction(new ShowNextNote());
    }
    private void getLangCity(String tempCode)//Function that gets all Languages and Cities from the node.
    {
        String tempString = "";

        Connection c;
        try
        {
            c = DriverManager.getConnection("jdbc:sqlite:/Users/Romis/IdeaProjects/final-project/src/main/resources/World");

            String sql = "SELECT lang.Language\n" +
                    "FROM CountryLanguage AS lang\n" +
                    "WHERE lang.CountryCode LIKE ?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, tempCode);
            ResultSet r = p.executeQuery();
            while (r.next())
            {
                tempString = tempString + r.getString(1) + ", ";
            }
            if (tempString.length()>2)
                tlang.setText(tempString.substring(0,tempString.length()-2));
            else
                tlang.setText(tempString);
            r.close();

            tempString = new String();

            sql = "SELECT c.Name\n" +
                    "FROM City AS c\n" +
                    "WHERE c.CountryCode LIKE ?";
            p = c.prepareStatement(sql);
            p.setString(1, tempCode);
            r = p.executeQuery();
            while (r.next())
            {
                tempString = tempString + r.getString(1) + ", ";
            }
            if (tempString.length()>2)
                tcity.setText(tempString.substring(0,tempString.length()-2));
            else
                tcity.setText(tempString);
            r.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void start(Stage primaryStage) throws Exception
    {
        buildScene();
        setButtons();

        Scene scene = new Scene(hbox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Schemas Search");
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    class SearchByName implements EventHandler<ActionEvent>
    {
        public void handle(ActionEvent actionEvent)
        {
            String tempCode = "";

            results.clear();
            flagCodeName = 1;
            searchCode.setText("");
            searchFor = searchName.getText();

            Connection c;
            try {
                c = DriverManager.getConnection("jdbc:sqlite:/Users/Romis/IdeaProjects/final-project/src/main/resources/World");

                String sql = "SELECT country.Name, country.Code, country.HeadOfState\n" +
                        "FROM Country AS country\n" +
                        "WHERE country.Name LIKE ?";
                PreparedStatement p = c.prepareStatement(sql);
                p.setString(1, searchFor + "%");
                ResultSet r = p.executeQuery();

                tempCode = r.getString(2);
                currentCode = tempCode;
                results.add(tempCode);
                tname.setText(r.getString(1));
                tcode.setText(r.getString(2));
                thead.setText(r.getString(3));
                r.close();

                //Getting all the languages and cities for this country
                getLangCity(tempCode);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    class SearchByCode implements EventHandler<ActionEvent>
    {
        public void handle(ActionEvent actionEvent)
        {
            String tempCode = "";

            results.clear();
            flagCodeName = 0;
            searchName.setText("");
            searchFor = searchCode.getText();

            Connection c;
            try
            {
                c = DriverManager.getConnection("jdbc:sqlite:/Users/Romis/IdeaProjects/final-project/src/main/resources/World");

                String sql = "SELECT country.Name, country.Code, country.HeadOfState\n" +
                        "FROM Country AS country\n" +
                        "WHERE country.Code LIKE ?";
                PreparedStatement p = c.prepareStatement(sql);
                p.setString(1, searchFor + "%");
                ResultSet r = p.executeQuery();

                tempCode = r.getString(2);
                currentCode = tempCode;
                results.add(tempCode);
                tname.setText(r.getString(1));
                tcode.setText(r.getString(2));
                thead.setText(r.getString(3));
                r.close();

                //Getting all the languages and cities for this country
                getLangCity(tempCode);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    class ShowNextNote implements EventHandler<ActionEvent>
    {
        public void handle(ActionEvent actionEvent)
        {
            String tempCode = "";
            String sql = "";
            boolean flag = true;
            int cnt = 0;
            Connection c;
            try
            {
                c = DriverManager.getConnection("jdbc:sqlite:/Users/Romis/IdeaProjects/final-project/src/main/resources/World");

                switch (flagCodeName)
                {
                    case 0:

                        sql = "SELECT country.Name, country.Code, country.HeadOfState\n" +
                                "FROM Country AS country\n" +
                                "WHERE country.Code LIKE ?";
                        PreparedStatement p = c.prepareStatement(sql);
                        p.setString(1, searchFor + "%");
                        ResultSet r = p.executeQuery();

                        while ((flag == true)&&(r.next()))
                        {
                            for (int i = 0; i < results.size(); i++)
                            {
                                if (r.getString(2).equalsIgnoreCase(results.get(i)))
                                {
                                    cnt++;
                                }
                            }
                            if (cnt==0)
                            {
                                flag = false;
                                tempCode = r.getString(2);
                                currentCode = tempCode;
                                results.add(r.getString(2));
                                tname.setText(r.getString(1));
                                tcode.setText(r.getString(2));
                                thead.setText(r.getString(3));

                                r.close();

                                getLangCity(tempCode);
                            }
                            else
                                cnt = 0;
                        }
                    break;

                    case 1:
                        sql = "SELECT country.Name, country.Code, country.HeadOfState\n" +
                                "FROM Country AS country\n" +
                                "WHERE country.Name LIKE ?";
                        p = c.prepareStatement(sql);
                        p.setString(1, searchFor + "%");
                        r = p.executeQuery();

                        while ((flag == true)&&(r.next()))
                        {
                            for (int i = 0; i < results.size(); i++)
                            {
                                if (r.getString(2).equalsIgnoreCase(results.get(i)))
                                {
                                    cnt++;
                                }
                            }
                            if (cnt==0)
                            {
                                flag = false;
                                tempCode = r.getString(2);
                                currentCode = tempCode;
                                results.add(r.getString(2));
                                tname.setText(r.getString(1));
                                tcode.setText(r.getString(2));
                                thead.setText(r.getString(3));

                                r.close();

                                getLangCity(tempCode);
                            }
                            else
                                cnt = 0;
                        }
                    break;
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    class ShowPreviousNote implements EventHandler<ActionEvent>
    {
        public void handle(ActionEvent actionEvent)
        {
            if (results.size()>1)
            {
                int i = results.indexOf(currentCode);
                String tempString = results.get(i - 1);
                String tempCode = "";

                Connection c;
                try {
                    c = DriverManager.getConnection("jdbc:sqlite:/Users/Romis/IdeaProjects/final-project/src/main/resources/World");

                    String sql = "SELECT country.Name, country.Code, country.HeadOfState\n" +
                            "FROM Country AS country\n" +
                            "WHERE country.Code LIKE ?";
                    PreparedStatement p = c.prepareStatement(sql);
                    p.setString(1, tempString);
                    ResultSet r = p.executeQuery();

                    tempCode = r.getString(2);
                    currentCode = tempCode;
                    results.remove(i);
                    tname.setText(r.getString(1));
                    tcode.setText(r.getString(2));
                    thead.setText(r.getString(3));
                    r.close();

                    //Getting all the languages and cities for this country
                    getLangCity(tempCode);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class ChangeData implements EventHandler<ActionEvent>
    {
        public void handle(ActionEvent actionEvent)
        {
            String tempString = "";

            Connection c;
            try
            {
                c = DriverManager.getConnection("jdbc:sqlite:/Users/Romis/IdeaProjects/final-project/src/main/resources/World");

                tempString = tname.getText();
                String sql = "UPDATE Country\n" +
                        "SET Name = ?\n" +
                        "WHERE Code LIKE ?";
                PreparedStatement p = c.prepareStatement(sql);
                p.setString(1, tempString);
                p.setString(2, currentCode);
                p.executeUpdate();

                tempString = thead.getText();
                sql = "UPDATE Country\n" +
                        "SET HeadOfState = ?\n" +
                        "WHERE Code LIKE ?";
                p = c.prepareStatement(sql);
                p.setString(1, tempString);
                p.setString(2, currentCode);
                p.executeUpdate();

                tempString = tcode.getText();
                sql = "UPDATE Country\n" +
                        "SET Code = ?\n" +
                        "WHERE Code LIKE ?";
                p = c.prepareStatement(sql);
                p.setString(1, tempString);
                p.setString(2, currentCode);
                p.executeUpdate();

                tempString = tcode.getText();
                sql = "UPDATE City\n" +
                        "SET CountryCode = ?\n" +
                        "WHERE CountryCode LIKE ?";
                p = c.prepareStatement(sql);
                p.setString(1, tempString);
                p.setString(2, currentCode);
                p.executeUpdate();

                tempString = tcode.getText();
                sql = "UPDATE CountryLanguage\n" +
                        "SET CountryCode = ?\n" +
                        "WHERE CountryCode LIKE ?";
                p = c.prepareStatement(sql);
                p.setString(1, tempString);
                p.setString(2, currentCode);
                p.executeUpdate();

                for(int i=0;i<results.size();i++)
                {
                    if (results.get(i).equalsIgnoreCase(currentCode))
                    {
                        results.set(i,tcode.getText());
                    }
                }

                currentCode = tcode.getText();

                p.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
