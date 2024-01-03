package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;

import static android.provider.OpenableColumns.DISPLAY_NAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSession.PermissionDelegate.MediaSource;

public final class promptDelegate implements GeckoSession.PromptDelegate {

    private final AppCompatActivity mActivity;
    private GeckoResult<PromptResponse> mFileResponse;
    private FilePrompt mFilePrompt;

    static final String LOGTAG = "BasicGeckoViewPrompt";
    public int filePickerRequestCode = 115;

    public promptDelegate(final AppCompatActivity activity) {
        mActivity = activity;
    }

    public void stopMedia(){
        try {
            activityContextManager.getInstance().getHomeController().onKillMedia();
        }catch (Exception ex){}
    }

    @Override
    public GeckoResult<PromptResponse> onAlertPrompt(@NonNull final GeckoSession session,
                                                     @NonNull final AlertPrompt prompt) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(prompt.title)
                .setMessage(prompt.message)
                .setPositiveButton(android.R.string.ok, null);
        GeckoResult<PromptResponse> res = new GeckoResult<>();
        createStandardDialog(builder, prompt, res).show();
        return res;
    }

    @Override
    public GeckoResult<PromptResponse> onButtonPrompt(@NonNull final GeckoSession session,
                                                      @NonNull final ButtonPrompt prompt) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(prompt.title)
                .setMessage(prompt.message);

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        final DialogInterface.OnClickListener listener =
                (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        res.complete(prompt.confirm(ButtonPrompt.Type.POSITIVE));
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        res.complete(prompt.confirm(ButtonPrompt.Type.NEGATIVE));
                    } else {
                        res.complete(prompt.dismiss());
                    }
                };

        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);

        createStandardDialog(builder, prompt, res).show();
        return res;
    }

    static boolean mPopupOpened = false;
    @Override
    public GeckoResult<PromptResponse> onSharePrompt(@NonNull final GeckoSession session,
                                                     @NonNull final SharePrompt prompt) {
        stopMedia();
        new Thread(){
            public void run(){
                try {
                    sleep(4000);
                    mPopupOpened = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        if(!mPopupOpened){
            mPopupOpened = true;
        }else {
            return GeckoResult.fromValue(prompt.confirm(SharePrompt.Result.ABORT));
        }
        try {
            String m_data = prompt.uri;
            if(prompt.text!=null){
                m_data += " : " + prompt.text;
            }

            ShareCompat.IntentBuilder.from(mActivity)
                    .setType("text/x-uri")
                    .setChooserTitle(prompt.title)
                    .setText(m_data)
                    .startChooser();
        }catch (Exception ex){}

        return GeckoResult.fromValue(prompt.confirm(SharePrompt.Result.SUCCESS));

    }

    @Nullable
    @Override
    public GeckoResult<PromptResponse> onRepostConfirmPrompt(@NonNull final GeckoSession session,
                                                             @NonNull final RepostConfirmPrompt prompt) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Are you sure?")
                .setMessage("To display this page, GeckoViewExample must send information that will repeat any action (such as a search or order confirmation) that was performed earlier.");

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        final DialogInterface.OnClickListener listener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                res.complete(prompt.confirm(AllowOrDeny.ALLOW));
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                res.complete(prompt.confirm(AllowOrDeny.DENY));
            } else {
                res.complete(prompt.dismiss());
            }
        };

        builder.setPositiveButton("Resend", listener);
        builder.setNegativeButton("Cancel", listener);

        try {
            if(mActivity!=null && !mActivity.isFinishing())
            {
                createStandardDialog(builder, prompt, res).show();
            }
        }catch (Exception ex){}
        return res;
    }

    @Nullable
    @Override
    public GeckoResult<PromptResponse> onBeforeUnloadPrompt(@NonNull final GeckoSession session,
                                                            @NonNull final BeforeUnloadPrompt prompt) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Are you sure?")
                .setMessage("This page is asking you to confirm that you want to leave - data you have entered may not be saved");

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        final DialogInterface.OnClickListener listener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                res.complete(prompt.confirm(AllowOrDeny.ALLOW));
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                res.complete(prompt.confirm(AllowOrDeny.DENY));
            } else {
                res.complete(prompt.dismiss());
            }
        };

        builder.setPositiveButton("Leave Page", listener);
        builder.setNegativeButton("Stay on Page", listener);

        createStandardDialog(builder, prompt, res).show();
        return res;
    }

    private int getViewPadding(final AlertDialog.Builder builder) {
        stopMedia();
        try {
            final TypedArray attr = builder.getContext().obtainStyledAttributes(
                    new int[]{android.R.attr.listPreferredItemPaddingLeft});
            final int padding = attr.getDimensionPixelSize(0, 1);
            attr.recycle();
            return padding;
        }catch (Exception ex){}
        return 0;
    }

    private LinearLayout addStandardLayout(final AlertDialog.Builder builder,
                                           final String title, final String msg) {
        stopMedia();
        final ScrollView scrollView = new ScrollView(builder.getContext());
        final LinearLayout container = new LinearLayout(builder.getContext());
        final int horizontalPadding = getViewPadding(builder);
        final int verticalPadding = (msg == null || msg.isEmpty()) ? horizontalPadding : 0;
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(/* left */ horizontalPadding, /* top */ verticalPadding,
                /* right */ horizontalPadding, /* bottom */ verticalPadding);
        scrollView.addView(container);
        builder.setTitle(title)
                .setMessage(msg)
                .setView(scrollView);
        return container;
    }

    private AlertDialog createStandardDialog(final AlertDialog.Builder builder,
                                             final BasePrompt prompt,
                                             final GeckoResult<PromptResponse> response) {
        final AlertDialog dialog = builder.create();
        stopMedia();
        dialog.setOnDismissListener(dialog1 -> {
            if (!prompt.isComplete()) {
                response.complete(prompt.dismiss());
            }
        });
        return dialog;
    }

    @Override
    public GeckoResult<PromptResponse> onTextPrompt(@NonNull final GeckoSession session,
                                                    @NonNull final TextPrompt prompt) {
        final Activity activity = mActivity;
        stopMedia();
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LinearLayout container = addStandardLayout(builder, prompt.title, prompt.message);
        final EditText editText = new EditText(builder.getContext());
        editText.setText(prompt.defaultValue);
        container.addView(editText);

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        builder.setNegativeButton(android.R.string.cancel, /* listener */ null)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> res.complete(prompt.confirm(editText.getText().toString())));

        createStandardDialog(builder, prompt, res).show();
        return res;
    }

    @Override
    public GeckoResult<PromptResponse> onAuthPrompt(@NonNull final GeckoSession session,
                                                    @NonNull final AuthPrompt prompt) {
        final Activity activity = mActivity;
        stopMedia();
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LinearLayout container = addStandardLayout(builder, prompt.title, prompt.message);

        final int flags = prompt.authOptions.flags;
        final int level = prompt.authOptions.level;
        final EditText username;
        if ((flags & AuthPrompt.AuthOptions.Flags.ONLY_PASSWORD) == 0) {
            username = new EditText(builder.getContext());
            username.setHint("Username");
            username.setText(prompt.authOptions.username);
            container.addView(username);
        } else {
            username = null;
        }

        final EditText password = new EditText(builder.getContext());
        password.setHint("Password");
        password.setText(prompt.authOptions.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(password);

        if (level != AuthPrompt.AuthOptions.Level.NONE) {
            final ImageView secure = new ImageView(builder.getContext());
            secure.setImageResource(android.R.drawable.ic_lock_lock);
            container.addView(secure);
        }

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        builder.setNegativeButton(android.R.string.cancel, /* listener */ null)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> {
                            if ((flags & AuthPrompt.AuthOptions.Flags.ONLY_PASSWORD) == 0) {
                                res.complete(prompt.confirm(username.getText().toString(),
                                        password.getText().toString()));

                            } else {
                                res.complete(prompt.confirm(password.getText().toString()));
                            }
                        });
        createStandardDialog(builder, prompt, res).show();

        return res;
    }

    private static class ModifiableChoice {
        public boolean modifiableSelected;
        public String modifiableLabel;
        public final ChoicePrompt.Choice choice;

        public ModifiableChoice(ChoicePrompt.Choice c) {
            choice = c;
            modifiableSelected = choice.selected;
            modifiableLabel = choice.label;
        }
    }

    private void addChoiceItems(final int type, final ArrayAdapter<ModifiableChoice> list,
                                final ChoicePrompt.Choice[] items, final String indent) {
        stopMedia();
        if (type == ChoicePrompt.Type.MENU) {
            for (final ChoicePrompt.Choice item : items) {
                list.add(new ModifiableChoice(item));
            }
            return;
        }

        for (final ChoicePrompt.Choice item : items) {
            final ModifiableChoice modItem = new ModifiableChoice(item);

            final ChoicePrompt.Choice[] children = item.items;

            if (indent != null && children == null) {
                modItem.modifiableLabel = indent + modItem.modifiableLabel;
            }
            list.add(modItem);

            if (children != null) {
                final String newIndent;
                if (type == ChoicePrompt.Type.SINGLE || type == ChoicePrompt.Type.MULTIPLE) {
                    newIndent = (indent != null) ? indent + '\t' : "\t";
                } else {
                    newIndent = null;
                }
                addChoiceItems(type, list, children, newIndent);
            }
        }
    }

    private void onChoicePromptImpl(final String title,
                                    final String message, final int type,
                                    final ChoicePrompt.Choice[] choices, final ChoicePrompt prompt,
                                    final GeckoResult<PromptResponse> res) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            res.complete(prompt.dismiss());
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        addStandardLayout(builder, title, message);

        final ListView list = new ListView(builder.getContext());
        if (type == ChoicePrompt.Type.MULTIPLE) {
            list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }

        final ArrayAdapter<ModifiableChoice> adapter = new ArrayAdapter<ModifiableChoice>(
                builder.getContext(), android.R.layout.simple_list_item_1) {
            private static final int TYPE_MENU_ITEM = 0;
            private static final int TYPE_MENU_CHECK = 1;
            private static final int TYPE_SEPARATOR = 2;
            private static final int TYPE_GROUP = 3;
            private static final int TYPE_SINGLE = 4;
            private static final int TYPE_MULTIPLE = 5;
            private static final int TYPE_COUNT = 6;

            private LayoutInflater mInflater;
            private View mSeparator;

            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(final int position) {
                final ModifiableChoice item = getItem(position);
                if (item.choice.separator) {
                    return TYPE_SEPARATOR;
                } else if (type == ChoicePrompt.Type.MENU) {
                    return item.modifiableSelected ? TYPE_MENU_CHECK : TYPE_MENU_ITEM;
                } else if (item.choice.items != null) {
                    return TYPE_GROUP;
                } else if (type == ChoicePrompt.Type.SINGLE) {
                    return TYPE_SINGLE;
                } else if (type == ChoicePrompt.Type.MULTIPLE) {
                    return TYPE_MULTIPLE;
                } else {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public boolean isEnabled(final int position) {
                final ModifiableChoice item = getItem(position);
                return !item.choice.separator && !item.choice.disabled &&
                        ((type != ChoicePrompt.Type.SINGLE &&
                                type != ChoicePrompt.Type.MULTIPLE) ||
                                item.choice.items == null);
            }

            @Override
            public View getView(final int position, View view,
                                final ViewGroup parent) {
                final int itemType = getItemViewType(position);
                final int layoutId;
                if (itemType == TYPE_SEPARATOR) {
                    if (mSeparator == null) {
                        mSeparator = new View(getContext());
                        mSeparator.setLayoutParams(new ListView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 2, itemType));
                        final TypedArray attr = getContext().obtainStyledAttributes(
                                new int[]{android.R.attr.listDivider});
                        mSeparator.setBackgroundResource(attr.getResourceId(0, 0));
                        attr.recycle();
                    }
                    return mSeparator;
                } else if (itemType == TYPE_MENU_ITEM) {
                    layoutId = android.R.layout.simple_list_item_1;
                } else if (itemType == TYPE_MENU_CHECK) {
                    layoutId = android.R.layout.simple_list_item_checked;
                } else if (itemType == TYPE_GROUP) {
                    layoutId = android.R.layout.preference_category;
                } else if (itemType == TYPE_SINGLE) {
                    layoutId = android.R.layout.simple_list_item_single_choice;
                } else if (itemType == TYPE_MULTIPLE) {
                    layoutId = android.R.layout.simple_list_item_multiple_choice;
                } else {
                    throw new UnsupportedOperationException();
                }

                if (view == null) {
                    if (mInflater == null) {
                        mInflater = LayoutInflater.from(builder.getContext());
                    }
                    view = mInflater.inflate(layoutId, parent, false);
                }

                final ModifiableChoice item = getItem(position);
                final TextView text = (TextView) view;
                text.setEnabled(!item.choice.disabled);
                text.setText(item.modifiableLabel);
                if (view instanceof CheckedTextView) {
                    final boolean selected = item.modifiableSelected;
                    if (itemType == TYPE_MULTIPLE) {
                        list.setItemChecked(position, selected);
                    } else {
                        ((CheckedTextView) view).setChecked(selected);
                    }
                }
                return view;
            }
        };
        addChoiceItems(type, adapter, choices, /* indent */ null);

        list.setAdapter(adapter);
        builder.setView(list);

        final AlertDialog dialog;
        if (type == ChoicePrompt.Type.SINGLE || type == ChoicePrompt.Type.MENU) {
            dialog = createStandardDialog(builder, prompt, res);
            list.setOnItemClickListener((parent, v, position, id) -> {
                final ModifiableChoice item = adapter.getItem(position);
                if (type == ChoicePrompt.Type.MENU) {
                    final ChoicePrompt.Choice[] children = item.choice.items;
                    if (children != null) {
                        // Show sub-menu.
                        dialog.setOnDismissListener(null);
                        dialog.dismiss();
                        onChoicePromptImpl(item.modifiableLabel, /* msg */ null,
                                type, children, prompt, res);
                        return;
                    }
                }
                res.complete(prompt.confirm(item.choice));
                dialog.dismiss();
            });
        } else if (type == ChoicePrompt.Type.MULTIPLE) {
            list.setOnItemClickListener((parent, v, position, id) -> {
                final ModifiableChoice item = adapter.getItem(position);
                item.modifiableSelected = ((CheckedTextView) v).isChecked();
            });
            builder.setNegativeButton(android.R.string.cancel, /* listener */ null)
                    .setPositiveButton(android.R.string.ok,
                            (dialog1, which) -> {
                                final int len = adapter.getCount();
                                ArrayList<String> items = new ArrayList<>(len);
                                for (int i = 0; i < len; i++) {
                                    final ModifiableChoice item = adapter.getItem(i);
                                    if (item.modifiableSelected) {
                                        items.add(item.choice.id);
                                    }
                                }
                                res.complete(prompt.confirm(items.toArray(new String[0])));
                            });
            dialog = createStandardDialog(builder, prompt, res);
        } else {
            throw new UnsupportedOperationException();
        }
        try {
            if(mActivity!=null && !mActivity.isFinishing())
            {
                dialog.show();
            }
        }catch (Exception ex){}
    }

    @Override
    public GeckoResult<PromptResponse> onChoicePrompt(@NonNull final GeckoSession session,
                                                      @NonNull final ChoicePrompt prompt) {
        stopMedia();
        final GeckoResult<PromptResponse> res = new GeckoResult<>();
        onChoicePromptImpl(prompt.title, prompt.message, prompt.type, prompt.choices,
                prompt, res);
        return res;
    }

    private static int parseColor(final String value, final int def) {
        try {
            return Color.parseColor(value);
        } catch (final IllegalArgumentException e) {
            return def;
        }
    }

    @Override
    public GeckoResult<PromptResponse> onColorPrompt(@NonNull final GeckoSession session,
                                                     @NonNull final ColorPrompt prompt) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        addStandardLayout(builder, prompt.title, /* msg */ null);

        final int initial = parseColor(prompt.defaultValue, /* def */ 0);
        final ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
                builder.getContext(), android.R.layout.simple_list_item_1) {
            private LayoutInflater mInflater;

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public int getItemViewType(final int position) {
                return (getItem(position) == initial) ? 1 : 0;
            }

            @Override
            public View getView(final int position, View view,
                                final ViewGroup parent) {
                if (mInflater == null) {
                    mInflater = LayoutInflater.from(builder.getContext());
                }
                final int color = getItem(position);
                if (view == null) {
                    view = mInflater.inflate((color == initial) ?
                            android.R.layout.simple_list_item_checked :
                            android.R.layout.simple_list_item_1, parent, false);
                }
                view.setBackgroundResource(android.R.drawable.editbox_background);
                view.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                return view;
            }
        };

        adapter.addAll(0xffff4444 /* holo_red_light */,
                0xffcc0000 /* holo_red_dark */,
                0xffffbb33 /* holo_orange_light */,
                0xffff8800 /* holo_orange_dark */,
                0xff99cc00 /* holo_green_light */,
                0xff669900 /* holo_green_dark */,
                0xff33b5e5 /* holo_blue_light */,
                0xff0099cc /* holo_blue_dark */,
                0xffaa66cc /* holo_purple */,
                0xffffffff /* white */,
                0xffaaaaaa /* lighter_gray */,
                0xff555555 /* darker_gray */,
                0xff000000 /* black */);

        final ListView list = new ListView(builder.getContext());
        list.setAdapter(adapter);
        builder.setView(list);

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        final AlertDialog dialog = createStandardDialog(builder, prompt, res);
        list.setOnItemClickListener((parent, v, position, id) -> {
            res.complete(prompt.confirm(String.format("#%06x", 0xffffff & adapter.getItem(position))));
            dialog.dismiss();
        });
        dialog.show();

        return res;
    }

    private static Date parseDate(final SimpleDateFormat formatter,
                                  final String value,
                                  final boolean defaultToNow) {
        try {
            if (value != null && !value.isEmpty()) {
                return formatter.parse(value);
            }
        } catch (final ParseException e) {
        }
        return defaultToNow ? new Date() : null;
    }

    @SuppressWarnings("deprecation")
    private static void setTimePickerTime(final TimePicker picker, final Calendar cal) {
        if (Build.VERSION.SDK_INT >= 23) {
            picker.setHour(cal.get(Calendar.HOUR_OF_DAY));
            picker.setMinute(cal.get(Calendar.MINUTE));
        } else {
            picker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            picker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }
    }

    @SuppressWarnings("deprecation")
    private static void setCalendarTime(final Calendar cal, final TimePicker picker) {
        if (Build.VERSION.SDK_INT >= 23) {
            cal.set(Calendar.HOUR_OF_DAY, picker.getHour());
            cal.set(Calendar.MINUTE, picker.getMinute());
        } else {
            cal.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
            cal.set(Calendar.MINUTE, picker.getCurrentMinute());
        }
    }

    @Override
    public GeckoResult<PromptResponse> onDateTimePrompt(@NonNull final GeckoSession session,
                                                        @NonNull final DateTimePrompt prompt) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }
        final String format;
        if (prompt.type == DateTimePrompt.Type.DATE) {
            format = "yyyy-MM-dd";
        } else if (prompt.type == DateTimePrompt.Type.MONTH) {
            format = "yyyy-MM";
        } else if (prompt.type == DateTimePrompt.Type.WEEK) {
            format = "yyyy-'W'ww";
        } else if (prompt.type == DateTimePrompt.Type.TIME) {
            format = "HH:mm";
        } else if (prompt.type == DateTimePrompt.Type.DATETIME_LOCAL) {
            format = "yyyy-MM-dd'T'HH:mm";
        } else {
            throw new UnsupportedOperationException();
        }

        final SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ROOT);
        final Date minDate = parseDate(formatter, prompt.minValue, /* defaultToNow */ false);
        final Date maxDate = parseDate(formatter, prompt.maxValue, /* defaultToNow */ false);
        final Date date = parseDate(formatter, prompt.defaultValue, /* defaultToNow */ true);
        final Calendar cal = formatter.getCalendar();
        cal.setTime(date);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = LayoutInflater.from(builder.getContext());
        final DatePicker datePicker;
        if (prompt.type == DateTimePrompt.Type.DATE || prompt.type == DateTimePrompt.Type.MONTH ||
                prompt.type == DateTimePrompt.Type.WEEK || prompt.type == DateTimePrompt.Type.DATETIME_LOCAL) {
            @SuppressLint("DiscouragedApi") final int resId = builder.getContext().getResources().getIdentifier(
                    "date_picker_dialog", "layout", "android");
            DatePicker picker = null;
            if (resId != 0) {
                try {
                    picker = (DatePicker) inflater.inflate(resId, /* root */ null);
                } catch (final ClassCastException | InflateException e) {
                }
            }
            if (picker == null) {
                picker = new DatePicker(builder.getContext());
            }
            picker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), /* listener */ null);
            if (minDate != null) {
                picker.setMinDate(minDate.getTime());
            }
            if (maxDate != null) {
                picker.setMaxDate(maxDate.getTime());
            }
            datePicker = picker;
        } else {
            datePicker = null;
        }

        final TimePicker timePicker;
        if (prompt.type == DateTimePrompt.Type.TIME || prompt.type == DateTimePrompt.Type.DATETIME_LOCAL) {
            @SuppressLint("DiscouragedApi") final int resId = builder.getContext().getResources().getIdentifier(
                    "time_picker_dialog", "layout", "android");
            TimePicker picker = null;
            if (resId != 0) {
                try {
                    picker = (TimePicker) inflater.inflate(resId, /* root */ null);
                } catch (final ClassCastException | InflateException e) {
                }
            }
            if (picker == null) {
                picker = new TimePicker(builder.getContext());
            }
            setTimePickerTime(picker, cal);
            picker.setIs24HourView(DateFormat.is24HourFormat(builder.getContext()));
            timePicker = picker;
        } else {
            timePicker = null;
        }

        final LinearLayout container = addStandardLayout(builder, prompt.title, /* msg */ null);
        container.setPadding(/* left */ 0, /* top */ 0, /* right */ 0, /* bottom */ 0);
        if (datePicker != null) {
            container.addView(datePicker);
        }
        if (timePicker != null) {
            container.addView(timePicker);
        }

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        final DialogInterface.OnClickListener listener =
                (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_NEUTRAL) {
                        // Clear
                        res.complete(prompt.confirm(""));
                        return;
                    }
                    if (datePicker != null) {
                        cal.set(datePicker.getYear(), datePicker.getMonth(),
                                datePicker.getDayOfMonth());
                    }
                    if (timePicker != null) {
                        setCalendarTime(cal, timePicker);
                    }
                    res.complete(prompt.confirm(formatter.format(cal.getTime())));
                };
        builder.setNegativeButton(android.R.string.cancel, /* listener */ null)
                .setNeutralButton("Clear", listener)
                .setPositiveButton(android.R.string.ok, listener);
        createStandardDialog(builder, prompt, res).show();

        return res;
    }

    @Override
    public GeckoResult<PromptResponse> onFilePrompt(@NonNull GeckoSession session, @NonNull FilePrompt prompt) {
        stopMedia();
        final Activity activity = mActivity;
        if (activity == null) {
            return GeckoResult.fromValue(prompt.dismiss());
        }

        // Merge all given MIME types into one, using wildcard if needed.
        String mimeType = null;
        String mimeSubtype = null;
        if (prompt.mimeTypes != null) {
            for (final String rawType : prompt.mimeTypes) {
                final String normalizedType = rawType.trim().toLowerCase(Locale.ROOT);
                final int len = normalizedType.length();
                int slash = normalizedType.indexOf('/');
                if (slash < 0) {
                    slash = len;
                }
                final String newType = normalizedType.substring(0, slash);
                final String newSubtype = normalizedType.substring(Math.min(slash + 1, len));
                if (mimeType == null) {
                    mimeType = newType;
                } else if (!mimeType.equals(newType)) {
                    mimeType = "*";
                }
                if (mimeSubtype == null) {
                    mimeSubtype = newSubtype;
                } else if (!mimeSubtype.equals(newSubtype)) {
                    mimeSubtype = "*";
                }
            }
        }

        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType((mimeType != null ? mimeType : "*") + '/' +
                (mimeSubtype != null ? mimeSubtype : "*"));
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (prompt.type == FilePrompt.Type.MULTIPLE) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        if (prompt.mimeTypes.length > 0) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, prompt.mimeTypes);
        }

        GeckoResult<PromptResponse> res = new GeckoResult<>();

        try {
            mFileResponse = res;
            mFilePrompt = prompt;
            activity.startActivityForResult(intent, filePickerRequestCode);
        } catch (final ActivityNotFoundException e) {
            Log.e(LOGTAG, "Cannot launch activity", e);
            return GeckoResult.fromValue(prompt.dismiss());
        }

        return res;
    }

    @SuppressLint("Range")
    public void onFileCallbackResult(final int resultCode, final Intent data) {

        if (mFileResponse == null) {
            return;
        }

        final GeckoResult<PromptResponse> res = mFileResponse;
        mFileResponse = null;

        final FilePrompt prompt = mFilePrompt;
        mFilePrompt = null;

        if (resultCode != Activity.RESULT_OK || data == null) {
            res.complete(prompt.dismiss());
            return;
        }





        Uri mURI = data.getData();
        String result = null;
        if (mURI.getScheme().equals("content")) {
            try (Cursor cursor = mActivity.getContentResolver().query(mURI, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = mURI.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }


        File myFile = new File(mActivity.getFilesDir(),result);
        final ClipData clip = data.getClipData();

        try {
            @SuppressLint("Recycle") FileInputStream in = (FileInputStream) mActivity.getContentResolver().openInputStream(data.getData());
            FileOutputStream out = new FileOutputStream(myFile);
            FileChannel inChannel = in.getChannel();
            FileChannel outChannel = out.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            in.close();
            out.close();
        } catch (Exception e) {}

        final Uri uri = Uri.parse("file:///"+myFile.getAbsolutePath());

        try {
            if (prompt.type == FilePrompt.Type.SINGLE
                    || (prompt.type == FilePrompt.Type.MULTIPLE && clip == null)) {
                res.complete(prompt.confirm(mActivity, uri));
            } else if (prompt.type == FilePrompt.Type.MULTIPLE) {
                if (clip == null) {
                    Log.w(LOGTAG, "No selected file");
                    res.complete(prompt.dismiss());
                    return;
                }
                final int count = clip.getItemCount();
                final ArrayList<Uri> uris = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    uris.add(clip.getItemAt(i).getUri());
                }
                res.complete(prompt.confirm(mActivity, uris.toArray(new Uri[0])));
            }
        }catch (Exception ex){
        }
    }

    private Spinner addMediaSpinner(
            final Context context,
            final ViewGroup container,
            final MediaSource[] sources,
            final String[] sourceNames) {
        final ArrayAdapter<MediaSource> adapter =
                new ArrayAdapter<MediaSource>(context, android.R.layout.simple_spinner_item) {
                    private View convertView(final int position, final View view) {
                        if (view != null) {
                            final MediaSource item = getItem(position);
                            ((TextView) view).setText(sourceNames != null ? sourceNames[position] : item.name);
                        }
                        return view;
                    }

                    @Override
                    public View getView(final int position, View view, final ViewGroup parent) {
                        return convertView(position, super.getView(position, view, parent));
                    }

                    @Override
                    public View getDropDownView(final int position, final View view, @NonNull final ViewGroup parent) {
                        return convertView(position, super.getDropDownView(position, view, parent));
                    }
                };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(sources);

        final Spinner spinner = new Spinner(context);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        container.addView(spinner);
        return spinner;
    }

    public void onMediaPrompt(
            final GeckoSession session,
            final String title,
            final MediaSource[] video,
            final MediaSource[] audio,
            final String[] videoNames,
            final String[] audioNames,
            final GeckoSession.PermissionDelegate.MediaCallback callback) {
        final Activity activity = mActivity;
        if (activity == null || (video == null && audio == null)) {
            callback.reject();
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LinearLayout container = addStandardLayout(builder, title, /* msg */ null);

        final Spinner videoSpinner;
        if (video != null) {
            videoSpinner = addMediaSpinner(builder.getContext(), container, video, videoNames);
        } else {
            videoSpinner = null;
        }

        final Spinner audioSpinner;
        if (audio != null) {
            audioSpinner = addMediaSpinner(builder.getContext(), container, audio, audioNames);
        } else {
            audioSpinner = null;
        }

        builder
                .setNegativeButton(android.R.string.cancel, /* listener */ null)
                .setPositiveButton(
                        android.R.string.ok,
                        (dialog, which) -> {
                            final MediaSource video1 =
                                    (videoSpinner != null) ? (MediaSource) videoSpinner.getSelectedItem() : null;
                            final MediaSource audio1 =
                                    (audioSpinner != null) ? (MediaSource) audioSpinner.getSelectedItem() : null;
                            callback.grant(video1, audio1);
                        });

        final AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(
                dialog1 -> callback.reject());
        dialog.show();
    }

    @Override
    public GeckoResult<PromptResponse> onPopupPrompt(
            @NonNull final GeckoSession session, final PopupPrompt prompt) {
        return GeckoResult.fromValue(prompt.confirm(AllowOrDeny.ALLOW));
    }
}