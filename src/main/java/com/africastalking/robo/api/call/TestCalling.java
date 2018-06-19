package com.africastalking.robo.api.call;

import org.json.*;

import com.africastalking.robo.api.AfricasTalkingGateway;
import com.africastalking.robo.utils.Configs;

public class TestCalling {
    public static void call(String str) {
        String username = Configs.username();
        String apiKey   = Configs.apiKey();
        String from = Configs.from();
        AfricasTalkingGateway gateway  = new AfricasTalkingGateway(username, apiKey);
        try {
            JSONArray results = gateway.call(from, str);
            int len = results.length();
            for(int i = 0; i < len; i++) {
                JSONObject result = results.getJSONObject(i);
                //Only status "Queued" means the call was successfully placed
                System.out.print(result.getString("status") + ",");
                System.out.print(result.getString("phoneNumber") + "\n");
            }
            // Our API will now contact your callback URL once the recipient answers the call!
        } catch (Exception e) {
            System.out.println("Encountered an error" + e.getMessage());
        }
    }
}