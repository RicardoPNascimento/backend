package com.simbioff.simbioff.dto;

import java.io.Serializable;

public class DaysLeftDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double workingDays;
    private Double dayOffRequestds;
    private Double daysOffRemmaing;

    public Double getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Double workingDays) {
        this.workingDays = workingDays;
    }

    public Double getDayOffRequestds() {
        return dayOffRequestds;
    }

    public void setDayOffRequestds(Double dayOffRequestds) {
        this.dayOffRequestds = dayOffRequestds;
    }

    public Double getDaysOffRemmaing() {
        return daysOffRemmaing;
    }

    public void setDaysOffRemmaing(Double daysOffRemmaing) {
        this.daysOffRemmaing = daysOffRemmaing;
    }
}
