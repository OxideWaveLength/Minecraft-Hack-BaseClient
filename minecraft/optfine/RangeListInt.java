package optfine;

public class RangeListInt
{
    private RangeInt[] ranges = new RangeInt[0];

    public void addRange(RangeInt p_addRange_1_)
    {
        this.ranges = (RangeInt[])((RangeInt[])Config.addObjectToArray(this.ranges, p_addRange_1_));
    }

    public boolean isInRange(int p_isInRange_1_)
    {
        for (int i = 0; i < this.ranges.length; ++i)
        {
            RangeInt rangeint = this.ranges[i];

            if (rangeint.isInRange(p_isInRange_1_))
            {
                return true;
            }
        }

        return false;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("[");

        for (int i = 0; i < this.ranges.length; ++i)
        {
            RangeInt rangeint = this.ranges[i];

            if (i > 0)
            {
                stringbuffer.append(", ");
            }

            stringbuffer.append(rangeint.toString());
        }

        stringbuffer.append("]");
        return stringbuffer.toString();
    }
}
