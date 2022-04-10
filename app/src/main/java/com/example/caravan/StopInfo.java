package com.example.caravan;

public class StopInfo
{
    private GooglePlaceModel m_stop;
    private double m_distance;

    public StopInfo(GooglePlaceModel stop, double distance) {
        m_stop = stop;
        m_distance = distance;
    }

    public GooglePlaceModel stop()
    {
        return m_stop;
    }

    public String name()
    {
        return m_stop.getName();
    }

    public double distance()
    {
        return m_distance;
    }
}