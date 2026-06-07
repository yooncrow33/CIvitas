package com.fw.internal.sys.operator;

import com.fw.main.Operator;

import java.util.ArrayList;

public class OperatorPack {
    private ArrayList<Operator> operators = new ArrayList<>();
    public void launch() {
        for (Operator o : operators) {
            o.exe();
        }
        operators.clear();
    }

    public void addOperator(Operator o) {
        operators.add(o);
    }

    public void exe() {
        for (Operator o : operators) {
            o.exe();
        }
    }
}
