package com.example.caravantest.Constant;

import com.example.caravantest.PlaceModel;
import com.example.caravantest.R;

import java.util.ArrayList;
import java.util.Arrays;

public interface AllConstant {

    int STORAGE_REQUEST_CODE = 1000;
    int LOCATION_REQUEST_CODE = 2000;
    String IMAGE_PATH = "/Profile/image_profile.jpg";


    ArrayList<PlaceModel> placesName = new ArrayList<>(
            Arrays.asList(
                    new PlaceModel(1, R.drawable.ic_gas_station, "Restaurant", "restaurant"),
                    new PlaceModel(2, R.drawable.ic_gas_station, "ATM", "atm"),
                    new PlaceModel(3, R.drawable.ic_gas_station, "Gas", "gas_station"),
                    new PlaceModel(4, R.drawable.ic_gas_station, "Groceries", "supermarket"),
                    new PlaceModel(5, R.drawable.ic_gas_station, "Hotels", "hotel"),
                    new PlaceModel(6, R.drawable.ic_gas_station, "Attractions", "tourist_attraction"),
                    new PlaceModel(7, R.drawable.ic_gas_station, "Parks", "park"),
                    new PlaceModel(8, R.drawable.ic_gas_station, "Museums", "museum"),
                    new PlaceModel(9, R.drawable.ic_gas_station, "Bars", "bar")

            )
    );
}
