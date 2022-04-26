package com.example.caravan;

public class MemberInfo
{
    private String memberName;
    private int profilePicture;

    public MemberInfo(String name, int picture)
    {
        memberName = name;
        profilePicture = picture;
    }

    public String memname() {
        return memberName;
    }

    public int picture()
    {
        return profilePicture;
    }

}
