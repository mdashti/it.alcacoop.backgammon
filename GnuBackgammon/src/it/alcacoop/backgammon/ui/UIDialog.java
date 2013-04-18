/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon.ui;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.logic.MatchState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public final class UIDialog extends Window {

  private Table t1, t2, t3;
  private TextButton bContinue;
  private TextButton bYes;
  private TextButton bNo;
  private TextButton bCancel;
  private TextButton bExport;
  
  private Label label;
  private Drawable background;
  private static ClickListener cl;
  
  private static UIDialog instance;
  
  private BaseFSM.Events evt;
  private boolean quitWindow = false;
  private boolean optionsWindow = false;
  private boolean leaveWindow = false;
  private boolean dicesWindow = false;
  
  private GameOptionsTable opts;
  
  static {
    instance = new UIDialog();
  }
  
  private UIDialog() {
    super("", GnuBackgammon.skin);
    setModal(true);
    setMovable(false);
    
    cl = new ClickListener(){
      public void clicked(InputEvent event, float x, float y) {
        final String s;
        if (event.getTarget() instanceof Label) {
          s = ((Label)event.getTarget()).getText().toString().toUpperCase();
        } else {
          s = ((TextButton)event.getTarget()).getText().toString().toUpperCase();
        }
        
        instance.addAction(MyActions.sequence(
            Actions.fadeOut(0.3f),
            Actions.run(new Runnable() {
              @Override
              public void run() {
                instance.remove();
                
                if (leaveWindow) {
                  GnuBackgammon.fsm.processEvent(instance.evt, s);
                  return;
                }
                
                boolean ret = s.equals("YES")||s.equals("OK");
                
                if ((instance.quitWindow)&&(ret)) {
                  Gdx.app.exit();
                } else {
                  GnuBackgammon.fsm.processEvent(instance.evt, ret);
                  if (instance.optionsWindow) opts.savePrefs();
                }
                
                if ((instance.dicesWindow)&&(!ret)) {
                  String[] ret2 = s.split("X");
                  int[] intArray = new int[ret2.length];
                  for(int i = 0; i < ret2.length; i++) {
                      intArray[i] = Integer.parseInt(ret2[i]);
                  }
                  GnuBackgammon.fsm.processEvent(GameFSM.Events.DICES_ROLLED, intArray);
                }

                Gdx.graphics.setContinuousRendering(false);
                Gdx.graphics.requestRendering();
              }
            })
        ));
      };
    };
    
    label = new Label("", GnuBackgammon.skin);
    
    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);
    
    bYes = new TextButton("Yes", tl);
    bYes.addListener(cl);
    bNo = new TextButton("No", tl);
    bNo.addListener(cl);
    bContinue = new TextButton("Ok", tl);
    bContinue.addListener(cl);
    bCancel = new TextButton("Cancel", tl);
    bCancel.addListener(cl);
    bExport = new TextButton("Export Match", tl);
    bExport.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.shareMatch(GnuBackgammon.Instance.rec);
        super.clicked(event, x, y);
      }
    });

    background = GnuBackgammon.skin.getDrawable("default-window");
    setBackground(background);
    
    opts = new GameOptionsTable(false, cl);
    
    t1 = new Table();
    t1.setFillParent(true);
    t1.add(label).fill().expand().center();
    
    t2 = new Table();
    t2.setFillParent(true);
    t2.add().colspan(2).expand();
    t2.add(bContinue).fill().expand();
    t2.add().colspan(2).expand();
    
    t3 = new Table();
    t3.setFillParent(true);
    t3.add().expand();
    t3.add(bNo).fill().expand();
    t3.add().expand();
    t3.add(bYes).fill().expand();
    t3.add().expand();
    
    setColor(1,1,1,0);
    
  }
  
  private void setText(String t) {
    label.setText(t);
  }
  
  /*
  private void hide(Runnable r) {
    addAction(MyActions.sequence(
      Actions.fadeOut(0.3f),
      Actions.run(r)
    ));
  }
  */
  
  public static void getYesNoDialog(BaseFSM.Events evt, String text, Stage stage) {
    getYesNoDialog(evt, text, 1, stage);
  }
  public static void getYesNoDialog(BaseFSM.Events evt, String text, float alpha, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.4f;
    float width = stage.getWidth()*0.78f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);
    
    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    
    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }
  
  public static void getContinueDialog(BaseFSM.Events evt, String text, Stage stage) {
    getContinueDialog(evt, text, 1, stage);
  }
  public static void getContinueDialog(BaseFSM.Events evt, String text, float alpha, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.4f;
    float width = stage.getWidth()*0.6f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add(instance.label).colspan(3).expand().align(Align.center);
    
    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    
    stage.addActor(instance);
    instance.addAction(Actions.alpha(alpha, 0.3f));
  }
 
  
  public static void getEndGameDialog(BaseFSM.Events evt, String text, String text1, String score1, String score2, Stage stage) {
    getEndGameDialog(evt, text, text1, score1, score2, 1, stage);
  }
  public static void getEndGameDialog(BaseFSM.Events evt, String text, String text1, String score1, String score2, float alpha, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    
    float height = stage.getHeight()*0.6f;
    float width = stage.getWidth()*0.6f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add().expand();
    instance.add(text1).colspan(2).expand().align(Align.center);
    instance.add().expand();
    
    instance.row();
    instance.add().expand();
    instance.add("Overall Score " + text).colspan(2).expand().align(Align.center);
    instance.add().expand();
    instance.row();
    instance.add().expand();
    instance.add(score1).expand().align(Align.center);
    instance.add(score2).expand().align(Align.center);
    instance.add().expand();
    
    Table t1 = new Table();
    t1.row().expand().fill();
    t1.add();
    t1.add(instance.bContinue).colspan(2).fill().expand().height(height*0.15f).width(width/3);
    if ((MatchState.anScore[0]>=MatchState.nMatchTo||MatchState.anScore[1]>=MatchState.nMatchTo)&&(MatchState.matchType==0)) {
      t1.add();
      t1.add(instance.bExport).colspan(2).fill().expand().height(height*0.15f).width(width/3);
    }
    t1.add();
    instance.row();
    instance.add(t1).colspan(4).fill().padBottom(width/25);
    
    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }
  
  
  public static void getFlashDialog(BaseFSM.Events evt, String text, Stage stage) {
    getFlashDialog(evt, text, 1, stage);
  }
  public static void getFlashDialog(BaseFSM.Events evt, String text, float alpha, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.3f;
    float width = stage.getWidth()*0.75f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.add(instance.label).expand().align(Align.center);
    
    stage.addActor(instance);
    instance.addAction(MyActions.sequence(
        Actions.alpha(alpha, 0.3f),
        Actions.delay(1.5f),
        Actions.fadeOut(0.3f),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            instance.remove();
            GnuBackgammon.fsm.processEvent(instance.evt, true);
          }
        })
    ));
  }
  
  public static void getQuitDialog(Stage stage) {
    getQuitDialog(1, stage);
  }
  public static void getQuitDialog(float alpha, Stage stage) {
    instance.quitWindow = true;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.remove();
    instance.setText("Really quit the game?");
    
    float height = stage.getHeight()*0.4f;
    float width = stage.getWidth()*0.5f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);
    
    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    
    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }

  
  public static void getLeaveDialog(BaseFSM.Events evt, float alpha, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = true;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    
    instance.setText("You are leaving current match.");
    
    float height = stage.getHeight()*0.45f;
    float width = stage.getWidth()*0.6f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add(instance.label).colspan(7).expand().align(Align.center);
    instance.row().padTop(width/45);
    instance.add(new Label("Do you want to save it?", GnuBackgammon.skin)).colspan(7).expand().align(Align.center);
    
    instance.row().padTop(width/25);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height*0.25f).width(width/4.5f);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height*0.25f).width(width/4.5f);
    instance.add();
    instance.add(instance.bCancel).fill().expand().height(height*0.25f).width(width/4.5f);
    instance.add();
    
    instance.row().padBottom(width/35);
    instance.add();
    
    
    stage.addActor(instance);
    instance.addAction(MyActions.sequence(Actions.alpha(alpha, 0.3f)));
  }
  
  public static void getHelpDialog(Stage stage, Boolean cb) {
    getHelpDialog(1, stage, cb);
  }
  public static void getHelpDialog(float alpha, Stage stage, Boolean cb) {
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = false;
    instance.dicesWindow = false;
    instance.remove();
    Label l = new Label(
        "GAME TYPE\n" +
        "You can choose two game type, and several options:\n" +
        "Backgammon - usual starting position\n" +
        "Nackgammon - Nack's starting position, attenuates lucky starting roll\n" +
        "Doubling Cube: use or not the doubling cube, with or without Crawford rule\n\n" +
        "START TURN\n" +
        "If cube isn't available, dices are rolled automatically,\n" +
        "else you must click on 'Double' or 'Roll' button\n\n" +
        "MOVING MECHANIC\n" +
        "You can choose two moves mechanic (Options->Move Logic):\n" +
        "TAP - once you rolled dices, select the piece you would move.\n" +
        "If legal moves for that piece are available, they will be shown.\n" +
        "Click an available point and the piece will move there.\n" +
        "AUTO - click on a piece and it moves automatically to destination point.\n" +
        "Bigger dice is played first. You can change dice order clicking on dices\n\n" +
        "You can cancel your moves in current hand just clicking the UNDO button\n" +
        "in the game options menu popup.\n\n" +
        "END TURN\n" +
        "When you finish your turn, click again the dices to take back them and change turn.\n"
    , GnuBackgammon.skin);
    l.setWrap(true);
    
    ScrollPane sc = new ScrollPane(l,GnuBackgammon.skin);
    sc.setFadeScrollBars(false);
    sc.setOverscroll(false, false);
    
    float height = stage.getHeight()*0.85f;
    float width = stage.getWidth()*0.9f;
    
    instance.clear();
    instance.row().padTop(width/25);
    instance.add(sc).colspan(3).expand().fill().align(Align.center).padTop(width/25).padLeft(width/35).padRight(width/35);
    
    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height*0.15f).width(width/4);
    instance.add();
    
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }
  
  
  public static void getAboutDialog(Stage stage, Boolean cb) {
    getAboutDialog(1, stage, cb);
  }
  public static void getAboutDialog(float alpha, Stage stage, Boolean cb) {
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = false;
    instance.dicesWindow = false;
    instance.remove();
    
    final String gnuBgLink = "http://www.gnubg.org";
    final String gplLink = "http://www.gnu.org/licenses/gpl.html";
    final String githubLink1 = "https://github.com/alcacoop/it.alcacoop.gnubackgammon";
    final String githubLink2 = "https://github.com/alcacoop/libgnubg-android";
    final String wikipediaLink = "http://en.wikipedia.org/wiki/Backgammon#Rules";
    
    Table t = new Table();
    t.add(new Label("ABOUT BACKGAMMON MOBILE", GnuBackgammon.skin)).expand();
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("Backgammon Mobile is based on GNUBackgammon (gnubg)", GnuBackgammon.skin));
    Label link1 = new Label(gnuBgLink, GnuBackgammon.skin);
    link1.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(gnuBgLink);
      };
    });
    t.row();
    t.add(link1);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("Its source code is released under a GPLv3 License", GnuBackgammon.skin));
    Label link2 = new Label(gplLink, GnuBackgammon.skin);
    link2.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(gplLink);
      };
    });
    t.row();
    t.add(link2);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("and is available on GitHub at:", GnuBackgammon.skin));
    Label link3 = new Label(githubLink1, GnuBackgammon.skin);
    link3.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(githubLink1);
      };
    });
    Label link4 = new Label(githubLink2, GnuBackgammon.skin);
    link4.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(githubLink2);
      };
    });
    t.row();
    t.add(link3);
    t.row();
    t.add(link4);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("You can find a detailed description of game rules on Wikipedia:", GnuBackgammon.skin));
    Label link5 = new Label(wikipediaLink, GnuBackgammon.skin);
    link5.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.myRequestHandler.openURL(wikipediaLink);
      };
    });
    t.row();
    t.add(link5);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("If you enjoy our game support us rating on the Play Store", GnuBackgammon.skin));
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("Copyright 2012 - Alca Soc. Coop.", GnuBackgammon.skin));
    
    
    ScrollPane sc = new ScrollPane(t,GnuBackgammon.skin);
    sc.setFadeScrollBars(false);
    sc.setOverscroll(false, false);
    
    float height = stage.getHeight()*0.85f;
    float width = stage.getWidth()*0.95f;
    
    instance.clear();
    instance.row().padTop(width/25);
    instance.add(sc).colspan(3).expand().fill().align(Align.center).padTop(width/25).padLeft(width/35).padRight(width/35);

    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height*0.15f).width(width/4);
    instance.add();
    
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }
  
  
  public static void getOptionsDialog(Stage stage) {
    getOptionsDialog(1, stage);
  }
  public static void getOptionsDialog(float alpha, Stage stage) {
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = true;
    instance.dicesWindow = false;
    instance.remove();
    
    instance.opts.initFromPrefs();
    
    float width = stage.getWidth()*0.75f;
    float height = stage.getHeight()*0.92f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);

    instance.add(instance.opts).expand().fill();
    
    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }

  public static boolean isOpened() {
    return instance.hasParent();
  }
  public static void setButtonsStyle(String b) {
    instance.bContinue.setStyle(GnuBackgammon.skin.get("button-"+b, TextButtonStyle.class));
    instance.bYes.setStyle(GnuBackgammon.skin.get("button-"+b, TextButtonStyle.class));
    instance.bNo.setStyle(GnuBackgammon.skin.get("button-"+b, TextButtonStyle.class));
    instance.bCancel.setStyle(GnuBackgammon.skin.get("button-"+b, TextButtonStyle.class));
    instance.bExport.setStyle(GnuBackgammon.skin.get("button-"+b, TextButtonStyle.class));
    instance.opts.setButtonsStyle(b);
  }
  public static void getDicesDialog(Stage stage, Boolean cb) {
    getDicesDialog(1, stage, cb);
  }
  public static void getDicesDialog(float alpha, Stage stage, Boolean cb) {
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = false;
    instance.dicesWindow = true;
    instance.remove();
    
    float height = stage.getHeight()*0.85f;
    float width = stage.getWidth()*0.9f;
    
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    instance.clear();
    
    Table t = new Table();
    
    instance.add(t).fill().expand();
    float cellWidth = width/7.5f;
    float cellHeight = height/7.5f;
    
    TextButtonStyle ts = GnuBackgammon.skin.get("default", TextButtonStyle.class);
    for (int i=1; i<7; i++) {
      t.row().space(cellHeight*0.125f);
      for (int j=1; j<7; j++) {
         if (j<=i) {
           TextButton b = new TextButton(i+"x"+j, ts);
           b.addListener(cl);
           t.add(b).width(cellWidth).height(cellHeight).fill();
         }
      }
      if (i==1)
        t.add(new Label("Choose "+((GameScreen)GnuBackgammon.Instance.currentScreen).getCurrentPinfo()+" dices", GnuBackgammon.skin)).right().colspan(5);
    }
    
    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }
}
