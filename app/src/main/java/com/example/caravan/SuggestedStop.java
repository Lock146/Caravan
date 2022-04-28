package com.example.caravan;

import com.example.caravan.Model.GooglePlaceModel.LocationModel;

public class SuggestedStop {
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PLACEID = "placeID";

    private LocationModel m_location;
    private String m_placeID;

    SuggestedStop(GooglePlaceModel suggestion){
        m_location = suggestion.getGeometry().getLocation();
        m_placeID = suggestion.placeID();
    }

    public LocationModel location(){
        return m_location;
    }

    public String placeID(){
        return m_placeID;
    }
}
