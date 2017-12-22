package fnn.smirl.ttsview;
import java.util.*;

public class Language implements Comparable<Language>
{
  public final String name;
  public final Locale locale;

  public Language(Locale locale) {
	this.locale = locale;
	name = locale.getDisplayCountry();
  }

  @Override
  public String toString() {
	// TODO: Implement this method
	return name;
  }

  @Override
  public int compareTo(Language p1) {
	// TODO: Implement this method
	return name.compareToIgnoreCase(p1.name);
  }

  @Override
  public boolean equals(Object o) {
	// TODO: Implement this method
	return name.compareToIgnoreCase(((Language)o).name) == 0;
  }
}
