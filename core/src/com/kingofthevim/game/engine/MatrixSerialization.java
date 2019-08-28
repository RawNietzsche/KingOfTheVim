package com.kingofthevim.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.kingofthevim.game.engine.matrix.*;
import com.kingofthevim.game.engine.vim_object.Cursor;
import com.kingofthevim.game.engine.vim_object.VimObject;

import java.util.ArrayList;
import java.util.Collections;

public class MatrixSerialization {

    private Cursor cursor;
    private VimObject vimObject;
    private static String fileName = "levels/builder/LevelBuilder_0";
    private static int counter;
    private Json json;
    private Save save;
    private LetterManager letterManager;
    private int numberOfSaves = 0;
    private int currentSave = 0;
    private ArrayList<String> filePaths;

    public MatrixSerialization(){
        json = new Json();
        json.setUsePrototypes(false);
        save = new Save();
        filePaths = new ArrayList<>();
    }

    public MatrixSerialization(VimObject vimObject){
        this();
        this.vimObject = vimObject;
        letterManager = new LetterManager(vimObject.getVimMatrix());
    }


    /**
     * Save object the data json uses to save and
     * load levels and objects with
     */
    private static class Save {


        private ArrayList<ArrayList<Properties>> savedMatrix;
        private int cursorRow;
        private int cursorColumn;

        Save(){
            savedMatrix = new ArrayList<>();
        }


        /**
         * Copies the current state of the vim matrix
         *
         * @param vimObject object used to copy with
         */
        void copyCurrentMatrix(VimObject vimObject){
            ArrayList<ArrayList<Cell>> cellMatrix = vimObject.getVimMatrix().getCellMatrix();

            for (int i = 0; i < cellMatrix.size(); i++) {

                savedMatrix.add(i, new ArrayList<>());

                for (int j = 0; j < cellMatrix.get(i).size(); j++) {
                    Cell column = cellMatrix.get(i).get(j);

                    savedMatrix.get(i).add(column.getCellProperties());

                }
            }
        }


        /**
         * Copies the chosen object
         *
         * @param vimObject object to copy
         */
        void copyObjectPosition(VimObject vimObject){
            cursorRow = vimObject.getPosition().getCurrRow();
            cursorColumn = vimObject.getPosition().getCurrColumn();

        }
    }



    /**
     * Saves all on screen in file with
     * an autogenerated name
     */
    public void saveAll( ){

        if(! Gdx.files.local("levels/builder/").exists()){
            Gdx.files.local("levels/builder/").mkdirs();
        }

        while (Gdx.files.local(fileName).exists()){
            fileName = "levels/builder/LevelBuilder_" + counter++;
        }

        saveAll(fileName);
    }


    /**
     * Saves all on screen in file with
     * an user-generated name
     */
    public void saveAll(String filePath){
        save.copyCurrentMatrix(vimObject);
        save.copyObjectPosition(vimObject);

        FileHandle file = Gdx.files.local(filePath);
        json.toJson(save,  file);
    }


    /**
     * Saves a specific object
     *
     * @param filePath name of file to save to
     */
    private void saveObj(String filePath){
        save.copyObjectPosition(vimObject);

        FileHandle file = Gdx.files.local(filePath);
        json.toJson(save,  file);
    }


    /**
     * Loads the latest save
     *
     */
    public void loadAll( ){
        loadAll(fileName);
    }

    /**
     * Loads the next save
     * @return true if successful
     */
    public boolean loadNextFile(){

        getFiles();

        if(! updateFileNum()) return false;

        currentSave++;
        safeLoadCurrentSave();

        return true;
    }


    /**
     * Loads the previous save
     * @return true if successful
     */
    public boolean loadPreviousFile(){

        getFiles();

        if(! updateFileNum()) return false;

        currentSave--;
        safeLoadCurrentSave();

        return true;
    }


