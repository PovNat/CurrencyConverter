package com.example.CurrencyConverter.Parsing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Parser {
    public Parser() throws IOException {
    }

    public String getParse() {
        try {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String newDate = dateFormat.format(date);
            Document doc1 = Jsoup.connect("http://www.cbr.ru/scripts/XML_daily.asp?date_req="+ newDate).get();
            String actual_date = doc1.select("ValCurs").attr("Date");

            Elements els = doc1.select("Valute");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/JavaServer", "postgres", "310193");
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("insert into actual_information (actual_date, NumCode, CharCode, Nominal, Name, Value) " +
                            "values (?, ?, ?, ?, ?, ?)");


            for (Element e : els) {
                String st2 = e.select("NumCode").text();
                Integer in2 = Integer.parseInt(st2);
                String st3 = e.select("CharCode").text();
                String st4 = e.select("Nominal").text();
                Integer in4 = Integer.parseInt(st4);
                String st5 = e.select("Name").text();
                String st6 = e.select("Value").text().replaceAll(",", ".");
                Double db6 = Double.parseDouble(st6);

                preparedStatement.setString(1, actual_date);
                preparedStatement.setInt(2, in2);
                preparedStatement.setString(3, st3);
                preparedStatement.setInt(4, in4);
                preparedStatement.setString(5, st5);
                preparedStatement.setDouble(6, db6);
                preparedStatement.execute();
            }
            preparedStatement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Successfully updated";
    }
}
