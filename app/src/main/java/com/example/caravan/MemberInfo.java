package com.example.caravan;

import androidx.annotation.DrawableRes;

public class MemberInfo
{
    private String m_memberName;
    private String m_profilePicture;


    public MemberInfo(String name, String profilePicture)
    {
        m_memberName = name;
        m_profilePicture = profilePicture;
    }

    public String getMemberName() {
        return m_memberName;
    }

    public String getProfilePicture()
    {
        return m_profilePicture;
    }

}