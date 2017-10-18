package com.sprayme.teamrsm.analyticspraydown.uicomponents;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by climbak on 10/17/17.
 */

public class EditTextBetterPaste extends EditText {
    private final Context context;

    /*
        Just the constructors to create a new EditText...
     */
    public EditTextBetterPaste(Context context) {
        super(context);
        this.context = context;
    }

    public EditTextBetterPaste(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public EditTextBetterPaste(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }
    static final int ID_PASTE = android.R.id.paste;
    static final int ID_PASTE_AS_PLAIN_TEXT = android.R.id.pasteAsPlainText;

    @Override
    public boolean onTextContextMenuItem(int id) {
        String text = this.getText().toString();
        int min = 0;
        int max = text.length();
        if (isFocused()) {
            final int selStart = getSelectionStart();
            final int selEnd = getSelectionEnd();
            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }
        switch (id) {
//            case ID_PASTE:
//                genericInvokMethod(this, "paste", 3, min, max, false);
////                paste(min, max, false /* withFormatting */);
//                return true;
//            case ID_PASTE_AS_PLAIN_TEXT:
//                genericInvokMethod(this, "paste", 3, min, max, false);
////                paste(min, max, false /* withFormatting */);
//                return true;
            default:
                return super.onTextContextMenuItem(id);
        }
    }

    private static Object genericInvokMethod(Object obj, String methodName,
                                            int paramCount, Object... params) {
        Method method;
        Object requiredObj = null;
        Object[] parameters = new Object[paramCount];
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            parameters[i] = params[i];
            classArray[i] = params[i].getClass();
        }
        try {
            System.out.println((obj.getClass().getSuperclass().getSuperclass().toString()));
            method = obj.getClass().getSuperclass().getSuperclass().getMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return requiredObj;
    }
        /**
         * Paste clipboard content between min and max positions.
         */
//    private void paste(int min, int max, boolean withFormatting) {
//        CharSequence s = this.getContentDescription();
//        ClipboardManager clipboard =
//                (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = clipboard.getPrimaryClip();
//        if (clip != null) {
//            boolean didFirst = false;
//            for (int i = 0; i < clip.getItemCount(); i++) {
//                final CharSequence paste;
//                if (withFormatting) {
//                    paste = clip.getItemAt(i).coerceToStyledText(getContext());
//                } else {
//                    // Get an item as text and remove all spans by toString().
//                    final CharSequence text = clip.getItemAt(i).coerceToText(getContext());
//                    paste = (text instanceof Spanned) ? text.toString() : text;
//                }
//                if (paste != null) {
//                    if (!didFirst) {
//                        Selection.setSelection((Spannable) s, max);
//                        ((Editable) s).replace(min, max, paste);
//                        didFirst = true;
//                    } else {
//                        ((Editable) s).insert(getSelectionEnd(), "\n");
//                        ((Editable) s).insert(getSelectionEnd(), paste);
//                    }
//                }
//                setText(s);
//            }
//        }
//    }
}
