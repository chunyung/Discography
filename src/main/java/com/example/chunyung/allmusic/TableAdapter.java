package com.example.chunyung.allmusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class TableAdapter extends BaseAdapter {
    private Context context;
    private List<TableRow> rows;

    public TableAdapter (Context context, List<TableRow> rows) {
        this.context = context;
        this.rows = rows;
    }

    @Override
    public TableRow getItem(int position) {
        return this.rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return new TableRowView(this.context, getItem(position));
    }

    @Override
    public int getCount() {
        return this.rows.size();
    }

    class TableRowView extends LinearLayout {
        public TableRowView(Context context, TableRow row) {
            super(context);
            for (TableCell cell : row.cells) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cell.width, cell.height);
                if (cell.cellType.equals("TEXT")) {
                    TextView textCell = new TextView(context);
                    textCell.setText(cell.value);
                    textCell.setLines(5);
                    textCell.setGravity(Gravity.CENTER);
                    textCell.setBackgroundColor(Color.WHITE);
                    addView(textCell, layoutParams);
                } else {
                    ImageView imgCell = new ImageView(context);
                    loadImageFromStorage(cell.value, imgCell);
                    addView(imgCell, layoutParams);
                }
            }
            setBackgroundColor(Color.WHITE);
        }
        private void loadImageFromStorage(String path, ImageView imgCell)
        {
            try {
                File f = new File(path);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imgCell.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    static public class TableRow {
        public List<TableCell> cells;
        public String details;
        public TableRow(List<TableCell> cells, String details) {
            this.cells = cells;
            this.details = details;
        }
        public int getSize() {
            return this.cells.size();
        }
        public TableCell getCell(int index) {
            if (index >= 0 && index < getSize()) {
                return this.cells.get(index);
            }
            return null;
        }
        public String getDetails() {
            return this.details;
        }
    }

    static public class TableCell {
        public String cellType, value;
        public int height, width;
        public TableCell(String type, String value, int height, int width) {
            this.cellType = type;
            this.value = value;
            this.height = height;
            this.width = width;
        }
    }
}
