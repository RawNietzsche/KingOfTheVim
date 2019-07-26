package com.kingofthevim.game.basicvim.Actions.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Json;
import com.kingofthevim.game.basicvim.Actions.Movement;
import com.kingofthevim.game.basicvim.MatrixSerialization;
import com.kingofthevim.game.basicvim.VimObject.Cursor;

public class MoveInput extends Movement implements InputProcessor {

    Cursor cursor;
    int iteration = 0;
    boolean hasExectued = false;

    public MoveInput(Cursor cursor){

        this.cursor = cursor;
        setObjectPosition(cursor.getPosition());
    }


    @Override
    public boolean keyDown(int keycode) {

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode){

            case Input.Keys.J:
                hasExectued = true;
                return charVerticalMove(cursor, true, iteration);

            case Input.Keys.K:
                hasExectued = true;
                return charVerticalMove(cursor, false, iteration);

            case Input.Keys.L:
                hasExectued = true;
                return charHorizontalMove(cursor, true, iteration);

            case Input.Keys.H:
                hasExectued = true;
                return charHorizontalMove(cursor, false, iteration);

            case Input.Keys.E:
                hasExectued = true;
                return traverseWord(cursor, Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT), false, iteration);

            case Input.Keys.W:
                hasExectued = true;
                return traverseWord(cursor, Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT), true, iteration);

            case Input.Keys.B:
                hasExectued = true;
                return traversePreviousWord(cursor, Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT), iteration);

            case Input.Keys.NUM_0:
                if(iteration > 0) return false;
                return traverseWholeLine(cursor, false);
            case Input.Keys.F1:

                MatrixSerialization matrixSerialization = new MatrixSerialization();

                matrixSerialization.saveCurrentMatrix(cursor);

                return true;

                /*
            case Input.Keys.F2:

                Json json2 = new Json();
                json2.setUsePrototypes(false);

                MatrixSerialization matrixSerialization1 = json2.fromJson(MatrixSerialization.class, cellSave);

                System.out.println(json2.prettyPrint(matrixSerialization1));
                return true;

                 */



        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if(character == '$'){
            return traverseWholeLine(cursor, true);
        }
        if(character == '^'){
            return goToFirstNonBlankChar(cursor);
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
