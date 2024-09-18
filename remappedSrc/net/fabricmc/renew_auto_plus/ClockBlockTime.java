package net.fabricmc.renew_auto_plus;

import net.minecraft.nbt.NbtCompound;

public class ClockBlockTime {
    public int hours;
    public int minutes;
    public int seconds;
    public int twentieths;

    ClockBlockTime() {
        hours = 0;
        minutes = 0;
        seconds = 0;
        twentieths = 0;
    }

    ClockBlockTime(int inHours, int inMinutes, int inSeconds, int inTwentieths) {
        hours = inHours;
        minutes = inMinutes;
        seconds = inSeconds;
        twentieths = inTwentieths;
    }

    public void readNbt(String name, NbtCompound nbt) {
        this.hours = nbt.getShort(name + "Hours");
        this.minutes = nbt.getShort(name + "Minutes");
        this.seconds = nbt.getShort(name + "Seconds");
        this.twentieths = nbt.getShort(name + "Twentieths");
    }
  
    public void writeNbt(String name, NbtCompound nbt) {
        nbt.putShort(name + "Hours", (short)this.hours);
        nbt.putShort(name + "Minutes", (short)this.minutes);
        nbt.putShort(name + "Seconds", (short)this.seconds);
        nbt.putShort(name + "Twentieths", (short)this.twentieths);
    }

    public void setTo(ClockBlockTime other) {
        hours = other.hours;
        minutes = other.minutes;
        seconds = other.seconds;
        twentieths = other.twentieths;
    }

    public void setHours(int time) {
        if(time > 99) {
            hours = 0;
        }
        else if(time < 0) {
            hours = 99;
        }
        else {
            hours = time;
        }
    }

    public void setMinutes(int time) {
        if(time >= 60) {
            minutes = 0;
            setHours(hours + 1);
        }
        else if(time < 0) {
            minutes = 59;
        }
        else {
            minutes = time;
        }
    }

    public void setSeconds(int time) {
        if(time >= 60) {
            seconds = 0;
            setMinutes(minutes + 1);
        }
        else if(time < 0) {
            seconds = 59;
        }
        else {
            seconds = time;
        }
    }

    public void setTwentieths(int time) {
        if(time >= 20) {
            twentieths = 0;
            setSeconds(seconds + 1);
        }
        else if(time < 0) {
            twentieths = 19;
        }
        else {
            twentieths = time;
        }
    }

    public boolean isZero() {
        return hours <= 0 && minutes <= 0 && seconds <= 0 && twentieths <= 0;
    }

    public void tickDown() {
        if(isZero()) {
            return;
        }
        if(twentieths == 0) {
            if(seconds == 0) {
                if(minutes == 0) {
                    hours = hours - 1;
                    minutes = 60;
                }
                minutes = minutes - 1;
                seconds = 60;
            }
            seconds = seconds - 1;
            twentieths = 20;
        }
        twentieths = twentieths - 1;
    }
}
