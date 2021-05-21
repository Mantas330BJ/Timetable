package sample;

public class Date {
    int year;
    int month;
    int dayOfMonth;
    Date (String date){
        String[] d = date.split(" ");
        this.year = Integer.parseInt(d[0]);
        this.month = Integer.parseInt(d[1]);
        this.dayOfMonth = Integer.parseInt(d[2]);
    }

    @Override
    public String toString() {
        return this.year + " " + this.month + " " + this.dayOfMonth;
    }
}
