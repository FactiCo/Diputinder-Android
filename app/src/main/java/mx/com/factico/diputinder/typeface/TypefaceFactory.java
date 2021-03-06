package mx.com.factico.diputinder.typeface;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceFactory {
    public static final int Gotham_Rounded_Bold = 0;
	public static final int Gotham_Rounded_Book = 1;
	
	private static String typefaceDirGothamRounded = "fonts/GothamRounded/";
	
	public static Typeface createTypeface(Context context, int type) {
		Typeface typeface;
		if (type == Gotham_Rounded_Bold) {
			typeface = Typeface.createFromAsset(context.getAssets(), typefaceDirGothamRounded + "Gotham-Rounded-Bold.ttf");
			return typeface;
		} else if (type == Gotham_Rounded_Book) {
			typeface = Typeface.createFromAsset(context.getAssets(), typefaceDirGothamRounded + "Gotham-Rounded-Book.ttf");
			return typeface;
		} else {
			typeface = Typeface.createFromAsset(context.getAssets(), typefaceDirGothamRounded + "Gotham-Rounded-Bold.ttf");
			return typeface;
		}
	}
}
