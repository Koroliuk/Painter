package com.koroliuk.painter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.koroliuk.painter.MainActivity;
import com.koroliuk.painter.R;

import java.util.Objects;
// Клас діалогового вікна створення растру
public class CreateDialog extends DialogFragment {

    public int height;
    public int width;
    public String color; // колір фону
    public Context context;
    public boolean isChanging;

    public CreateDialog(Context context, boolean isChanging) {
        super();
        this.context = context;
        this.isChanging = isChanging;
        color = "#FFFFFF";
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.create_dialog, null);
        LinearLayout layout = view.findViewById(R.id.color);
        layout.setOnClickListener(v -> {
            // Використання бібліотечного вікна створення власного кольору
            ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(context);
            colorPickerDialog.setPositiveActionText("Обрати");
            colorPickerDialog.setNegativeActionText("Назад");
            colorPickerDialog.setLastColor(color);
            colorPickerDialog.setInitialColor(Color.BLUE);
            colorPickerDialog.setSliderThumbColor(Color.RED);
            colorPickerDialog.setOnColorPickedListener((chosenColor, hexVal) -> {
                color = hexVal;
                layout.setBackgroundColor(Color.parseColor(hexVal));
            });
            colorPickerDialog.show();
        });
        EditText editTextHeight = view.findViewById(R.id.height);
        EditText editTextWidth = view.findViewById(R.id.width);
        builder.setTitle(R.string.create_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.create_dialog_positive_button, (dialog, which) -> {
                    try {
                        // Одержання даних
                        height = Integer.parseInt(String.valueOf(editTextHeight.getText()));
                        width = Integer.parseInt(String.valueOf(editTextWidth.getText()));
                        if (height <= 0 || width <= 0) {
                            throw new Exception();
                        }
                        MainActivity.enableChangeSize();
                        if (isChanging) {
                            MainActivity.changeSize(width, height, color);
                        } else {
                            MainActivity.createDrawingPlace(width, height, color, null);
                        }
                    } catch (Exception e) {
                        // Ігнорування винятків для забезпечення роботи функції зміни розміру растра
                        if (!(e.toString().equals("java.lang.IllegalArgumentException: x + width must be <= bitmap.width()") ||
                                e.toString().equals("java.lang.IllegalArgumentException: x + height must be <= bitmap.height()"))) {
                            Toast toast = Toast.makeText(context, "Перевірте коректність вхідних даних", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialog, which) -> dialog.cancel());
        return builder.create();
    }
}