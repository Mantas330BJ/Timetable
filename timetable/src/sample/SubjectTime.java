package sample;

public class SubjectTime {
    Date date;

    double startHour;
    double endHour;
    String stringified;

    SubjectTime (String date, String hour) {
        this.stringified = date + "*" + hour;
        this.date = new Date(date);
        String[] h = hour.split(" ");
        this.startHour = Double.parseDouble(h[0]);
        this.endHour = Double.parseDouble(h[1]);
    }

    @Override
    public String toString() {
        return this.stringified;
    }

}
