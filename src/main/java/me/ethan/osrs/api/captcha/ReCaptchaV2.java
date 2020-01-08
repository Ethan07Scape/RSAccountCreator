package me.ethan.osrs.api.captcha;

import me.ethan.osrs.api.HttpAPI;
import me.ethan.osrs.utils.Condition;


public class ReCaptchaV2 {
    private String siteKey;
    private String googleKey;
    private String url;
    private int timeCount;
    private String response;

    public ReCaptchaV2(String siteKey, String googleKey, String url) {
        this.siteKey = siteKey;
        this.googleKey = googleKey;
        this.url = url;
    }


    public String getCaptchaID() {
        try {
            String proxy = "";
          /*  if (this.proxy != null) {
                proxy = "&proxy=" + this.proxy.getIp() + ":" + this.proxy.getPort()
                        + "&proxytype=https";
            }*/
            final String parameters = "key=" + siteKey
                    + "&method=userrecaptcha"
                    + "&method=userrecaptcha"
                    + "&googlekey=" + googleKey
                    + "&pageurl=" + url
                    + proxy;
            final HttpAPI httpAPI = new HttpAPI();
            return httpAPI.get("https://2captcha.com/in.php?" + parameters, null).replaceAll("OK\\|", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }


    public String solve() {
        try {
            final String captchaId = getCaptchaID();
            System.out.println("Captcha ID: " + captchaId);
            final String parameters = "key=" + siteKey
                    + "&action=get"
                    + "&id=" + captchaId;
            final HttpAPI httpAPI = new HttpAPI();
            String response;
            do {
                response = httpAPI.get("http://2captcha.com/res.php?" + parameters, null);
                setResponse(response);
                Condition.sleep(10000);
                timeCount = timeCount + 10;
                System.out.println("Waiting on captcha to be solved.");
            } while (getResponse().contains("NOT_READY"));
            System.out.println("Captcha took " + timeCount + " seconds to solve.");
            return response.replaceAll("OK\\|", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public String getSiteKey() {
        return siteKey;
    }

    public String getGoogleKey() {
        return googleKey;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

}
