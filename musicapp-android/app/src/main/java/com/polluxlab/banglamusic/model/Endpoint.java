package com.polluxlab.banglamusic.model;

import android.util.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Endpoint extends ModelBase {

  private transient static Endpoint self = null;

  private Links links;

  private Endpoint() {
  }

  public static Endpoint instance() {
    if (self == null) {
      try {
        synchronized (Endpoint.class) {
          String response = get(ModelBase.ENDPOINT_URL);
          self = gson.fromJson(response, Endpoint.class);
        }
      } catch (RuntimeException e) {
        Log.e(Endpoint.class.getName(), "Failed to initialize Endpoint", e);
        throw e;
      }
    }

    return self;
  }

  public List<Tag> getTags() {
    String response = get(this.links.getTags());
    if (response.isEmpty()) {
      return new ArrayList<>();
    }
    else {
      Type songCollectionType = new TypeToken<List<Tag>>() {
      }.getType();
      return gson.fromJson(response, songCollectionType);
    }
  }

  public String getAuthUrl() {
    return links.getLogin().replace("http", "https");
  }

  public Subscription getSubscription(String secret) {
    return this.links.getSubscription(secret);
  }

  public String getPurchase() {
    return links.getPurchase();
  }

}

