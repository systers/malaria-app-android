package com.peacecorps.malaria.model;

import junit.framework.TestCase;

/**
 * Created by yatna on 17/8/16.
 */
public class RapidFireQuestionModelTest extends TestCase {
    RapidFireQuestionModel rapidFireQuestionModel;
    public void setUp() throws Exception {
        super.setUp();
        rapidFireQuestionModel=new RapidFireQuestionModel("QuesText","a","b","c",2);

    }

    public void testGetQuestion() throws Exception {
        assertEquals("Question getter invalid","QuesText",rapidFireQuestionModel.getQuestion());
    }

    public void testGetOption1() throws Exception {
        assertEquals("Option1 getter invalid","a",rapidFireQuestionModel.getOption1());
    }

    public void testGetOption2() throws Exception {
        assertEquals("Option2 getter invalid","b",rapidFireQuestionModel.getOption2());
    }

    public void testGetOption3() throws Exception {
        assertEquals("Option3 getter invalid","c",rapidFireQuestionModel.getOption3());
    }

    public void testGetAns() throws Exception {
        assertEquals("Answer getter invalid",2,rapidFireQuestionModel.getAns());
    }
}