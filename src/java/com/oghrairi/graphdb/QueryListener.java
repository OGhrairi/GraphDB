package com.oghrairi.graphdb;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;

import java.util.ArrayList;

public class QueryListener extends gBaseListener{
    private ArrayList<String> conjunctiveVariables;


    public QueryListener() {
        conjunctiveVariables = new ArrayList<String>();
    }

    @Override
    public void enterVariables(gParser.VariablesContext ctx) {
        int childCount = ctx.getChildCount();
        for (int i=1; i<childCount-2;i=i+2){
            String child = ctx.getChild(i).getText();
            System.out.println(child);
            conjunctiveVariables.add(child);
        }
    }

    @Override
    public void enterCexpression(gParser.CexpressionContext ctx) {
        String[] boundVariables = new String[2];
        int childCount = ctx.getChildCount();
        ParseTree t = ctx.getChild(1);
        boundVariables[0] = ctx.getChild(childCount-4).getText();
        boundVariables[1] = ctx.getChild(childCount-2).getText();
        System.out.println("var 1:" + boundVariables[0] + " var2: "+boundVariables[1]);
        int cc = t.getChildCount();

        for (int i=0; i<cc; i++){
            System.out.println(t.getChild(i).getText());
            String cls= t.getChild(i).getClass().getName();
            //experiment to expose leaf classes in order to know which query method to apply to them
            if(cls.indexOf("$")>=0){
                cls = cls.substring(cls.indexOf("$"));
                System.out.println(cls);
            }
        }
    }
    public void queryApplyer(ParseTree subtree){
        //this method will (probably recursively) handle applying the right query algorithms to the right sections of a query string
    }
}
