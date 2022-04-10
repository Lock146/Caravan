package com.example.caravan;

public class StopInfo
{
    public String m_stop;
    public double m_distance;

    public StopInfo(String Name, double Meters) {
        m_stop = Name;
        m_distance = Meters;
    }


    public String getRouteName()
    {
        return m_stop;
    }

    public double getRouteMeters()
    {
        return m_distance;
    }
}