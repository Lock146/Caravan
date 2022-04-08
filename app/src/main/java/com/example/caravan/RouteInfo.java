package com.example.caravan;

public class RouteInfo
{
    public String routeName;
    public double routeMeters;

    public RouteInfo(String Name, double Meters) {
        routeName = Name;
        routeMeters = Meters;
    }


    public String getRouteName()
    {
        return routeName;
    }

    public double getRouteMeters()
    {
        return routeMeters;
    }
}