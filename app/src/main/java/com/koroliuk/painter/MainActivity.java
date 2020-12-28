package com.koroliuk.painter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.koroliuk.painter.editor.PainterView;
import com.koroliuk.painter.editor.drawings.Shape;
import com.koroliuk.painter.scrolling.MyHorizontalScrollView;
import com.koroliuk.painter.scrolling.MyScrollView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static PainterView painterView;
    private Uri selectedImage;
    private Menu menu;
    private boolean isToolbarShowed;
    private View tableView;
    private LinearLayout mainLayout;
    private ArrayList<ImageButton> imageButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        painterView = findViewById(R.id.painter_view);
        PainterView.context = this;
        isToolbarShowed = true;
        LayoutInflater inflater = getLayoutInflater();
        mainLayout = findViewById(R.id.main_linear);
        mainLayout.removeView(painterView);
        tableView = inflater.inflate(R.layout.toolbar, mainLayout, false);
        mainLayout.addView(tableView);
        mainLayout.addView(painterView);
        setToolBar();
        painterView.scrollView = findViewById(R.id.scroll);
        painterView.horizontalScrollView = findViewById(R.id.scroll_hor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.create:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                LayoutInflater inflater1 = Objects.requireNonNull(getLayoutInflater());
                View view1 = inflater1.inflate(R.layout.create_dialog, null);
                EditText editTextHeight = view1.findViewById(R.id.height);
                EditText editTextWidth = view1.findViewById(R.id.width);
                builder1.setTitle(R.string.create_dialog_title)
                        .setView(view1)
                        .setPositiveButton(R.string.create_dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int height = Integer.parseInt(String.valueOf(editTextHeight.getText()));
                                    int width = Integer.parseInt(String.valueOf(editTextWidth.getText()));
                                    painterView.bitmap = null;
                                    painterView.imageBitmap = null;
                                    painterView.canvas = null;
                                    painterView.showedShapes = new ArrayList<>();
                                    painterView.isDrawing = false;
                                    createDrawingPlace(width, height);
                                    enableChangeSize(menu);
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(MainActivity.this, "Перевірте коректність вхідних даних", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder1.create();
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                break;
            case R.id.open:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                enableChangeSize(menu);
                enableSave(menu);
                break;
            case R.id.save:
                try {
                    painterView.saveFile(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.save_as_png:
                if (isExternalStorageWritable()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    LayoutInflater inflater2 = Objects.requireNonNull(getLayoutInflater());
                    View view2 = inflater2.inflate(R.layout.save_dialog, null);
                    final String[] name = new String[1];
                    EditText editText2 = view2.findViewById(R.id.save_dialog);
                    builder2.setTitle(R.string.save_dialog_title)
                            .setView(view2)
                            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        name[0] = String.valueOf(editText2.getText());
                                        String path = painterView.saveFileAsPNG(name[0]);
                                        Toast toast = Toast.makeText(MainActivity.this, "Файл збережено " + path, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } catch (Exception e) {
                                        Toast toast = Toast.makeText(MainActivity.this, "Перевірте коректність даних", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Немає дозволу", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            case R.id.change:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                LayoutInflater inflater3 = Objects.requireNonNull(getLayoutInflater());
                View view3 = inflater3.inflate(R.layout.create_dialog, null);
                EditText editTextHeight3 = view3.findViewById(R.id.height);
                EditText editTextWidth3 = view3.findViewById(R.id.width);
                builder3.setTitle(R.string.create_dialog_title)
                        .setView(view3)
                        .setPositiveButton(R.string.create_dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int height = Integer.parseInt(String.valueOf(editTextHeight3.getText()));
                                    int width = Integer.parseInt(String.valueOf(editTextWidth3.getText()));
                                    painterView.changeSize(width, height);
                                    enableChangeSize(menu);
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(MainActivity.this, "Перевірте коректність вхідних даних", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder3.create();
                AlertDialog dialog3 = builder3.create();
                dialog3.show();
                break;
            case R.id.exit:
                finish();
            case R.id.toolbar:
                if (isToolbarShowed) {
                    mainLayout.removeView(tableView);
                } else {
                    mainLayout.removeView(painterView);
                    mainLayout.addView(tableView);
                    mainLayout.addView(painterView);
                }
                isToolbarShowed = !isToolbarShowed;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enableSave(Menu menu) {
        MenuItem changeSizeMenuItem = menu.findItem(R.id.save);
        changeSizeMenuItem.setEnabled(true);
    }

    private void enableChangeSize(Menu menu) {
        MenuItem changeSizeMenuItem = menu.findItem(R.id.change);
        changeSizeMenuItem.setEnabled(true);
    }

    public static void createDrawingPlace(int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        painterView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            enableSave(menu);
            selectedImage = data.getData();
            try {
                painterView.bitmap = null;
                painterView.imageBitmap = null;
                painterView.canvas = null;
                painterView.showedShapes = new ArrayList<>();
                painterView.isDrawing = false;
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                painterView.imageBitmap = imageBitmap;
                createDrawingPlace(imageBitmap.getWidth(), imageBitmap.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void  setToolBar() {
        ImageButton buttonThick = findViewById(R.id.thickness);
        ImageButton buttonSetFilled = findViewById(R.id.filled);
        ImageButton buttonBrush = findViewById(R.id.brush);
        ImageButton buttonLine = findViewById(R.id.line);
        ImageButton buttonRect = findViewById(R.id.rect);
        ImageButton buttonOval = findViewById(R.id.oval);
        ImageButton buttonCube = findViewById(R.id.cube);
        ImageButton buttonErasor = findViewById(R.id.erasor);
        imageButtons = new ArrayList<>();
        imageButtons.add(buttonBrush);
        imageButtons.add(buttonLine);
        imageButtons.add(buttonRect);
        imageButtons.add(buttonOval);
        imageButtons.add(buttonCube);
        imageButtons.add(buttonErasor);
        buttonThick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = Objects.requireNonNull(getLayoutInflater());
                View view = inflater.inflate(R.layout.thickness_dialog, null);
                EditText editTextWidth = view.findViewById(R.id.get_width);
                builder.setTitle(R.string.get_width_title)
                        .setView(view)
                        .setPositiveButton(R.string.create_dialog_positive_button, (dialog, which) -> {
                            try {
                                painterView.width = Integer.parseInt(String.valueOf(editTextWidth.getText()));
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(MainActivity.this, "Перевірте коректність вхідних даних", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        })
                        .setNegativeButton(R.string.dialog_negative_button, (dialog, which) -> dialog.cancel());
                builder.create();
                AlertDialog dialog1 = builder.create();
                dialog1.show();
            }
        });
        buttonThick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Товщина", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        buttonSetFilled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                painterView.isFilled = !painterView.isFilled;
                if (painterView.isFilled) {
                    buttonSetFilled.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                } else {
                    buttonSetFilled.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                }

            }
        });
        buttonSetFilled.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Заповнення", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        buttonBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (painterView.selectedType == 1) {
                    painterView.selectedType = 0;
                    painterView.scrollView.setEnableScrolling(true);
                    painterView.horizontalScrollView.setEnableScrolling(true);
                    buttonBrush.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

                } else {
                    painterView.scrollView.setEnableScrolling(false);
                    painterView.horizontalScrollView.setEnableScrolling(false);
                    optionOn(buttonBrush);
                    painterView.selectedType = 1;
                    painterView.start(1);
                }
            }
        });
        buttonBrush.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Пензлик", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        buttonLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (painterView.selectedType == 2) {
                    painterView.selectedType = 0;
                    painterView.scrollView.setEnableScrolling(true);
                    painterView.horizontalScrollView.setEnableScrolling(true);
                    buttonLine.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

                } else {
                    painterView.scrollView.setEnableScrolling(false);
                    painterView.horizontalScrollView.setEnableScrolling(false);
                    optionOn(buttonLine);
                    painterView.selectedType = 2;
                    painterView.start(2);
                }
            }
        });
        buttonLine.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Лінія", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        buttonRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (painterView.selectedType == 3) {
                    painterView.selectedType = 0;
                    painterView.scrollView.setEnableScrolling(true);
                    painterView.horizontalScrollView.setEnableScrolling(true);
                    buttonRect.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

                } else {
                    painterView.scrollView.setEnableScrolling(false);
                    painterView.horizontalScrollView.setEnableScrolling(false);
                    optionOn(buttonRect);
                    painterView.selectedType = 3;
                    painterView.start(3);
                }
            }
        });
        buttonRect.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Прямокутник", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        buttonOval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (painterView.selectedType == 4) {
                    painterView.selectedType = 0;
                    painterView.scrollView.setEnableScrolling(true);
                    painterView.horizontalScrollView.setEnableScrolling(true);
                    buttonOval.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

                } else {
                    painterView.scrollView.setEnableScrolling(false);
                    painterView.horizontalScrollView.setEnableScrolling(false);
                    optionOn(buttonOval);
                    painterView.selectedType = 4;
                    painterView.start(4);
                }
            }
        });
        buttonOval.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Овал", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        buttonCube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (painterView.selectedType == 5) {
                    painterView.selectedType = 0;
                    painterView.scrollView.setEnableScrolling(true);
                    painterView.horizontalScrollView.setEnableScrolling(true);
                    buttonCube.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

                } else {
                    painterView.scrollView.setEnableScrolling(false);
                    painterView.horizontalScrollView.setEnableScrolling(false);
                    optionOn(buttonCube);
                    painterView.selectedType = 5;
                    painterView.start(5);
                }
            }
        });
        buttonCube.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Куб", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        buttonErasor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (painterView.selectedType == 6) {
                    painterView.selectedType = 0;
                    painterView.scrollView.setEnableScrolling(true);
                    painterView.horizontalScrollView.setEnableScrolling(true);
                    buttonErasor.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

                } else {
                    painterView.scrollView.setEnableScrolling(false);
                    painterView.horizontalScrollView.setEnableScrolling(false);
                    optionOn(buttonErasor);
                    painterView.selectedType = 6;
                    painterView.start(6);
                }
            }
        });
        buttonBrush.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(MainActivity.this, "Гумка", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        LinearLayout layoutStroke = findViewById(R.id.stroke);
        LinearLayout layoutFill = findViewById(R.id.fill);
        LinearLayout layout1 = findViewById(R.id.linear1);
        LinearLayout layout2 = findViewById(R.id.linear2);
        LinearLayout layout3 = findViewById(R.id.linear3);
        LinearLayout layout4 = findViewById(R.id.linear4);
        LinearLayout layout5 = findViewById(R.id.linear5);
        LinearLayout layout6 = findViewById(R.id.linear6);
        LinearLayout layout7 = findViewById(R.id.linear7);
        LinearLayout layout8 = findViewById(R.id.linear8);
        LinearLayout layout9 = findViewById(R.id.linear9);
        LinearLayout layout10 = findViewById(R.id.linear10);
        LinearLayout layout11 = findViewById(R.id.linear11);
        LinearLayout layout12 = findViewById(R.id.linear12);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout1.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout1.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout2.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout2.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout3.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout3.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout4.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout4.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout5.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout5.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout6.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout6.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout7.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout7.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout8.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout8.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout9.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout9.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout9.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout10.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout10.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout10.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout11.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout11.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout11.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);                 }
                return true;
            }
        });
        layout12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = layout12.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutStroke.setBackgroundColor(color);
                    painterView.paintStroke.setColor(color);
                }
            }
        });
        layout12.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = layout12.getBackground();
                if (drawable instanceof ColorDrawable) {
                    int color = ((ColorDrawable) drawable).getColor();
                    layoutFill.setBackgroundColor(color);
                    painterView.paintFill.setColor(color);
                }
                return true;
            }
        });
    }

    public void optionOn(ImageButton button) {
        for (ImageButton button1 : imageButtons) {
            button1.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }
        button.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }
}