package com.example.caravan;

public interface DestinationInterface {

    void onLocationClick(DestinationModel destinationModel);
    void onLocationClick2(GooglePlaceModel googlePlaceModel);
    void onConfirmationClick(DestinationModel destinationModel);

    void onStartClick();
    //void onStartClick();
}
