package com.koroliuk.painter.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.koroliuk.painter.editor.drawing_editor.BrushEditor;
import com.koroliuk.painter.editor.drawing_editor.CubeEditor;
import com.koroliuk.painter.editor.drawing_editor.ErasorEditor;
import com.koroliuk.painter.editor.drawing_editor.LineEditor;
import com.koroliuk.painter.editor.drawing_editor.OvalEditor;
import com.koroliuk.painter.editor.drawing_editor.RectEditor;
import com.koroliuk.painter.editor.drawings.Brush;
import com.koroliuk.painter.editor.drawings.Cube;
import com.koroliuk.painter.editor.drawings.Erasor;
import com.koroliuk.painter.editor.drawings.Line;
import com.koroliuk.painter.editor.drawings.Oval;
import com.koroliuk.painter.editor.drawings.Rect;
import com.koroliuk.painter.editor.drawings.Shape;
import com.koroliuk.painter.scrolling.MyHorizontalScrollView;
import com.koroliuk.painter.scrolling.MyScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class DrawerView extends View {

    public static Context context;
    public MyScrollView scrollView;
    public MyHorizontalScrollView horizontalScrollView;
    public boolean isDrawing;
    public boolean isRecycle;
    public List<Shape> showedShapes = new ArrayList<>();
    public Bitmap bitmap;
    public Bitmap imageBitmap;
    public Canvas canvas;
    public Paint paintStroke;
    public Paint paintFill;
    public String backgroundColor;
    public Shape lastEdited;
    public boolean isFilled;
    public int width;
    public int selectedType;
    public float sx;
    public float sy;
    public float ex;
    public float ey;

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundColor = "#FFFFFF";
        paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(20);
        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(Color.TRANSPARENT);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if (canvas == null) {
            canvas = new Canvas(bitmap);
            draw(canvas);
        }
        if (imageBitmap != null) {
            bitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawColor(Color.parseColor(backgroundColor));
            canvas.drawBitmap(bitmap, 0, 0, null);
            for (Shape shape : showedShapes) {
                shape.draw();
            }
            if (isDrawing) {
                if (lastEdited.type == 1 || lastEdited.type == 6) {
                    lastEdited.canvas = this.canvas;
                } else {
                    lastEdited.canvas = canvas;
                }
                lastEdited.draw();
            }
            if (isRecycle) {
                isRecycle = false;
                canvas.drawColor(Color.parseColor("#C2C5C6"));
            }
        }
    }

    public void recycle() {
        if (!isDrawing) {
            isDrawing = true;
            invalidate();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (selectedType) {
            case 1:
                BrushEditor brushEditor = new BrushEditor(this);
                brushEditor.edit(event);
                break;
            case 2:
                LineEditor lineEditor = new LineEditor(this);
                lineEditor.edit(event);
                break;
            case 3:
                RectEditor rectEditor = new RectEditor(this);
                rectEditor.edit(event);
                break;
            case 4:
                OvalEditor ovalEditor = new OvalEditor(this);
                ovalEditor.edit(event);
                break;
            case 5:
                CubeEditor cubeEditor = new CubeEditor(this);
                cubeEditor.edit(event);
                break;
            case 6:
                ErasorEditor erasorEditor = new ErasorEditor(this);
                erasorEditor.edit(event);
                break;
        }
        return true;
    }

    public void start(int type) {
        switch (type) {
            case 1:
                lastEdited = new Brush(paintStroke, paintFill, 1);
                break;
            case 2:
                lastEdited = new Line(paintStroke, paintFill, 2);
                break;
            case 3:
                lastEdited = new Rect(paintStroke, paintFill, 3);
                break;
            case 4:
                lastEdited = new Oval(paintStroke, paintFill, 4);
                break;
            case 5:
                lastEdited = new Cube(paintStroke, paintFill, 5);
                break;
            case 6:
                lastEdited = new Erasor(paintStroke, paintFill, 6);
                break;
        }
    }

    public void addToDrawen() {
        lastEdited.canvas = canvas;
        lastEdited.draw();
        showedShapes.add(lastEdited);
        switch (lastEdited.type) {
            case 1:
                lastEdited = new Brush(paintStroke, paintFill, 1);
                break;
            case 2:
                lastEdited = new Line(paintStroke, paintFill, 2);
                break;
            case 3:
                lastEdited = new Rect(paintStroke, paintFill, 3);
                break;
            case 4:
                lastEdited = new Oval(paintStroke, paintFill, 4);
                break;
            case 5:
                lastEdited = new Cube(paintStroke, paintFill, 5);
                break;
            case 6:
                lastEdited = new Erasor(paintStroke, paintFill, 6);
                break;
        }
        invalidate();
    }

    public void  changeSize(int w, int h) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        this.setLayoutParams(params);
        onSizeChanged(w, h, bitmap.getWidth(), bitmap.getHeight());
        bitmap = newBitmap;
        invalidate();
    }

    public void saveFile(Uri uri) throws IOException {
        final File file = new File(getRealPathFromURI(context, uri));
        try (FileOutputStream fOut = new FileOutputStream(file)) {
            String[] path = file.getAbsolutePath().split("\\.");
            String ext = path[path.length - 1];
            if (ext.equals("png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                Toast toast = Toast.makeText(context, "Збережено", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (ext.equals("jpeg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                Toast toast = Toast.makeText(context, "Файл збережено", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Toast toast = Toast.makeText(context, "Формат файлу не вірний", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String saveFileAsPNG(String name) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut;
            File file = new File(fullPath, name+".png");
            if(file.exists())
                file.delete();
            file.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return fullPath+name+".png";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Помилка";
    }
}
