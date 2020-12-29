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
import android.widget.Toast;

import com.koroliuk.painter.State;
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


public class PainterView extends View {

    public static Context context;
    public MyScrollView scrollView;
    public MyHorizontalScrollView horizontalScrollView;
    public boolean isDrawing;
    public State state;
    public List<Shape> showedShapes;
    public Bitmap mainBitmap;
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


    public PainterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundColor = "#FFFFFF";
        paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(10);
        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(Color.TRANSPARENT);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mainBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if (imageBitmap != null) {
            mainBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        canvas = new Canvas(mainBitmap);
        state = new State(null, mainBitmap, null);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mainBitmap != null) {
            canvas.drawColor(Color.parseColor(backgroundColor));
            canvas.drawBitmap(mainBitmap, 0, 0, null);
            if (isDrawing) {
                if (lastEdited.type == 1 || lastEdited.type == 6) {
                    lastEdited.canvas = this.canvas;
                } else {
                    lastEdited.canvas = canvas;
                }
                lastEdited.draw();
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "DrawAllocation"})
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (scrollView.isEnableScrolling()) {
            return true;
        }
        lastEdited.pStroke = new Paint(paintStroke);
        lastEdited.pFill = new Paint(paintFill);
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
                Paint pStroke1 = new Paint(paintStroke);
                Paint pFill1 = new Paint(paintFill);
                lastEdited = new Brush(pStroke1, pFill1, 1);
                break;
            case 2:
                Paint pStroke2 = new Paint(paintStroke);
                Paint pFill2 = new Paint(paintFill);
                lastEdited = new Line(pStroke2, pFill2, 2);
                break;
            case 3:
                Paint pStroke3 = new Paint(paintStroke);
                Paint pFill3 = new Paint(paintFill);
                lastEdited = new Rect(pStroke3, pFill3, 3);
                break;
            case 4:
                Paint pStroke4 = new Paint(paintStroke);
                Paint pFill4 = new Paint(paintFill);
                lastEdited = new Oval(pStroke4, pFill4, 4);
                break;
            case 5:
                Paint pStroke5 = new Paint(paintStroke);
                Paint pFill5 = new Paint(paintFill);
                lastEdited = new Cube(pStroke5, pFill5, 5);
                break;
            case 6:
                Paint pStroke6 = new Paint(paintStroke);
                Paint pFill6 = new Paint(paintFill);
                lastEdited = new Erasor(pStroke6, pFill6, 6);
                break;
        }
    }

    public void addToDrawen() {
        lastEdited.canvas = canvas;
        lastEdited.draw();
        State newState = new State(state, mainBitmap, null);
        state.setNext(newState);
        state = newState;
        start(lastEdited.type);
        invalidate();
    }

    public void saveFile(Uri uri) throws IOException {
        final File file = new File(getRealPathFromURI(context, uri));
        try (FileOutputStream fOut = new FileOutputStream(file)) {
            String[] path = file.getAbsolutePath().split("\\.");
            String ext = path[path.length - 1];
            if (ext.equals("png")) {
                mainBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                Toast toast = Toast.makeText(context, "Збережено", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (ext.equals("jpeg")) {
                mainBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
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
            mainBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return fullPath+name+".png";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Помилка";
    }
}
