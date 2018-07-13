package com.peacecorps.malaria.model;

import com.peacecorps.malaria.ui.play.rapid_fire.QuestionModel;

import junit.framework.TestCase;

/**
 * Created by yatna on 17/8/16.
 */
public class QuestionModelTest extends TestCase {
    QuestionModel questionModel;
    public void setUp() throws Exception {
        super.setUp();
        questionModel =new QuestionModel("QuesText","a","b","c",2);

    }

    public void testGetQuestion() throws Exception {
        assertEquals("Question getter invalid","QuesText", questionModel.getQuestion());
    }

    public void testGetOption1() throws Exception {
        assertEquals("Option1 getter invalid","a", questionModel.getOption1());
    }

    public void testGetOption2() throws Exception {
        assertEquals("Option2 getter invalid","b", questionModel.getOption2());
    }

    public void testGetOption3() throws Exception {
        assertEquals("Option3 getter invalid","c", questionModel.getOption3());
    }

    public void testGetAns() throws Exception {
        assertEquals("Answer getter invalid",2, questionModel.getAns());
    }
}