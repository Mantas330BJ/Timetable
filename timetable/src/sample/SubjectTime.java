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

    String getTime() {
        int startH = (int)startHour, startM = (int)((startHour - startH) * 60), endH = (int)endHour, endM = (int)((endHour - endH) * 60);
        String startMins = "", endMins = "";
        if (startM < 10)
            startMins += "0";
        startMins += Integer.toString(startM);
        if (endM < 10)
            endMins += "0";
        endMins += Integer.toString(endM);

        return startH + ":" + startMins + " - " + endH + ":" + endMins;
    }

    @Override
    public String toString() {
        return this.stringified;
    }

}
