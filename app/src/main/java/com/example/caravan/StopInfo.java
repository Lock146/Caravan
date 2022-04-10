package com.example.caravan;

public class StopInfo
{
    public GooglePlaceModel m_stop;
    public double m_distance;

    public StopInfo(GooglePlaceModel stop, double distance) {
        m_stop = stop;
        m_distance = distance;
    }

    public GooglePlaceModel stop()
    {
        return m_stop;
    }

    public double distance()
    {
        return m_distance;
    }
}