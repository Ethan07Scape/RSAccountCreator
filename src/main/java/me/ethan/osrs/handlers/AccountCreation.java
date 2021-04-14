package me.ethan.osrs.handlers;

import me.ethan.osrs.api.HttpAPI;
import me.ethan.osrs.api.captcha.ReCaptchaV2;
import me.ethan.osrs.api.proxy.Proxy;
import me.ethan.osrs.data.Constants;
import me.ethan.osrs.utils.Random;
import org.apache.http.NameValuePair;

import java.util.List;

public class AccountCreation {

    private final HttpAPI httpAPI;
    private final String email;
    private final Proxy proxy;
    private String currentSource;

    private final String[] emailProviders = new String[]{
      "@aol.com", "@netscape.net", "@yahoo.com", "@gmail.com", "@email.com"
    };

    public AccountCreation(final Proxy proxy) {
        this.httpAPI = new HttpAPI();
        this.email = generateEmail();
        this.proxy = proxy;
    }

    public String getResponse() {
        try {
            final ReCaptchaV2 reCaptcha = new ReCaptchaV2(Constants.CAPTCHA_KEY, Constants.GOOGLE_KEY, Constants.CREATE_LINK);
            final String captchaResponse = reCaptcha.solve();
            this.currentSource = httpAPI.get(Constants.CREATE_LINK, proxy);
            final List<NameValuePair> form = httpAPI.getFilledForm(this.currentSource, captchaResponse, this.email);
            this.currentSource = httpAPI.post(Constants.CREATE_LINK, form, proxy);
            if (createSuccess()) {
                return "SUCCESS";
            } else {
                System.out.println(this.currentSource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }


    private String generateEmail() {
        return Random.nextInt(0, 999) + Random.getRandomName(Random.nextInt(4, 10)) + Random.nextInt(0, 99) + emailProviders[Random.nextInt(0, emailProviders.length)];
    }

    private boolean createSuccess() {
        if (this.currentSource == null)
            return false;
        return this.currentSource.toLowerCase().contains("account created");
    }

    public String getEmail() {
        return email;
    }
}
