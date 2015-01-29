package AIR.Common.Utilities;

public class MathUtils
{

  /*
   * This works like the Math.truncate in .NEt. 
   */
  //TODO Shiva: I looked but could not find any easy implementation.
  public static double truncate (double d)
  {
    if (d < 0)
      return -1 * Math.floor(d * -1);
    else
      return Math.floor (d);
  }
}
