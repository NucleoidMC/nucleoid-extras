package xyz.nucleoid.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Calendar;
import java.util.GregorianCalendar;

public record WrappedEvent(
    int year,
    GregorianCalendar start,
    GregorianCalendar end
) {
    @SuppressWarnings("MagicConstant")
    public static final Codec<GregorianCalendar> CALENDAR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("year").forGetter(cal -> cal.get(Calendar.YEAR)),
        Codec.INT.fieldOf("month").forGetter(cal -> cal.get(Calendar.MONTH) + 1),
        Codec.INT.fieldOf("date").forGetter(cal -> cal.get(Calendar.DAY_OF_MONTH))
    ).apply(instance, (year, month, dayOfMonth) -> new GregorianCalendar(year, month - 1, dayOfMonth)));

    public static final Codec<WrappedEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("year").forGetter(WrappedEvent::year),
        CALENDAR_CODEC.fieldOf("start").forGetter(WrappedEvent::start),
        CALENDAR_CODEC.fieldOf("end").forGetter(WrappedEvent::end)
    ).apply(instance, WrappedEvent::new));

    public boolean isDuring(Calendar cal) {
        return cal.equals(this.start) || cal.equals(this.end) || (cal.after(this.start) && cal.before(this.end));
    }
}
