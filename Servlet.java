/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONObject;

/**
 *
 * @author Irindu
 */
@WebServlet(urlPatterns = {"/Servlet"})
public class Servlet extends HttpServlet {

    final PotStation thePotStation = new PotStation();

    @Override
    public void init(ServletConfig config) {
    }


    @Override

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        HttpSession session = request.getSession(false);

        if (session == null) {
            session = request.getSession(true);
            //authenticate
            session.setAttribute("privatekey", request.getParameter("message"));
       
            System.out.println("new session");
            System.out.println(request.getParameter("message"));

        } else {
            System.out.println("old session");
            
            System.out.println(session.getAttribute("privatekey"));
           }
        response.setContentType("text/event-stream;charset=UTF-8");
        response.flushBuffer();
        String privateKey = (String) session.getAttribute("privatekey");

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            while (!Thread.interrupted()) {
                System.out.println("running");
                synchronized (thePotStation) {
                    if (!PotStation.potMap.containsKey(privateKey)) {
                        System.out.println("waiting");
                        thePotStation.wait();//wait until the first data insertion
                    } 
                       //web application ekata jason
                    JSONObject obj = new JSONObject();
                    //pot eke status eka web application ekata yawanawa
                    obj.put("STATUS", new Integer(PotStation.potMap.get(privateKey).LastresponseCode));
                    obj.put("SoilMoisture", new Integer(PotStation.potMap.get(privateKey).getLastSoilMoistureValue()));
                    obj.put("LightIntensity", new Integer(PotStation.potMap.get(privateKey).getLastLightIntensityValue()));
                    obj.put("Temperature", new Integer(PotStation.potMap.get(privateKey).getLastTemperatureValue()));
                    System.out.println(obj);
                    out.print("data: ");
                    out.println(obj);
                    out.println();
                    out.flush();
                    thePotStation.wait();//post request eka athule notify all wenakan
                    //mc produce
                    //web application consume
                    //producer consumer concept
                    System.out.println("waiting at 2");
                }

            }

        } catch (InterruptedException ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception");
            System.out.println(ex);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String privateKey;
        int soilMoisture;
        int lightIntensity;
        int temperature;
        //serial number eka ewanawa.extraxt kara gannawa mona pot ekada balanna
        privateKey = request.getParameter("privateKey").trim();
        soilMoisture = Integer.parseInt(request.getParameter("soilMoisture").trim());
        lightIntensity = Integer.parseInt(request.getParameter("lightIntensity").trim());
        temperature = Integer.parseInt(request.getParameter("temperature").trim());

        int responseCode = 0;
        /*
        Response Codes
        0 Everything OK
        1 Pot needs water
        2 Pot needs SunLight
        3 Temperature is too High
        4 Tem\perature is too Low
         */

        
        synchronized (thePotStation) {//potstation can only service one user atone time

            if (!PotStation.potMap.containsKey(privateKey)) {
                PotStation.potMap.put(privateKey, new TalkingPot(privateKey, soilMoisture, lightIntensity, temperature));
            }

            //make sure to caculate the response before adding the data to the data base response calculation depends on last added data
            responseCode = thePotStation.getReponse(privateKey, soilMoisture, lightIntensity, temperature);

            thePotStation.addToDatabase(privateKey, soilMoisture, lightIntensity, temperature);

            if (PotStation.potMap.get(privateKey).isChange()) {
             
                thePotStation.notifyAll();
                PotStation.potMap.get(privateKey).setChange(false);
            }

            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println(responseCode);
                out.println(responseCode);
            }
            System.out.println("\n\n Notified \n \n");
            thePotStation.notifyAll();

        }

    }

  
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void destroy() {
        thePotStation.OnDestroy();
    }
}
