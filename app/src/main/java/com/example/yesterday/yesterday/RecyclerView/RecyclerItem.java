package com.example.yesterday.yesterday.RecyclerView;

public class RecyclerItem {

    String userID;
    String food;
    String count;
    String startDate;
    String endDate;
    String favorite;

    //count 랑 favorite DB에는 int로  정의 되어있음

    //소멸 예정
    public String text;

   // public String date;

    public RecyclerItem(String userID,String food,String count,String startDate,String endDate,String favorite,String text){
        this.userID = userID;
        this.food = food;
        this.count = count;
        this.startDate = startDate;
        this.endDate = endDate;
        this.favorite = favorite;
        //
        this.text = text;
    }

    public String getUserID(){ return userID; }
    public String getFood(){ return food; }
    public String getCount(){ return count; }
    public String getStartDate(){ return startDate; }
    public String getEndDate(){ return endDate; }
    public String getFavorite(){ return favorite; }
    //
    public String getText(){
        return text;
    }

}
