package com.example.caravan;

import androidx.annotation.DrawableRes;

import java.util.HashMap;

public class MemberInfo
{
    private String m_memberName;
    private String m_profilePicture;


    public MemberInfo(String name, String profilePicture)
    {
        m_memberName = name;
        m_profilePicture = profilePicture;
    }

    public MemberInfo() {
            m_memberName = "";
            m_profilePicture = "";
    }

    public String getMemberName() {
        return m_memberName;
    }

    public String getProfilePicture()
    {
        return m_profilePicture;
    }

    public void setMemberName(String name)
    {
        m_memberName = name;
    }

    public void setProfilePicture(String pic)
    {
        m_profilePicture = pic;
    }

    public static MemberInfo get_member_info(HashMap<String, Object> hashedStop){
        MemberInfo info = new MemberInfo();
        info.setMemberName("Name");
        info.setProfilePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Cat.png?alt=media&token=072603f3-b678-4209-98e8-47dc4bc92850");
        return info;
    }


    public static MemberInfo getMemberInfo()
    {
        MemberInfo member = new MemberInfo(MemberInfo.getMemberInfo().m_memberName, MemberInfo.getMemberInfo().m_profilePicture);
        return member;
    }
}