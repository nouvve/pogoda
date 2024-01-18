package com.example.weather;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloApplication extends Application {
    TextField search = new TextField();
    Label currentWeatherLabel= new Label();
    Label forecast1 = new Label();
    Label forecast2 = new Label();
    Label forecast3 = new Label();
    private String API = "165288cee32aa2b123157f6f24f0e088";
    @Override
    public void start(Stage primaryStage) {
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e->searchWeather());
        Text forecastTitle = new Text("Forecast for 3 upcoming days:");
        GridPane root = new GridPane();
        root.setVgap(5);
        root.addRow(1,search,searchButton);
        root.addRow(2,currentWeatherLabel);
        root.addRow(3,forecastTitle);
        root.addRow(4,forecast1);
        root.addRow(5,forecast2);
        root.addRow(6,forecast3);
        Group group = new Group(root);
        Scene scene = new Scene(group,400,500,Color.LIGHTGRAY);
        primaryStage.setScene(scene);
        primaryStage.setTitle("API Weather");
        primaryStage.show();
    }

    private void searchWeather(){
        String city = search.getText();
        if (!city.isEmpty()) {
            String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API;
            String forecastURL = "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API;
            try {
                updateWeather(weatherURL,forecastURL);
            } catch (Exception e) {
                currentWeatherLabel.setText("error fetching the data.");
            }
        } else {
            currentWeatherLabel.setText("write a city name");
        }
    }
    private void updateWeather(String weatherUrl,String forecastUrl) throws Exception {
        JSONObject weatherObject = new JSONObject(fetch(weatherUrl));
        JSONObject forecastObject = new JSONObject(fetch(forecastUrl));
        String cityName = weatherObject.getString("name");
        JSONObject main = weatherObject.getJSONObject("main");
        double temperature = main.getDouble("temp");
        JSONObject weather = weatherObject.getJSONArray("weather").getJSONObject(0);
        String weatherDescription = weather.getString("description");
        String iconCode = weather.getString("icon");
        ImageView weatherIcon = new ImageView(new Image("http://openweathermap.org/img/wn/" + iconCode + ".png"));
        String displayText = String.format("%s: %.0f°C, %s", cityName, (temperature-274), weatherDescription);
        currentWeatherLabel.setText(displayText);
        currentWeatherLabel.setGraphic(weatherIcon);
        try {
            JSONArray forecastList = forecastObject.getJSONArray("list");
            for (int i = 0; i < forecastList.length(); i += 8) {
                JSONObject entry = forecastList.getJSONObject(i);
                long timestamp = entry.getLong("dt");
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(timestamp * 1000));
                JSONObject mainforecast = entry.getJSONObject("main");
                double temperatureForecast = mainforecast.getDouble("temp");
                JSONArray weatherArray = entry.getJSONArray("weather");
                String forecastDescription = weatherArray.getJSONObject(0).getString("description");
                String forecastIcon = weatherArray.getJSONObject(0).getString("icon");

                ImageView weatherIco = new ImageView(new Image("http://openweathermap.org/img/wn/" + forecastIcon + ".png"));
                String format = String.format("%s: %.0f°C, %s", date, (temperatureForecast - 274), forecastDescription);
                if (i == 0) {
                    forecast1.setText(format);
                    forecast1.setGraphic(weatherIco);
                } else if (i == 8){
                    forecast2.setText(format);
                    forecast2.setGraphic(weatherIco);
                } else if (i == 16){
                    forecast3.setText(format);
                    forecast3.setGraphic(weatherIco);
                }
            }
        } catch (Exception e){
            currentWeatherLabel.setText("error fetching the data.");
            forecast1.setText("error fetching the data.");
        }
    }
    private String fetch(String urlString) throws Exception{
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return reader.readLine();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
