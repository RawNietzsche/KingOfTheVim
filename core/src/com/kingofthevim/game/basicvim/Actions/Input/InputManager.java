package com.kingofthevim.game.basicvim.Actions.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.kingofthevim.game.basicvim.Actions.VimMove;
import com.kingofthevim.game.basicvim.Actions.VimMovement;
import com.kingofthevim.game.basicvim.Builder;
import com.kingofthevim.game.basicvim.VimObject.Cursor;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.kingofthevim.game.basicvim.Matrix.Tools.tryParseInt;

public class InputManager implements InputProcessor {


    Cursor cursor;
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    MoveInput moveInput;
    OperationInput operationInput;
    Builder builder;
    TextInput textInput;

    private boolean builderActive = false;

    private LinkedList<Character> inputHistory;

    int iterationInt = 0;
    String iterationString = "0";
    boolean addToHistory = false;

    ArrayList<VimMove> vimMoveList;



    public InputManager(Cursor cursor){
        this.cursor = cursor;
        inputHistory = new LinkedList<>();
        moveInput = new MoveInput(cursor);
        operationInput = new OperationInput(cursor);
        builder = new Builder(cursor);
        textInput = new TextInput(cursor);
        vimMoveList = new ArrayList<>();

        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(moveInput);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public boolean keyDown(int keycode) {
        addToHistory = true;

        if(operationInput.hasExectued){
            inputMultiplexer.removeProcessor(operationInput);
            inputMultiplexer.addProcessor(1, moveInput);
        }

        if(textInput.hasExecuted){
            inputMultiplexer.removeProcessor(textInput);
            inputMultiplexer.addProcessor(1, moveInput);
        }

        iterationSync();

        switch (keycode){

            case Input.Keys.D:
                inputMultiplexer.removeProcessor(moveInput);
                inputMultiplexer.addProcessor(1, operationInput);
                System.out.println("D pressed");
                operationInput.hasExectued = false;
                //operationInput.vimMove.operator = 'd';
                return true;

            case Input.Keys.TAB:
                if (builderActive){
                    inputMultiplexer.removeProcessor(builder);
                    builderActive = false;
                }
                else {
                    inputMultiplexer.addProcessor(1, builder);
                    builderActive = true;
                }
                return true;

            case Input.Keys.R:
                if(oneBeforeLast() != 'r'){
                    textInput.operatorChar = 'r';
                    inputMultiplexer.removeProcessor(moveInput);
                    inputMultiplexer.addProcessor(1, textInput);
                    System.out.println("R pressed");
                    textInput.hasExecuted = false;
                }
                return true;

            case Input.Keys.NUM_1:
                return integerMaker('1');

            case Input.Keys.NUM_2:
                return integerMaker('2');

            case Input.Keys.NUM_3:
                return integerMaker('3');

            case Input.Keys.NUM_4:
                return integerMaker('4');

            case Input.Keys.NUM_5:
                return integerMaker('5');

            case Input.Keys.NUM_6:
                return integerMaker('6');

            case Input.Keys.NUM_7:
                return integerMaker('7');

            case Input.Keys.NUM_8:
                return integerMaker('8');

            case Input.Keys.NUM_9:
                return integerMaker('9');

            case Input.Keys.NUM_0:
                return integerMaker('0');

            case Input.Keys.I:
                inputMultiplexer.addProcessor(0, textInput);
                inputMultiplexer.removeProcessor(moveInput);
                textInput.hasExecuted = false;
                return true;

            case Input.Keys.ESCAPE:
                resetIteration();
                resetInputProcessors();
                System.out.println("ESC");
                return true;

        }

        return false;
    }

    private void resetInputProcessors(){
        inputMultiplexer.clear();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(1, moveInput);
    }

    private void iterationSync(){
        if(operationInput.hasExectued
        || moveInput.hasExectued
        || textInput.hasExecuted){

            addVimMove(moveInput);
            addVimMove(operationInput);


            operationInput.iteration = 0;
            moveInput.iteration = 0;
            iterationInt = 0;
            iterationString = "0";
            operationInput.hasExectued = false;
            moveInput.hasExectued = false;
            textInput.hasExecuted = false;

        }

        if(iterationInt > 0){
            operationInput.iteration = iterationInt;
            moveInput.iteration = iterationInt;
        }
    }

    /**
     * Gives back the second last char
     * from the input history.
     * @return the second last char typed
     */
    private char oneBeforeLast(){
        if(inputHistory.size() > 0){
            return inputHistory.get(inputHistory.size() - 1);
        }
        return ' ';
    }

    /**
     * checks if a char can be converted to an int
     * and if so adds it to iterationInt, if not iterationInt
     * is reset
     * @param intProspect char to check for int
     *                    conversion possibility
     * @return true if intProspect could be parsed
     */
    public boolean integerMaker(char intProspect){

        if(intProspect > 47 &&
                intProspect < 58){
            int integer;
            iterationString += String.valueOf(intProspect);
            integer = tryParseInt(iterationString);

            if(iterationInt + integer == 0){
                iterationString = "0";
                return false;
            }

            iterationInt = integer;

            System.out.println("it-int: " + iterationInt);
            return true;
        }
        return false;
    }


    //TODO make iteration work with ONE!
    void resetIteration(){
        iterationString = "0";
        iterationInt = 0;
        moveInput.iteration = 0;
        operationInput.iteration = 0;
    }

    @Override
    public boolean keyUp(int keycode) {
        addToHistory = false;

        return false;
    }


    @Override
    public boolean keyTyped(char character) {
        if(addToHistory) inputHistory.add(character);

        return false;
    }


    /**
     * adds a legit VimMove to history
     */
    private void addVimMove(VimMovement vimMove){

        if(vimMove.hasMove()){
            vimMoveList.add(vimMove.getResetVimMove());
        }
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
