package com.example.sudoku;

//reference: https://github.com/knutkirkhorn/Android-Sudoku

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.prefs.InvalidPreferencesFormatException;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, CellGroupFragment.OnFragmentInteractionListener {
    private View clickedCell;
    private View lastClickedCell;
    private int numberPadID[] = new int[]{R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btnDelete, R.id.btnCheck};
    private int clickedGroup;
    private int clickedCellId;

    private int numOfBlanks;
    private int[][] mat;

    private TextView selectedCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        numOfBlanks = intent.getIntExtra("blanks", 50);

        Sudoku_generator sudoku = new Sudoku_generator(numOfBlanks);
        sudoku.fillValues();
        mat = sudoku.getSudoku();

        //links fragments id
        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i - 1]);
            thisCellGroupFragment.setGroupId(i);
        }

        //links numberpad id
        Button buttons[] = new Button[11];
        for (int i = 0; i < 11; i++) {
            buttons[i] = findViewById(numberPadID[i]);
            buttons[i].setOnClickListener(this);
        }

        //Display all values from the current board
        CellGroupFragment tempCellGroupFragment;
        for (int i = 8; i >= 0; i--) {
            for (int j = 8; j >= 0; j--) {
                int column = j / 3;
                int row = i / 3;

                int fragmentNumber = (row * 3) + column;
                tempCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
                int groupColumn = j % 3;
                int groupRow = i % 3;

                int groupPosition = (groupRow * 3) + groupColumn;
                int currentValue = mat[i][j];

                if (currentValue != 0) {
                    tempCellGroupFragment.setValue(groupPosition, currentValue);
                }
            }
        }
    }

    @Override
    public void onFragmentInteraction(int groupId, int cellId, View view) {
        clickedCell = view;
        clickedGroup = groupId;
        clickedCellId = cellId;
        int row = ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
        int column = ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);

        //Log.i(TAG, "Clicked group " + groupId + ", cell " + cellId);
        if (mat[row][column] == 0) {
            try {
                lastClickedCell.setBackgroundResource(R.drawable.table_border_cell);
            } catch (NullPointerException e) {
            }
            view.setBackgroundColor(R.color.teal_200);
            lastClickedCell = view;
            selectedCell = (TextView) clickedCell;
        }
    }

    @Override
    public void onClick(View view) {
        for (int i = 1; i < 12; i++) {
            if (view.getId() == numberPadID[i - 1]) {
                try {
                    if (i < 10) {
                        selectedCell.setText(String.valueOf(i));
                        selectedCell.setTextColor(Color.BLACK);
                        selectedCell.setTypeface(null, Typeface.BOLD);
                    } else if (i == 10) {
                        selectedCell.setText("");
                    } else {
                        if (checkAllGroups()) {
                            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "有錯誤！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (NullPointerException e) {
                }
            }
        }
    }

    private boolean checkAllGroups() {
        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 0; i < 9; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i]);
            if (!thisCellGroupFragment.checkGroupCorrect()) {
                return false;
            }
        }
        return true;
    }
}