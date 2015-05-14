package mx.com.factico.diputinder.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.typeface.TypefaceFactory;

public class CustomTextView extends TextView {
    protected String TAG_CLASS = CustomTextView.class.getSimpleName();

    public CustomTextView(Context context) {
		super(context);
		if (!isInEditMode())
			init(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode())
			init(context, attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (!isInEditMode())
			init(context, attrs);
	}
	
	public void init(Context context) {
		isInEditMode();
	}
	
	public void init(Context context, AttributeSet attrs) {
		isInEditMode();
		
		if (attrs == null || getContext() == null) {
            return;
        }
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);

		if (typedArray == null) {
            return;
        }
		
		final int N = typedArray.getIndexCount();
		for (int i = 0; i < N; ++i) {
			int attr = typedArray.getIndex(i);
			switch (attr) {
			case R.styleable.CustomTextView_typeface:
				int type = Integer.parseInt(typedArray.getString(attr));
				
				Typeface typeface = TypefaceFactory.createTypeface(context, type);
				setTypeface(typeface);
				
				break;
			}
		}
		typedArray.recycle();
	}
}