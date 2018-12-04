/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Irindu Indeera
 */
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PotStation {//service to pots

    static HashMap<String, TalkingPot> potMap = new HashMap<String, TalkingPot>();

    static Set<String> eventMap = new HashSet<String>();

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";//data base connnectivity handling
    static final String DB_URL = "jdbc:mysql://localhost/tpot";

    static final String USER = "root";
    static final String PASS = "";

    static Connection conn = null;
    static Statement stmt = null;

    static {

        try {
            //Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Create a statement
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

        } catch (SQLException se) {
            //Handle errors for JDBC
            System.out.println("Could not Connect to the databse! SQL ERROR");
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            System.out.println("Could not Connect to the databse!");
            e.printStackTrace();
        }

    }//end of static block

    //updating the data base
    void addToDatabase(String privateKey, int currentSoilMoisture, int currentLightIntensity, int currentTemperature) {

        
        //performance improvemences
        PotStation.potMap.get(privateKey).setLastSoilMoistureValue(currentSoilMoisture);
        PotStation.potMap.get(privateKey).setLastLightIntensityValue(currentLightIntensity);
        PotStation.potMap.get(privateKey).setLastTemperatureValue(currentTemperature);

        try {

            String sql = "INSERT INTO `data`"
                    + "(`User`, `SoilMoisture`, `LightIntensity`, `Temperature`, `time`) VALUES (" + "'" + privateKey + "'" + "," + currentSoilMoisture + "," + currentLightIntensity + "," + currentTemperature + ", CURRENT_TIMESTAMP)";
            System.out.println(sql);
            stmt.executeUpdate(sql);

            System.out.println("PotStation.addToDatabase() Done!");

        } catch (SQLException sqlException) {
            System.out.println("PotStation.addToDatabase() SQL EXCEPTION");
            System.out.println(sqlException);
        } catch (Exception e) {
            System.out.println("PotStation.addToDatabase() Other Exception");
            System.out.println(e);
        }

    }

    
    float getAverage(String privateKey, int hourInterval, String value) {
        try {

            // String sql = "SELECT `"+value+"` FROM `data` WHERE DATE_ADD(NOW(), INTERVAL " + hourInterval + " HOUR) and (`User` = '"+privateKey+"')" ;
            String sql = "SELECT `" + value + "` FROM `data` WHERE `time` >= DATE_SUB(NOW(), INTERVAL " + hourInterval + " HOUR) and (`User` = '" + privateKey + "')";

            System.out.println(sql);
            //System.out.println(stmt);
            ResultSet rs = stmt.executeQuery(sql);

            float count = 0;
            int sum = 0;
            while (rs.next()) {
                //Retrieve by column name
                int id = rs.getInt(value);
                //Add the values
                sum += id;
                count++;
            }
            //Display the count of values usually 172 per hour
            System.out.println("count(172/h) :" + count);
            return (sum / count);

        } catch (SQLException e) {
            System.out.println("Could not calculate the average!");
            System.out.println("SQL EXCEPTION");
            System.out.println(e);
        }

        return 0;
    }

    int getReponse(String privateKey, int SoilMoisture, int LightIntensity, int Temperature) {
        //`SoilMoisture`, `LightIntensity`, `Temperature`, `time`

        int resposneCode = 0; //AlL OK

        /*const float IdealSoilMoisture = 500;
        const float IdealLightIntensity = 500;
        const float IdealTemperature = 500; */
        float IdealSoilMoisture = 40;
        float IdealLightIntensity = 10;
        float IdealTemperatureMin = 18;
        float IdealTemperatureMax = 35;

        //The average values for Soil Moisture Light intensity and Temerature are calculated per hour ,24 hours and 1 hour respectively
        float averageSoilMoisture = this.getAverage(privateKey, 1, "SoilMoisture");
        float averageLightIntensity = this.getAverage(privateKey, 24, "LightIntensity");
        float averageTemperature = this.getAverage(privateKey, 1, "Temperature");

        //The response is priortized in following order. Soil moisture,light intensity and  temperature
        //first check  for soil moisture
        
        
        //prioritized
        //soil
        //light
        //temperature
        if (SoilMoisture < IdealSoilMoisture) {
            if ((averageSoilMoisture < IdealSoilMoisture) && (SoilMoisture < PotStation.potMap.get(privateKey).getLastSoilMoistureValue())) {
                resposneCode = 1;
            }
        } else if (LightIntensity < IdealLightIntensity) {
            if ((averageLightIntensity < IdealLightIntensity) && (LightIntensity < PotStation.potMap.get(privateKey).getLastLightIntensityValue())) {
                resposneCode = 2;
            }
        } else if (Temperature > IdealTemperatureMax) {
            if ((averageTemperature > IdealTemperatureMax) && (Temperature > PotStation.potMap.get(privateKey).getLastTemperatureValue())) {
                resposneCode = 3;
            }
        } else if (Temperature < IdealTemperatureMin) {
            if ((averageTemperature < IdealTemperatureMin) && (Temperature < PotStation.potMap.get(privateKey).getLastTemperatureValue())) {
                resposneCode = 4;
            }
        }
        PotStation.potMap.get(privateKey).setResponseCode(resposneCode);
        return resposneCode;
    }

    
    void OnDestroy() {
        //closing the resources

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se2) {
        }// nothing we can do
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException se) {
            System.out.println("Database Connection did not close!");
            System.out.println(se);
        }//end try

    }// end of onDestroy
}
