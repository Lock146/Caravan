package com.example.caravan.Constant;

import com.example.caravan.PlaceModel;
import com.example.caravan.R;

import java.util.ArrayList;
import java.util.Arrays;

public interface AllConstant {

    int STORAGE_REQUEST_CODE = 1000;
    int LOCATION_REQUEST_CODE = 2000;
    String IMAGE_PATH = "/Profile/image_profile.jpg";




    ArrayList<PlaceModel> placesName = new ArrayList<>(
            Arrays.asList(
                    new PlaceModel(1, R.drawable.ic_food, "Restaurant", "restaurant"),
                    new PlaceModel(2, R.drawable.ic_atm, "ATM", "atm"),
                    new PlaceModel(3, R.drawable.ic_gas, "Gas", "gas_station"),
                    new PlaceModel(4, R.drawable.ic_groceries, "Groceries", "supermarket"),
                    new PlaceModel(5, R.drawable.ic_hotel, "Hotels", "hotel"),
                    new PlaceModel(6, R.drawable.ic_attractions, "Attractions", "tourist_attraction"),
                    new PlaceModel(7, R.drawable.ic_parks, "Parks", "park"),
                    new PlaceModel(8, R.drawable.ic_museums, "Museums", "museum"),
                    new PlaceModel(9, R.drawable.ic_bars, "Bars", "bar")

            )
    );
}
