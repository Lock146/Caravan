package com.example.caravan;

import androidx.annotation.DrawableRes;

public class MemberInfo
{
    String memberName;
    @DrawableRes int profilePicture;


    public MemberInfo(String name, @DrawableRes int picture)
    {
        memberName = name;
        profilePicture = picture;
    }

    public String getMemberName() {
        return memberName;
    }

    public @DrawableRes int getProfilePicture()
    {
        return profilePicture;
    }

}