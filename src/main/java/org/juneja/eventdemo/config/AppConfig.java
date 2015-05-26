package org.juneja.eventdemo.config;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;

@Component
public class AppConfig {

    @Value("${isSubscriptionConfirmed}")
    private boolean isSubscriptionConfirmed;

    // ...
    @Value("{uriForDataAPI}")
    private String uriForDataAPI;

	public boolean isSubscriptionConfirmed() {
		return isSubscriptionConfirmed;
	}

	public void setSubscriptionConfirmed(boolean isSubscriptionConfirmed) {
		this.isSubscriptionConfirmed = isSubscriptionConfirmed;
	}

	public String getUriForDataAPI() {
		return uriForDataAPI;
	}

	public void setUriForDataAPI(String uriForDataAPI) {
		this.uriForDataAPI = uriForDataAPI;
	}
    
    

}
