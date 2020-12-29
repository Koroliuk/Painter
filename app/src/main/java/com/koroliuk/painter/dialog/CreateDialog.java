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

public class CreateDialog extends DialogFragment {

    public int height;
    public int width;
    public String color;
    public Context context;
    public boolean isChanging;

    public CreateDialog(Context context) {
        super();
        this.context = context;
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
                        height = Integer.parseInt(String.valueOf(editTextHeight.getText()));
                        width = Integer.parseInt(String.valueOf(editTextWidth.getText()));
                        MainActivity.createDrawingPlace(width, height, color);
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(context, "Перевірте коректність вхідних даних", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialog, which) -> dialog.cancel());
        return builder.create();
    }
}