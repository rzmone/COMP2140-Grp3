package com.groupthree.sims;

public class App 
{
    private static User loggedInUser;

    public static String getLoggedInUsername()
    {
        return loggedInUser.getUsername();
    }

    public static int getLoggedInUserId()
    {
        return loggedInUser.getId();
    }

    public static void main( String[] args )
    {

    }
}