    /**
     * Uses the currentSave integer to load a game
     * from filePaths. If it detects that currentSave
     * is too low it will set the highest number and load
     * that and if to high, it will reset before loading.
     */
    private void safeLoadCurrentSave(){

        if(currentSave < 0) {
            currentSave = numberOfSaves - 1;
            loadAll("levels/builder/" + filePaths.get( currentSave));
            return;
        }

        if(currentSave < numberOfSaves) {
            loadAll("levels/builder/" + filePaths.get( currentSave));
        }

        if(currentSave >= numberOfSaves){
            currentSave = 0;
            loadAll("levels/builder/" + filePaths.get( currentSave));
        }
    }


    /**
     * Updates the file number and looks if the
     * file-list is empty.
     * @return true if files exist
     */
    private boolean updateFileNum(){
        if(filePaths.isEmpty()) return false;

        if(filePaths.size() > numberOfSaves) numberOfSaves = filePaths.size();
        return true;
    }

    /**
     * Loads a specific save
     *
     * @param filePath name of file to load from
     */
    public void loadAll(String filePath){
        Save save = loadSave(filePath);
        loadObject(save);
        loadMatrix(save);
        Gdx.graphics.setTitle("King of the Vim: " + filePath);
    }

    private void getFiles(){

        FileHandle[] files = Gdx.files.internal("levels/builder/").list();
        filePaths = new ArrayList<>();
        for(FileHandle f : files){ filePaths.add(f.name()); }
        Collections.sort(filePaths);
    }

    /**
     * List files at the screen
     */
    public void listFiles(){

        letterManager.setHorizontalStringArray(filePaths, 2, 2, 2, true, true, LetterType.WHITE);
    }

    /**
     * Loads a specific level into a given matrix
     * and returns a Cursor connected to it
     *
     * @param filePath name of file to load from
     * @param matrix to load level into
     * @return Cursor connected given matrix
     */
    public Cursor loadLevel(String filePath, CellMatrix matrix){
        Save save = loadSave(filePath);
        loadMatrix(save, matrix);

        cursor = new Cursor(matrix, save.cursorRow, save.cursorColumn);

        return cursor;
    }

    /**
     * Loads Save class from file
     *
     * @param filePath name of file to load from
     * @return a Save object
     */
    public Save loadSave(String filePath){
        FileHandle file = Gdx.files.local(filePath);
        return json.fromJson(Save.class, file);
    }


    /**
     * Loads level matrix into static grid of
     * the constructor given vim_object
     *
     * @param save to load from
     */
    private void loadMatrix(Save save){

        ArrayList<ArrayList<Cell>> cellMatrix = vimObject.getVimMatrix().getCellMatrix();

        ArrayList<ArrayList<Properties>> propList = save.savedMatrix;

        for (int i = 0; i < cellMatrix.size(); i++) {

            for (int j = 0; j < cellMatrix.get(i).size(); j++) {

                vimObject.getVimMatrix().getCellMatrix().get(i).get(j).setCellLook(propList.get(i).get(j));

            }
        }

    }

    /**
     * Loads level matrix into static grid of
     * the given vim_object
     *
     * @param save to load from
     * @param matrix to load level into
     */
    private void loadMatrix(Save save, CellMatrix matrix){

        ArrayList<ArrayList<Cell>> cellMatrix = matrix.getCellMatrix();

        ArrayList<ArrayList<Properties>> propList = save.savedMatrix;

        for (int i = 0; i < cellMatrix.size(); i++) {

            for (int j = 0; j < cellMatrix.get(i).size(); j++) {

                matrix.getCellMatrix().get(i).get(j).setCellLook(propList.get(i).get(j));

            }
        }

    }

    /**
     * loads object into grid
     *
     * @param save to load from
     */
    private void loadObject(Save save){
        vimObject.getPosition().setAbsoluteColumn(save.cursorColumn);
        vimObject.getPosition().setAbsoluteRow(save.cursorRow);
    }

}
