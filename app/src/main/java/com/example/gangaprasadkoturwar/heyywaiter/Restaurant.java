package com.example.gangaprasadkoturwar.heyywaiter;

/**
 * Created by Gangaprasad.Koturwar on 08-02-2015.
 */

public class Restaurant {
    String code;
    String name;
    String location;
    String foodType;
    String priceRange;
    Double rating;

    public String getRestaurantCode(){
        return code;
    }

    public String getRestaurantName(){
        return name;
    }

    public String getRestaurantLocation(){
        return location;
    }

    public String getRestaurantFoodType(){
        return foodType;
    }

    public String getRestaurantPriceRange(){
        return priceRange;
    }

    public Double getRestaurantRating(){
        return rating;
    }

    public void setRestaurantCode(String input){
        code = input;
    }

    public void setRestaurantName(String input){
        name = input;
    }

    public void setRestaurantLocation(String input){
        location = input;
    }

    public void setRestaurantFoodType(String input){
        foodType = input;
    }

    public void setRestaurantRating(Double input){
        rating = input;
    }

    public void setRestaurantPriceRange(String input){
        priceRange = input;
    }
}

