package perso.edt1;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Comparator;

public class CalendarUtils
{
    public static LocalDate selectedDate;

    public static ArrayList<Event> selectedDateEvents;

    public static String formattedDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String formattedTime(LocalTime time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return time.format(formatter);
    }

    public static String formattedShortTime(LocalTime time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static String formattedHours(LocalTime time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
        return time.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static String monthDayFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM");
        return date.format(formatter);
    }

    public static String upperCaseWords(String input) {
        String[] words = input.split(" "); // Split the input string by space

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String restOfWord = word.substring(1);
                result.append(firstChar).append(restOfWord).append(" ");
            }
        }
        return result.toString().trim();
    }

    public static ArrayList<LocalDate> daysInMonthArray()
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);

        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek)
                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(),prevMonth.getMonth(),prevDaysInMonth + i - dayOfWeek));
            else if(i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(),nextMonth.getMonth(),i - dayOfWeek - daysInMonth));
            else
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i - dayOfWeek));
        }
        return  daysInMonthArray;
    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate)
    {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate))
        {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    static LocalDate sundayForDate(LocalDate current)
    {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo))
        {
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }


    public static Event getNextEvent(Event event, Boolean dayOnly) {

        if(selectedDateEvents == null || selectedDateEvents.isEmpty() || !Objects.equals(selectedDateEvents.get(0).getDate(), selectedDate)) {

            selectedDateEvents = Event.EventsByDay.get(selectedDate);

            Comparator<Event> eventComparator = Comparator.comparing(Event::getStartTime);
            selectedDateEvents.sort(eventComparator);
        }

        Event currentEvent = null;

        for(int i = 0; i < selectedDateEvents.size(); i++) {
            currentEvent = selectedDateEvents.get(i);

            if (!Objects.equals(currentEvent.getCategory(), "Fill")) {

                if (Objects.equals(currentEvent.getCategory(), event.getCategory()) && Objects.equals(event.getStartTime(), currentEvent.getStartTime()) && Objects.equals(event.getGroup(), currentEvent.getGroup())) {
                    if (i < selectedDateEvents.size() - 1) {
                        if(selectedDateEvents.get(i + 1).getCategory().equals("Fill")) {
                            if (i < selectedDateEvents.size() - 2) {
                                return selectedDateEvents.get(i + 2);
                            }
                        } else {
                            return selectedDateEvents.get(i + 1);
                        }
                    }
                }
            }
        }
        if(dayOnly) {
            return null;
        } else {
            int nbTries = 0;
            while(nbTries < 20) {
                nbTries++;
                selectedDate = event.getDate().plusDays(1);
                selectedDateEvents = Event.EventsByDay.get(selectedDate);
                if(selectedDateEvents != null && !selectedDateEvents.isEmpty()) {
                    for(int i = 0; i < selectedDateEvents.size(); i++) {
                        currentEvent = selectedDateEvents.get(i);
                        if (!Objects.equals(currentEvent.getCategory(), "Fill")) {
                            return selectedDateEvents.get(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Event getPreviousEvent(Event event) {

            if(selectedDateEvents == null || selectedDateEvents.isEmpty() || !Objects.equals(selectedDateEvents.get(0).getDate(), selectedDate)) {

                selectedDateEvents = Event.EventsByDay.get(selectedDate);

                Comparator<Event> eventComparator = Comparator.comparing(Event::getStartTime);
                selectedDateEvents.sort(eventComparator);
            }

            Event currentEvent = null;

            for(int i = 0; i < selectedDateEvents.size(); i++) {
                currentEvent = selectedDateEvents.get(i);

                if (!Objects.equals(currentEvent.getCategory(), "Fill")) {

                    if (Objects.equals(currentEvent.getCategory(), event.getCategory()) && Objects.equals(event.getStartTime(), currentEvent.getStartTime()) && Objects.equals(event.getGroup(), currentEvent.getGroup())) {
                        if (i > 0) {
                            if(selectedDateEvents.get(i - 1).getCategory().equals("Fill")) {
                                if (i > 1) {
                                    return selectedDateEvents.get(i - 2);
                                }
                            } else {
                                return selectedDateEvents.get(i - 1);
                            }
                        }
                    }
                }
            }

            return null;
    }
}
